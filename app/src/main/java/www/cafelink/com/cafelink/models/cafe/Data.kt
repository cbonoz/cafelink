package www.cafelink.com.cafelink.models.cafe

import www.cafelink.com.cafelink.models.ConversationCafe

data class Data(
        val hours: List<Hour>?,
        val name: String,
        val phone: String?,
        val picture: Picture,
        val location: Location,
        val id: String
) {

    // reduced cafe representations
    fun toConversationCafe(): ConversationCafe {
        return ConversationCafe(name, phone, location, picture)
    }

    fun getInfo(): String {
        val cafeString = StringBuilder()
//        cafeString.append("Name: \n").append(name.capitalize())

        if (hours != null && hours.isNotEmpty()) {
            cafeString.append("Hours:\n")
            hours.map {
                val dayTokens = it.key.split("_")
                val dayString = when (dayTokens.size) {
                    3 -> "${dayTokens.get(0).capitalize()} ${dayTokens.get(2)}"
                    2 -> "${dayTokens.get(0).capitalize()} ${dayTokens.get(1)}"
                    else -> when (it.key.isBlank()) {
                        true -> "Unknown"
                        false -> it.key.replace("_", " ").capitalize()
                    }
                }
                cafeString.append("\n * $dayString: ${it.value}")
            }
            cafeString.append("\n")
        }

        if (!phone.isNullOrBlank()) {
            cafeString.append("\nPhone:").append("\n${phone}\n")
        }

        cafeString.append("\nLocation:\n${location}")
        return cafeString.toString()
    }

    override fun toString(): String {
        return "CafeData(hours=$hours, name='$name', phone=$phone, picture=$picture, location=$location, id='$id')"
    }
}