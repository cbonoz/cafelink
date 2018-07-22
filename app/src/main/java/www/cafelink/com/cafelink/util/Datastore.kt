package www.cafelink.com.cafelink.util

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import www.cafelink.com.cafelink.models.CafeMessage
import www.cafelink.com.cafelink.models.Conversation

//https://firebase.google.com/docs/firestore/solutions/arrays
//https://firebase.google.com/docs/firestore/query-data/get-data
class Datastore(val gson: Gson) {

//    val database = FirebaseDatabase.getInstance()
    val database = FirebaseFirestore.getInstance();
    // Contains 'Conversation' class objects as json (with id index).
    val conversationDatabase = database.collection("conversations") // conversations
    // Contains 'CafeMessage' class objects as json (with id index).
    val messageDatabase = database.collection("messages") // messages for each conversation
    // Contains 'User' class objects as json (with userId index).
    val userDatabase = database.collection("users") // users in the app


    fun writeMessage(cafeMessage: CafeMessage, listener: OnSuccessListener<in Void>, failureListener: OnFailureListener) {
        val cafeString = gson.toJson(cafeMessage)
        val cafeMap: Map<String, Any> = gson.fromJson(cafeString, object : TypeToken<Map<String, Any>>(){}.type)
        messageDatabase.document(cafeMessage.id).set(cafeMap).addOnSuccessListener(listener).addOnFailureListener(failureListener)
    }

    fun writeConversation(conversation: Conversation, listener: OnSuccessListener<in Void>, failureListener: OnFailureListener) {
        val conversationString = gson.toJson(conversation)
        val conversationMap: Map<String, Any> = gson.fromJson(conversationString, object : TypeToken<Map<String, Any>>(){}.type)
    }
}