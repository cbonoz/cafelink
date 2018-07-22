package www.cafelink.com.cafelink.fragments.conversation


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import timber.log.Timber
import www.cafelink.com.cafelink.CafeApplication

import www.cafelink.com.cafelink.R
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
        val v = inflater.inflate(R.layout.fragment_conversation, container, false)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
            this.layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        }
        setupConversationList(v, recyclerView)
        val userId = userSessionManager.getLoggedInUser().getId()
        fetchConversationsForUser(userId)
        val conversationHeader = v.findViewById<TextView>(R.id.conversationHeaderText)
        conversationHeader.text = getString(R.string.your_conversations)
        return v
    }

    private fun fetchConversationsForUser(userId: String) {
        // TODO: use participants
//        datastore.conversationDatabase.child("userId").equalTo(userId).orderByChild("lastUpdated").addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Timber.d("onCancelled")
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                Timber.d("onData: ${p0}")
//                adapter.updateData(data)
//                adapter.notifyDataSetChanged()
//            }
//
//        })
    }

}
