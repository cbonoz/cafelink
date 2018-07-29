package www.cafelink.com.cafelink.models

import www.cafelink.com.cafelink.models.cafe.Location
import www.cafelink.com.cafelink.models.cafe.Picture

data class ConversationCafe(val name: String, val phone: String?, val location: Location, val picture: Picture)
