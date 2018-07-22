package www.cafelink.com.cafelink.models

import com.github.bassaer.chatmessageview.model.Message

data class CafeMessage(val message: Message, val userId: String, val conversationId: String) {

    override fun toString(): String {
        return "CafeMessage(message=$message, userId='$userId', conversationId='$conversationId')"
    }
}
