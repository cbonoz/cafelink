package www.cafelink.com.cafelink.models

import com.github.bassaer.chatmessageview.model.IChatUser
import com.github.bassaer.chatmessageview.model.Message
import java.util.*

/*
 *
 */
data class CafeMessage(val userName: String, val userId: String, val conversationId: String, val message: String, val createdAt: Long, val id: String = UUID.randomUUID().toString()) {

    override fun toString(): String {
        return "CafeMessage(message=$message, id='$userId', conversationId='$conversationId')"
    }


    fun toMessage(user: IChatUser, text: String, rightSide: Boolean = true): Message {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = createdAt
        return Message.Builder()
                .setUser(user)
                .setSendTime(calendar)
                .setRight(rightSide)
                .setText(text)
                .hideIcon(true)
                .build()
    }
}
