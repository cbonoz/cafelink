package www.cafelink.com.cafelink.util

import android.content.Context
import android.provider.Settings
import www.cafelink.com.cafelink.BuildConfig
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.PrefManager
import java.util.*

class UserSessionManager(private val prefManager: PrefManager) {

    // TODO: replace with auth (deviceId ok for demo uniqueness for now, each device will be unique user).
    fun setLoggedInUserId(context: Context) {
        val s = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        prefManager.saveString(USER_ID_LOCATION, s)
    }

    fun getLoggedInUserId(): String {
        return prefManager.getString(USER_ID_LOCATION, "")!!
    }

    val random = Random()

    companion object {
        val SERVER_URL = ""
        val USER_ID_LOCATION = "user_id"
    }

}