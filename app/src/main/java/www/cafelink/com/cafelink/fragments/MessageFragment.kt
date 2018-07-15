package www.cafelink.com.cafelink.fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import net.idik.lib.slimadapter.SlimAdapter
import www.cafelink.com.cafelink.CafeApplication

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.CafeService
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject


/**
 * Contains the history of all messages/threads the user has participated in.
 */
class MessageFragment : Fragment() {

    private lateinit var adapter: SlimAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val data: List<CafeMessage> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var cafeService: CafeService
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
        val v = inflater.inflate(R.layout.fragment_message, container, false)
        setupMessageList(v)
        fetchMessagesForConversation()
        return v
    }

    private fun fetchMessagesForConversation() {
        datastore.conversationDatabase.child("conversationId").equalTo(userSessionManager.getLoggedInUserId()).orderByChild("lastUpdated").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                adapter.updateData(data)
                adapter.notifyDataSetChanged()
            }

        })

        }

    private fun setupMessageList(v: View) {
        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
            this.layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        }
        adapter = SlimAdapter.create()
                .register<CafeMessage>(R.layout.item_message) { data, injector ->
                    injector.text(R.id.name, data.message)
                            .text(R.id.age, data.timestamp.toString())
                            .clicked(R.id.messageLayout) {
                                Toast.makeText(activity, "clicked message: ${data.id}", Toast.LENGTH_LONG).show()
                            }
                }
                .attachTo(recyclerView)

    }


}
