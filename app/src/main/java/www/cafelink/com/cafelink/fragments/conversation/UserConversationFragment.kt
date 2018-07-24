package www.cafelink.com.cafelink.fragments.conversation


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
 * Contains the history of all messages/threads the user has participated in.
 */
class UserConversationFragment : AbstractConversationFragment() {

    @Inject
    lateinit var datastore: Datastore
    @Inject
    lateinit var userSessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_user_conversation, container, false)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
            this.layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        }
        setupConversationList(v)
        val userId = userSessionManager.getLoggedInUser().id
        fetchConversationsForUser(userId)
        val conversationHeader = v.findViewById<TextView>(R.id.conversationHeaderText)
        conversationHeader.text = getString(R.string.your_conversations)
        return v
    }

    private fun fetchConversationsForUser(userId: String) {
        val key = "participants.$userId"
        datastore.conversationDatabase.whereGreaterThan(key, 0).orderBy(key).orderBy("lastUpdated", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Timber.e(firebaseFirestoreException, "error getting conversations for userId: %s", userId)
                    } else {
                        for (dc in snapshots!!.documentChanges) {
                            val docData = dc.document.data
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    // Append the entry to the conversation list view.
                                    val conversation = gson.fromJson(gson.toJson(docData), Conversation::class.java)
                                    data.add(conversation)
                                    adapter.updateData(data)
                                    adapter.notifyItemChanged(data.size - 1)
                                }
                                DocumentChange.Type.MODIFIED -> Timber.d("Modified conversation: %s", docData)
                                DocumentChange.Type.REMOVED -> Timber.d("Removed conversation: %s", docData)
                            }
                        }
                    }
                }
    }

}
