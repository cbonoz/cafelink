package www.cafelink.com.cafelink.models

import android.graphics.Bitmap
import com.github.bassaer.chatmessageview.model.IChatUser

class User(val userId: String, val userName: String, val iconUrl: String) : IChatUser {
    override fun getIcon(): Bitmap? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIcon(bmp: Bitmap) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getId(): String {
        return userId
    }

    override fun getName(): String? {
        return userName
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}