package www.cafelink.com.cafelink.util

import www.cafelink.com.cafelink.BuildConfig
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.util.PrefManager
import java.util.*

class CafeService(private val prefManager: PrefManager) {

    val random = Random()

    fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    fun postMessageUrl(): String {
        return "${SERVER_URL}/api/message"
    }

    fun getCafeMessagesUrl(cafeId: String): String {
        return "${SERVER_URL}/api/cafe/messages/${cafeId}"
    }

    fun getUserMessagesUrl(userId: String): String {
        return "${SERVER_URL}/api/user/messages/${userId}"
    }

    fun getUserInfoUrl(userId: String): String {
        return "${SERVER_URL}/api/user/info/${userId}"
    }

    companion object {
        val SERVER_URL = ""
    }

}