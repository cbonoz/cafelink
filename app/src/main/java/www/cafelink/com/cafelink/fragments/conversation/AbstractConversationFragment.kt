package www.cafelink.com.cafelink.fragments.conversation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import net.idik.lib.slimadapter.SlimAdapter
import timber.log.Timber
import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.fragments.message.MessagesFragment
import www.cafelink.com.cafelink.models.Conversation
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
abstract class AbstractConversationFragment : Fragment() {

    protected lateinit var adapter: SlimAdapter
    protected lateinit var layoutManager: LinearLayoutManager
    protected val data: ArrayList<Conversation> = ArrayList()

    protected lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
    }

    fun setupConversationList(v: View) {
        val sdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss aa", Locale.getDefault())
        adapter = SlimAdapter.create()
                .register<Conversation>(R.layout.item_conversation) { data, injector ->
                    val lastUpdatedDate = sdf.format(Date(data.lastUpdated))
                    injector.text(R.id.title, data.title)
                            .text(R.id.lastUpdated, "Last Updated: $lastUpdatedDate")
                            .text(R.id.messageCountText, "${data.messageCount}")
                            .clicked(R.id.messageLayout) {
                                Toast.makeText(activity, "clicked message: ${data.id}", Toast.LENGTH_LONG).show()
                                Timber.d("Clicked conversation: $data")
                                val conversationString = gson.toJson(data)
                                val messageFragment = MessagesFragment.makeMessageFragment(conversationString)
                                fragmentManager!!.beginTransaction()
                                        .replace(R.id.fragment_container, messageFragment)
                                        .commit()
                            }
                }
                .attachTo(this.recyclerView)

    }

}
