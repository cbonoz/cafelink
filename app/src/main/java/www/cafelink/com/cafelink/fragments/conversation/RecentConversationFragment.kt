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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import timber.log.Timber
import www.cafelink.com.cafelink.CafeApplication

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.Conversation
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject


/**
 * View recent conversations
 * User can start a new conversation, view existing conversations, and contribute to existing conversations.
 * Each conversation is it's own thread, where the user can click  a conversation
 *  View messages: view all the messages
 *  Reply: add a message to the thread
 *
 * Hitting back will take the user back to the maps fragment.
 */
class RecentConversationFragment : AbstractConversationFragment() {

    @Inject
    lateinit var userSessionManager: UserSessionManager
    @Inject
    lateinit var datastore: Datastore

    private lateinit var noConversationsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_recent_conversation, container, false)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
            layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        }

        activity!!.title = "What's Happening"

        noConversationsText = v.findViewById(R.id.noConversationsText)
        setupConversationList(v)
        fetchConversationsForCafe()
        return v
    }

    private fun fetchConversationsForCafe() {
        data.clear()
        datastore.conversationDatabase.orderBy("lastUpdated", Query.Direction.DESCENDING).limit(10)
                .addSnapshotListener { snapshots, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Timber.e(firebaseFirestoreException, "error getting recent conversations")
                    } else {
                        var noConversations = true
                        for (dc in snapshots!!.documentChanges) {
                            val docData = dc.document.data
                            when (dc.getType()) {
                                DocumentChange.Type.ADDED -> {
                                    // Append the entry to the conversation list view.
                                    val conversation = gson.fromJson(gson.toJson(docData), Conversation::class.java)
                                    noConversations = false
                                    data.add(conversation)
                                    adapter.updateData(data)
                                    adapter.notifyItemChanged(data.size - 1)
                                }
                                DocumentChange.Type.MODIFIED -> Timber.d("Modified conversation: %s", docData)
                                DocumentChange.Type.REMOVED -> Timber.d("Removed conversation: %s", docData)
                            }
                        }

                        if (noConversations) {
                            noConversationsText.visibility = View.VISIBLE
                        } else {
                            noConversationsText.visibility = View.GONE
                        }
                    }
                }
    }

}
