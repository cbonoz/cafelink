package www.cafelink.com.cafelink.fragments.message

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.github.bassaer.chatmessageview.model.IChatUser
import net.idik.lib.slimadapter.SlimAdapter
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject
import com.github.bassaer.chatmessageview.view.ChatView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.gson.Gson
import timber.log.Timber
import www.cafelink.com.cafelink.models.Conversation
import www.cafelink.com.cafelink.models.MyIChatUser
import www.cafelink.com.cafelink.models.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_message_list.*
import www.cafelink.com.cafelink.CafeApplication.Companion.LAST_LOCATION_LOC
import www.cafelink.com.cafelink.activities.MainActivity
import www.cafelink.com.cafelink.fragments.MapsFragment
import www.cafelink.com.cafelink.models.cafe.Location
import www.cafelink.com.cafelink.util.PrefManager
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * View all the messages for a given currentConversation.
 * Each message is it's own thread, where the user can click one of two buttons for each row.
 *  View messages: view all the messages
 *  Reply: add a message to the thread
 *
 * Hitting back will take the user back to the maps fragment.
 */
class MessagesFragment : Fragment() {

    protected lateinit var mChatView: ChatView

    lateinit var adapter: SlimAdapter
    lateinit var layoutManager: LinearLayoutManager
//    val data: ArrayList<CafeMessage> = ArrayList<CafeMessage>()

    lateinit var recyclerView: RecyclerView

    lateinit var LEFT_ICON: Bitmap
    lateinit var RIGHT_ICON: Bitmap

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var prefManager: PrefManager
    @Inject
    lateinit var userSessionManager: UserSessionManager
    @Inject
    lateinit var datastore: Datastore

    lateinit var currentUser: User
    lateinit var currentIChatUser: IChatUser

