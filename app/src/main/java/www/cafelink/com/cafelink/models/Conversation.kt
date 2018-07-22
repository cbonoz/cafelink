package www.cafelink.com.cafelink.models

data class Conversation(val id: String, val title: String, val participants: Map<String, Boolean>, val cafeId: String, val lastUpdated: Long) {

    override fun toString(): String {
        return "Conversation(id='$id', title='$title', participants=$participants, cafeId='$cafeId', lastUpdated=$lastUpdated)"
    }
}
