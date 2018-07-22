package www.cafelink.com.cafelink.models

data class Conversation(val id: String = "",
                        val title: String = "",
                        val createdBy: User? = null,
                        val participants: Map<String, Boolean> = mapOf(),
                        val cafeId: String = "",
                        val lastUpdated: Long = 0) {

    override fun toString(): String {
        return "Conversation(id='$id', title='$title', createdBy=$createdBy, participants=$participants, cafeId='$cafeId', lastUpdated=$lastUpdated)"
    }
}