    lateinit var loadingSpinner: SpinKitView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)

        LEFT_ICON = BitmapFactory.decodeResource(resources, R.drawable.face_1)
        RIGHT_ICON = BitmapFactory.decodeResource(resources, R.drawable.face_2)

        currentUser = userSessionManager.getLoggedInUser()
        currentIChatUser = currentUser.toIChatUser(RIGHT_ICON)
    }

    private lateinit var currentConversation: Conversation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_message_list, container, false)
        loadingSpinner = v.findViewById(R.id.loading_spinner)
        loadingSpinner.visibility = View.VISIBLE
        val args = arguments
        val conversationString = args?.getString(CafeApplication.CONVERSATION_DATA, null)
        if (conversationString == null) {
            Toast.makeText(activity, getString(R.string.conversation_fail), Toast.LENGTH_SHORT).show()
        } else {
            try {
                currentConversation = gson.fromJson(conversationString, Conversation::class.java)
                fetchMessagesForConversation(v, currentConversation)
                activity!!.title = "Messages: ${currentConversation.title}"
//                val headerText = v.findViewById<TextView>(R.id.headerText)
//                headerText.text = "Conversation: ${currentConversation.title}"

            } catch (e: Exception) {
                Toast.makeText(activity, getString(R.string.conversation_fail), Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentCafe = currentConversation.cafe
        if (currentCafe != null) {
            conversationHeaderView.setCafe(currentCafe)
            if (activity is MainActivity) {
                conversationHeaderView.setOnClickListener {
                    MaterialDialog.Builder(activity as Context)
                            .title("Go to Cafe")
                            .content("Clicking ok will find ${currentCafe.name} and open it back in the Map View")
                            .positiveText(R.string.ok)
                            .autoDismiss(false)
                            .onPositive { dialog, which ->
                                dialog.dismiss()
                                goToCafe(currentCafe.location)
                            }
                            .negativeText(R.string.cancel)
                            .onNegative { dialog, which ->
                                dialog.dismiss()
                            }
                            .show();
                }
            }
        } else {
            conversationHeaderView.visibility = GONE
        }

    }

    private fun goToCafe(location: Location) {
        prefManager.saveJson(LAST_LOCATION_LOC, location)
        (activity as MainActivity).replaceFragment(MapsFragment(), getString(R.string.title_explore))
    }

    fun fetchMessagesForConversation(v: View, conversation: Conversation) {
        Timber.d("fetchMessagesForConversation: %s", conversation)
        setupMessageList(v)
        mChatView.getMessageView().removeAll()
        datastore.messageDatabase.whereEqualTo("conversationId", conversation.id).orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Timber.e(firebaseFirestoreException, "error getting messages for conversationId: %s", conversation.id)
                        Toast.makeText(activity as Context, "Could not fetch messages, try again later", Toast.LENGTH_SHORT).show()
                    } else {
                        for (dc in snapshots!!.documentChanges) {
                            val docData = dc.document.data
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    // Append the entry to the conversation list view.
                                    val message = gson.fromJson(gson.toJson(docData), CafeMessage::class.java)
                                    addMessageToChatView(message, currentUser)
                                }
                                DocumentChange.Type.MODIFIED -> Timber.d("Modified message: %s", docData)
                                DocumentChange.Type.REMOVED -> Timber.d("Removed message: %s", docData)
                            }
                        }

                    }
                }
    }

    private var writing: Boolean = false

    fun setupMessageList(v: View) {
        mChatView = v.findViewById(R.id.chatMessageView)

        //Set UI parameters if you need
        val context = activity as Context
        mChatView.setRightBubbleColor(ContextCompat.getColor(context, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(context, R.color.blueGray500));
        mChatView.setSendButtonColor(ContextCompat.getColor(context, R.color.md_cyan_500));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("new message...");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);

        val currentUser = userSessionManager.getLoggedInUser()

        loadingSpinner.visibility = View.GONE
        mChatView.visibility = VISIBLE

        //Click Send Button
        mChatView.setOnClickSendButtonListener(View.OnClickListener {
            //new message
            val text = mChatView.inputText
            if (text.isEmpty()) {
                Toast.makeText(activity, "Message must not be empty", Toast.LENGTH_SHORT).show()
            } else if (!writing) {
                writing = true


                val cafeMessage = CafeMessage(
                        currentUser.name,
                        currentUser.id,
                        currentConversation.id,
                        text,
                        System.currentTimeMillis()
                )

                datastore.messageDatabase
                        .document(cafeMessage.id)
                        .set(cafeMessage)
                        .addOnSuccessListener {
                            Timber.d("Created message: $cafeMessage")
                            // Update the conversation participants list.
                            val updateMap = HashMap<String, Any>()
                            val userId = cafeMessage.userId
                            updateMap["participants.$userId"] = 1502144665L // used for index + orderBy (firestore specific).
                            updateMap["messageCount"] = currentConversation.messageCount + 1
                            val currentTime = System.currentTimeMillis()
                            updateMap["lastUpdated"] = currentTime
                            datastore.conversationDatabase
                                    .document(currentConversation.id)
                                    .update(updateMap)
                                    .addOnSuccessListener {
                                        Timber.d("Added message for user: $userId")
                                        currentConversation.messageCount += 1
                                        currentConversation.lastUpdated = currentTime
                                        // Reset edit text.
                                        mChatView.inputText = ""
                                        writing = false
                                    }
                                    .addOnFailureListener {
                                        Timber.e(it, "Could not create message")
                                        Toast.makeText(activity as Context, "Could not create message: ${it.message}", Toast.LENGTH_SHORT).show()
                                        writing = false
                                    }
                        }.addOnFailureListener {
                            Timber.e(it, "Could not create message")
                            Toast.makeText(activity as Context, "Could not create message: ${it.message}", Toast.LENGTH_SHORT).show()
                            writing = false
                        }
            } else {
                Toast.makeText(activity, getString(R.string.saving), Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun addMessageToChatView(it: CafeMessage, currentUser: User) {
        if (it.userId == currentUser.id) {
            val msg = it.toMessage(MyIChatUser(it.userId, "You", RIGHT_ICON), it.message, true)
            mChatView.send(msg)
        } else {
            val msg = it.toMessage(MyIChatUser(it.userId, it.userName, LEFT_ICON), it.message, false)
            mChatView.receive(msg)
        }
    }

    companion object {

        @JvmStatic
        fun makeMessageFragment(conversationString: String): MessagesFragment {
            val args = Bundle()
            args.putString(CafeApplication.CONVERSATION_DATA, conversationString)
            val messageFragment = MessagesFragment()
            messageFragment.setArguments(args)
            return messageFragment
        }
    }

}