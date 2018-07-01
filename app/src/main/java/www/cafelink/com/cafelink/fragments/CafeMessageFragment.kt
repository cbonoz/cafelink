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
import com.github.kittinunf.fuel.httpGet
import net.idik.lib.slimadapter.SlimAdapter
import www.cafelink.com.cafelink.CafeApplication

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.CafeService
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject


/**
 * View all the messages for a given cafe.
 * Each message is it's own thread, where the user can click one of two buttons for each row.
 *  View messages: view all the messages
 *  Reply: add a message to the thread
 *
 * Hitting back will take the user back to the maps fragment.
 */
class CafeMessageFragment : Fragment() {

    private lateinit var adapter: SlimAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val data: List<CafeMessage> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var cafeService: CafeService
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
        fetchMessages()
        return v
    }

    private fun fetchMessages() {
//        cafeService.getCafeMessagesUrl(cafeId = "").httpGet()
        adapter.updateData(data)
        adapter.notifyDataSetChanged()
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