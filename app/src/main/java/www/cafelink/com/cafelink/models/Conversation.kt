package www.cafelink.com.cafelink.models

data class Conversation(val id: String,
                        val title: String,
                        val createdBy: User,
                        val participants: Map<String, Long>,
                        val cafeId: String,
                        val lastUpdated: Long,
                        var messageCount: Int = 0) {

    override fun toString(): String {
        return "Conversation(id='$id', title='$title', createdBy=$createdBy, participants=$participants, cafeId='$cafeId', lastUpdated=$lastUpdated)"
    }
}
