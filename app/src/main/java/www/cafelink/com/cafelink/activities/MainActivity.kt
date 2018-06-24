package www.cafelink.com.cafelink.activities

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import www.cafelink.com.cafelink.R
import www.cafelink.com.cafelink.fragments.MapsFragment
import www.cafelink.com.cafelink.fragments.NotificationFragment
import www.cafelink.com.cafelink.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private var lastFragmentSelected: String = ""

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_explore -> {
                replaceFragment(MapsFragment(), getString(R.string.title_explore))
            }
            R.id.navigation_notifications -> {
                replaceFragment(NotificationFragment(), getString(R.string.title_notifications))
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

    private fun replaceFragment(fragment: Fragment, fragmentTitle: String) {
        if (fragmentTitle == lastFragmentSelected) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        setTitle(fragmentTitle)
        transaction.commit()
        lastFragmentSelected = fragmentTitle
    }
}
