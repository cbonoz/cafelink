package www.cafelink.com.cafelink.util

import com.google.firebase.database.FirebaseDatabase


class Datastore() {

    val database = FirebaseDatabase.getInstance()
    val conversationDatabase = database.getReference("conversations") // conversations
    val messageDatabase = database.getReference("messages") // messages for each conversation
    val userDatabase =  database.getReference("users") // users in the app

}