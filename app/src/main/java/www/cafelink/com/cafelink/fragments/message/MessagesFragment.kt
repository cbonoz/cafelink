package www.cafelink.com.cafelink.fragments.message

import android.content.Context
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
import com.github.bassaer.chatmessageview.model.Message
import net.idik.lib.slimadapter.SlimAdapter
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject
import com.github.bassaer.chatmessageview.view.ChatView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import timber.log.Timber
import www.cafelink.com.cafelink.models.Conversation
import www.cafelink.com.cafelink.models.User


/**
 * View all the messages for a given conversation.
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
    val data: List<CafeMessage> = ArrayList()

    lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var userSessionManager: UserSessionManager
    @Inject
    lateinit var datastore: Datastore

    lateinit var me: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
        me = userSessionManager.getLoggedInUser()
    }

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
                val conversation = gson.fromJson(conversationString, Conversation::class.java)
                fetchMessagesForConversation(v, conversation)
                val messageHeader = v.findViewById<TextView>(R.id.messageHeaderText)
                messageHeader.text = conversation.title

            } catch (e: Exception) {
                Toast.makeText(activity, getString(R.string.conversation_fail), Toast.LENGTH_SHORT).show()
            }
        }
        return v
    }

    fun fetchMessagesForConversation(v: View?, conversation: Conversation) {
        Timber.d("fetchMessagesForConversation: %s", conversation)
        datastore.conversationDatabase.child("conversationId").equalTo(conversation.id).orderByChild("lastUpdated").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.d("onCancelled")
                setupMessageList(v!!, ArrayList())
            }

            override fun onDataChange(p0: DataSnapshot) {
                Timber.d("onData: ${p0}")
//                adapter.updateData(data)
//                adapter.notifyDataSetChanged()
                setupMessageList(v!!, ArrayList())

                // TODO: set the conversation message component with the retrieved messages.

            }

        })

    }

    fun setupMessageList(v: View, messages: List<Message>) {
//        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
//            this.layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
//        }
//        adapter = SlimAdapter.create()
//                .register<CafeMessage>(R.layout.item_message) { data, injector ->
//                    injector.text(R.id.name, data.message)
//                            .text(R.id.age, data.timestamp.toString())
//                            .clicked(R.id.messageLayout) {
//                                Toast.makeText(activity, "clicked message: ${data.id}", Toast.LENGTH_LONG).show()
//                            }
//                }
//                .attachTo(recyclerView)

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

        // Populate the existing messages list.
        messages.map {
            if (it.user.getId() == currentUser.getId()) {
                mChatView.send(it)
            } else {
                mChatView.receive(it)
            }
        }

        mChatView.visibility = VISIBLE

        //Click Send Button
        mChatView.setOnClickSendButtonListener(View.OnClickListener {
            //new message
            val text = mChatView.inputText
            if (text.isEmpty()) {
                Toast.makeText(activity, "Message must not be empty", Toast.LENGTH_SHORT).show()
            } else {
                val message = Message.Builder()
                        .setUser(me)
                        .setRight(true)
                        .setText(text)
                        .hideIcon(true)
                        .build()
                //Set to chat view
                // TODO: make firebase append call before completing the send operation below.
                mChatView.send(message)
                //Reset edit text
                mChatView.inputText = ""
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