package www.cafelink.com.cafelink.models

import java.util.*

class User(val userName: String, val userId: String = UUID.randomUUID().toString())