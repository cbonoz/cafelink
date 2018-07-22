package www.cafelink.com.cafelink.fragments.conversation


import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.CafeApplication.Companion.CAFE_DATA

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.Conversation
import www.cafelink.com.cafelink.models.cafe.Data
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import java.util.*
import javax.inject.Inject


/**
 * View all the conversations for a given cafe.
 * User can start a new conversation, view existing conversations, and contribute to existing conversations.
 * Each conversation is it's own thread, where the user can click  a conversation
 *  View messages: view all the messages
 *  Reply: add a message to the thread
 *
 * Hitting back will take the user back to the maps fragment.
 */
class CafeConversationFragment : AbstractConversationFragment() {

    @Inject
    lateinit var userSessionManager: UserSessionManager
    @Inject
    lateinit var datastore: Datastore

    lateinit var currentCafe: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
        val args = arguments
        val cafeDataString: String = args!!.getString(CAFE_DATA)
        currentCafe = gson.fromJson(cafeDataString, Data::class.java)
        Timber.d("currentCafe: $currentCafe")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_cafe_conversation, container, false)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
            layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        }
        setupConversationList(v, recyclerView)
        setupConversationFab(v, currentCafe)
        fetchConversationsForCafe(currentCafe.id)
        val conversationHeader = v.findViewById<TextView>(R.id.conversationHeaderText)
        conversationHeader.text = currentCafe.name
        return v
    }

    private fun setupConversationFab(v: View, currentCafe: Data) {
        val fab = v.findViewById<FloatingActionButton>(R.id.fab_new_conversation)
        fab.setOnClickListener { view ->
            startNewConversationDialog(currentCafe)
        }
    }

    private fun startNewConversationDialog(currentCafe: Data) {
        MaterialDialog.Builder(activity as Context)
                .title("Create New Conversation Thread")
                .content("Enter the title for a new conversation at: ${currentCafe.name}")
                .autoDismiss(false)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.input_hint, R.string.input_prefill) { dialog, input ->
                    val conversationTitle = input.toString()
                    if (conversationTitle.isBlank()) {
                        Toast.makeText(activity as Context, "Thread title must not be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        // Successful - create the conversation thread.
                        val user = userSessionManager.getLoggedInUser()
                        val conversation = Conversation(
                                UUID.randomUUID().toString(),
                                conversationTitle,
                                user,
                                mapOf(Pair(user.getId(), true)),
                                currentCafe.id,
                                System.currentTimeMillis()
                        )

                        Timber.d("Writing new conversation: ${conversation.id}")
                        datastore.writeConversation(conversation, OnSuccessListener {
                            Timber.d("Created conversation: $conversation")
                            dialog.dismiss()
                        }, OnFailureListener {
                            Timber.e(it, "Could not create conversation")
                            Toast.makeText(activity as Context, "Could not create conversation: ${it.message}", Toast.LENGTH_SHORT).show()
                        })
                    }
                }.show()
    }

    private fun fetchConversationsForCafe(cafeId: String) {
        datastore.conversationDatabase.whereEqualTo("cafeId" ,cafeId).orderBy("lastUpdated")
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {

                    }

        })
    }

}
