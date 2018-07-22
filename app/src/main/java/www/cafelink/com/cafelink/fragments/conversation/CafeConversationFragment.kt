package www.cafelink.com.cafelink.fragments.conversation


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import timber.log.Timber
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.CafeApplication.Companion.CAFE_DATA

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.cafe.Data
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
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
        fetchConversationsForCafe(currentCafe.id)
        val conversationHeader = v.findViewById<TextView>(R.id.conversationHeaderText)
        conversationHeader.text = currentCafe.name
        return v
    }

    private fun fetchConversationsForCafe(cafeId: String) {
        datastore.conversationDatabase.child("cafeId").equalTo(cafeId).orderByChild("lastUpdated").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Timber.d("onCancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                adapter.updateData(data)
                adapter.notifyDataSetChanged()
            }

        })
    }

}
