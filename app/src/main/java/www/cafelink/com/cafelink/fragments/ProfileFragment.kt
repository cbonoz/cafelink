package www.cafelink.com.cafelink.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import www.cafelink.com.cafelink.CafeApplication

import www.cafelink.com.cafelink.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CafeApplication.injectionComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        fetchUserInfo("")
        return v
    }

    private fun fetchUserInfo(userId: String) {
        datastore.userDatabase.child("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        cafeService.getUserInfoUrl(userId = userSessionManager.getLoggedInUserId())
    }


}
