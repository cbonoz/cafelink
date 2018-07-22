
package www.cafelink.com.cafelink.models.cafe

data class Data(
        val hours: List<Hour>,
        val name: String,
        val phone: String,
        val picture: Picture,
        val location: Location,
        val id: String


) {
    override fun toString(): String {
        return "Cafe(hours=$hours, name='$name', phone='$phone', picture=$picture, location=$location, id='$id')"
    }
}