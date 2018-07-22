
package www.cafelink.com.cafelink.models.cafe
data class Location(
        val city: String,
        val country: String,
        val latitude: Double,
        val longitude: Double,
        val state: String,
        val street: String,
        val zip: String


) {
    override fun toString(): String {
        return "$street\n$city, $state\n$country $zip"
    }
}