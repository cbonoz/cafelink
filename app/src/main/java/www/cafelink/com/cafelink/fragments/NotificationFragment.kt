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
import net.idik.lib.slimadapter.SlimAdapter

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.CafeNotification


/**
 * A simple [Fragment] subclass.
 *
 */
class NotificationFragment : Fragment() {

    private lateinit var adapter: SlimAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val data: List<CafeNotification> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_notification, container, false)
        setupNotificationList(v)
        fetchNotifications()
        return v
    }

    private fun fetchNotifications() {
        adapter.updateData(data)
        adapter.notifyDataSetChanged()
    }

    private fun setupNotificationList(v: View) {
        recyclerView = v.findViewById<RecyclerView>(R.id.recyler_view).apply {
            this.layoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        }
        adapter = SlimAdapter.create()
                .register<CafeNotification>(R.layout.item_notification) { data, injector ->
                    injector.text(R.id.name, data.message)
                            .text(R.id.age, data.timestamp.toString())
                            .clicked(R.id.notificationLayout) {
                                Toast.makeText(activity, "clicked notification: ${data.id}", Toast.LENGTH_LONG).show()
                            }
                }
                .attachTo(recyclerView)

    }


}
