package www.cafelink.com.cafelink.models

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser
import java.util.*

class User(val name: String, val id: String = UUID.randomUUID().toString()) {

    fun toIChatUser(bitmap: Bitmap): IChatUser {
        return MyIChatUser(id, name, bitmap)
    }


}