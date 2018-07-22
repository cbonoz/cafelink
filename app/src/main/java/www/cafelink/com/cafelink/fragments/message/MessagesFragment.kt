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
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.github.bassaer.chatmessageview.model.IChatUser
import net.idik.lib.slimadapter.SlimAdapter
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject
import com.github.bassaer.chatmessageview.view.ChatView
import com.google.gson.Gson
import timber.log.Timber
import www.cafelink.com.cafelink.models.Conversation
import www.cafelink.com.cafelink.models.MyIChatUser
import www.cafelink.com.cafelink.models.User
import java.util.*
import com.google.firebase.firestore.DocumentChange
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
    val data: ArrayList<CafeMessage> = ArrayList<CafeMessage>()

    lateinit var recyclerView: RecyclerView

    lateinit var LEFT_ICON: Bitmap
    lateinit var RIGHT_ICON: Bitmap

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var userSessionManager: UserSessionManager
    @Inject
    lateinit var datastore: Datastore

    lateinit var currentUser: User
    lateinit var currentIChatUser: IChatUser

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
        val args = arguments
        val conversationString = args?.getString(CafeApplication.CONVERSATION_DATA, null)
        if (conversationString == null) {
            Toast.makeText(activity, getString(R.string.conversation_fail), Toast.LENGTH_SHORT).show()
        } else {
            try {
                currentConversation = gson.fromJson(conversationString, Conversation::class.java)
                fetchMessagesForConversation(v, currentConversation)
                val messageHeader = v.findViewById<TextView>(R.id.messageHeaderText)
                messageHeader.text = currentConversation.title

            } catch (e: Exception) {
                Toast.makeText(activity, getString(R.string.conversation_fail), Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }

    fun fetchMessagesForConversation(v: View?, conversation: Conversation) {
        Timber.d("fetchMessagesForConversation: %s", conversation)
        data.clear()
        datastore.messageDatabase.whereEqualTo("conversationId", conversation.id).orderBy("lastUpdated")
                .addSnapshotListener { snapshots, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Timber.e(firebaseFirestoreException, "error getting messages for conversationId: %s", conversation.id)
                    } else {
                        for (dc in snapshots!!.getDocumentChanges()) {
                            val docData = dc.document.data
                            when (dc.getType()) {
                                DocumentChange.Type.ADDED -> {
                                    // Append the entry to the conversation list view.
                                    val message = gson.fromJson(gson.toJson(docData), CafeMessage::class.java)
                                    data.add(message)
                                    adapter.updateData(data)
                                    adapter.notifyItemChanged(data.size - 1)
                                }
                                DocumentChange.Type.MODIFIED -> Timber.d("Modified message: %s", docData)
                                DocumentChange.Type.REMOVED -> Timber.d("Removed message: %s", docData)
                            }
                        }
                    }
                }
    }

    private var writing: Boolean = false

    fun setupMessageList(v: View, cafeMessages: List<CafeMessage>) {
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

        // Populate the existing cafeMessages list.
        cafeMessages.map {
            if (it.userId == currentUser.id) {
                val msg = it.toMessage(MyIChatUser(it.userId, it.userName, RIGHT_ICON), it.message, true)
                mChatView.send(msg)
            } else {
                val msg = it.toMessage(MyIChatUser(it.userId, it.userName, LEFT_ICON), it.message, false)
                mChatView.receive(msg)
            }
        }

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
                        text
                )

                datastore.messageDatabase
                        .document(cafeMessage.id)
                        .set(cafeMessage)
                        .addOnSuccessListener {
                            Timber.d("Created message: $cafeMessage")
                            // Update the conversation participants list.
                            val updateMap = HashMap<String, Any>()
                            updateMap["participants.${cafeMessage.userId}"] = true
                            updateMap["messageCount"] = currentConversation.messageCount + 1
                            datastore.conversationDatabase
                                    .document(currentConversation.id)
                                    .update(updateMap)
                                    .addOnSuccessListener {
                                        Timber.d("Added message for user: ${cafeMessage.userId}")
                                        // Send to chat view.
                                        val msg = cafeMessage.toMessage(currentIChatUser, text, true)
                                        mChatView.send(msg)
                                        // Reset edit text.
                                        mChatView.inputText = ""
                                        currentConversation.messageCount += 1
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