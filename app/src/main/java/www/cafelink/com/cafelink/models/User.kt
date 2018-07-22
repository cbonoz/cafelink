package www.cafelink.com.cafelink.models

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser
import java.util.*

class User(var userName: String, var userId: String = UUID.randomUUID().toString(), var iconBitmap: Bitmap? = null, var iconUrl: String = "") : IChatUser {
    override fun getIcon(): Bitmap? {
        return iconBitmap
    }

    override fun setIcon(bmp: Bitmap) {
        iconBitmap = bmp
    }

    override fun getId(): String {
        return userId
    }

    override fun getName(): String? {
        return userName
    }


}