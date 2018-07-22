package www.cafelink.com.cafelink.models

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser


class ChatUser(val userName: String, val userId: String, var iconBitmap: Bitmap? = null, var iconUrl: String = "") : IChatUser {
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