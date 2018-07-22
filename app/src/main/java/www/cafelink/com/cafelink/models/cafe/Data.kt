package www.cafelink.com.cafelink.models.cafe

data class Data(
        val hours: List<Hour>?,
        val name: String,
        val phone: String?,
        val picture: Picture,
        val location: Location,
        val id: String
) {


    fun getInfo(): String {
        val cafeString = StringBuilder()

//        cafeString.append("Name: \n").append(name.capitalize())

        if (hours != null && hours.isNotEmpty()) {
            cafeString.append("\n\nHours:\n")
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
        }

        if (!phone.isNullOrBlank()) {
            cafeString.append("\n\nPhone:").append("\n${phone}")
        }

        cafeString.append("\n\nLocation:\n${location}")
        return cafeString.toString()
    }

    override fun toString(): String {
        return "CafeData(hours=$hours, name='$name', phone=$phone, picture=$picture, location=$location, id='$id')"
    }
}