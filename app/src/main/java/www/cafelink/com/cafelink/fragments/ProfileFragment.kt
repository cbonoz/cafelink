package www.cafelink.com.cafelink.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import www.cafelink.com.cafelink.CafeApplication

import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.models.User
import www.cafelink.com.cafelink.util.CafeService
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager
import javax.inject.Inject

/**
 * Fragment containing user information for the current app user.
 */
class ProfileFragment : Fragment() {

    @Inject
    lateinit var cafeService: CafeService
    @Inject
    lateinit var datastore: Datastore
    @Inject
    lateinit var userSessionManager: UserSessionManager

    private lateinit var me: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        me = userSessionManager.getLoggedInUser()
        val userText = v.findViewById<TextView>(R.id.userTextValue)
        userText.text = me.userName
        return v
    }

}
