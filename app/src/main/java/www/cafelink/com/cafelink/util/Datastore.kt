package www.cafelink.com.cafelink.util

import com.google.firebase.database.FirebaseDatabase
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.models.Conversation


class Datastore() {

    val database = FirebaseDatabase.getInstance()
    // Contains 'Conversation' class objects as json (with id index).
    val conversationDatabase = database.getReference("conversations") // conversations
    // Contains 'CafeMessage' class objects as json (with id index).
    val messageDatabase = database.getReference("messages") // messages for each conversation
    // Contains 'User' class objects as json (with userId index).
    val userDatabase =  database.getReference("users") // users in the app


    fun writeMessage(cafeMessage: CafeMessage) {
        messageDatabase.child(cafeMessage.id).setValue(cafeMessage)
    }

    fun writeConversation(conversation: Conversation) {
        conversationDatabase.child(conversation.id).setValue(conversation)
    }

}