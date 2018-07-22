package www.cafelink.com.cafelink.models

import java.util.*

data class Conversation(val id: String,
                        val title: String,
                        val createdBy: User,
                        val participants: Map<String, Boolean>,
                        val cafeId: String,
                        val lastUpdated: Long) {

    override fun toString(): String {
        return "Conversation(id='$id', title='$title', createdBy=$createdBy, participants=$participants, cafeId='$cafeId', lastUpdated=$lastUpdated)"
    }
}
