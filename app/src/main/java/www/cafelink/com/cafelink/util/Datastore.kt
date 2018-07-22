package www.cafelink.com.cafelink.util

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson

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

    fun fetchCafeConversations(cafeId: String, listener: OnCompleteListener<QuerySnapshot>) {
        conversationDatabase.whereEqualTo("cafeId", cafeId)
                .orderBy("lastUpdated")
                .get()
                .addOnCompleteListener(listener)
    }

    fun fetchUserConversations(userId: String, listener: OnCompleteListener<QuerySnapshot>) {
        conversationDatabase.whereEqualTo("participants.$userId", true)
                .orderBy("lastUpdated")
                .get()
                .addOnCompleteListener(listener)
    }
}