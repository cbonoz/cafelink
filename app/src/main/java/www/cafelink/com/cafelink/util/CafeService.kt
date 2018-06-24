package www.cafelink.com.cafelink.util

import www.cafelink.com.cafelink.BuildConfig
import www.cafelink.com.cafelink.util.PrefManager
import java.util.*

class CafeService(private val prefManager: PrefManager) {

    val random = Random()

    fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }

    companion object {
        val SERVER_URL = ""
    }

}