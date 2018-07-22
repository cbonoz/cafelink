package www.cafelink.com.cafelink.models

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser

class MyIChatUser(val userId: String, val userName: String?, var bitmap: Bitmap?) : IChatUser {

    override fun getIcon(): Bitmap? {
        return bitmap
    }

    override fun getId(): String {
        return userId
    }

    override fun getName(): String? {
        return userName
    }

    override fun setIcon(bmp: Bitmap) {
        bitmap = bmp
    }

}
