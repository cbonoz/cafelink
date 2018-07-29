package www.cafelink.com.cafelink.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.fragments.MapsFragment
import www.cafelink.com.cafelink.fragments.ProfileFragment
import www.cafelink.com.cafelink.fragments.conversation.RecentConversationFragment
import www.cafelink.com.cafelink.fragments.conversation.UserConversationFragment

class MainActivity : AppCompatActivity() {

    private var lastFragmentSelected = ""

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_explore -> {
                replaceFragment(MapsFragment(), getString(R.string.title_explore))
            }
            R.id.navigation_recent -> {
                replaceFragment(RecentConversationFragment(), getString(R.string.title_recent))
            }
            R.id.navigation_messages -> {
                replaceFragment(UserConversationFragment(), getString(R.string.title_messages))
            }
            R.id.navigation_profile -> {
                replaceFragment(ProfileFragment(), getString(R.string.title_profile))
            }
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(MapsFragment(), getString(R.string.title_explore))
    }

    fun replaceFragment(fragment: Fragment, fragmentTitle: String) {
//        if (fragmentTitle == lastFragmentSelected) {
//            return
//        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        title = fragmentTitle
        transaction.addToBackStack(null).commit()
        lastFragmentSelected = fragmentTitle
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
