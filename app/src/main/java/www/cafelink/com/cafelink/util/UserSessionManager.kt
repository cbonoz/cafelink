package www.cafelink.com.cafelink.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.gson.Gson
import www.cafelink.com.cafelink.CafeApplication.Companion.app
import www.cafelink.com.cafelink.activities.SplashActivity
import www.cafelink.com.cafelink.models.User
import java.util.*

class UserSessionManager(private val prefManager: PrefManager, private val gson: Gson) {

    // TODO: replace with auth (deviceId ok for demo uniqueness for now, each device will be unique user).
    fun setLoggedInUser(context: Context, user: User) {
        prefManager.saveString(USER_ID_LOCATION, gson.toJson(user))
    }

    fun hasLoggedInUser(): Boolean {
        val userString = prefManager.getString(USER_ID_LOCATION, "")
        return !userString.isNullOrBlank()
//        try {
//        } catch (e: Exception) {
//            prefManager.clear(USER_ID_LOCATION)
//            Toast.makeText(app, "Session Expired, please reregister", Toast.LENGTH_LONG).show()
//            val intent = Intent(app, SplashActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            app!!.startActivity(intent)
//        }
    }

    fun getLoggedInUser(): User {
        val userString = prefManager.getString(USER_ID_LOCATION, "")
        return gson.fromJson(userString, User::class.java)
    }

    val random = Random()

    companion object {
        val SERVER_URL = ""
        val USER_ID_LOCATION = "user_id"
    }

}