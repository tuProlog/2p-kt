package node

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class People(var name: String? = null, var email: String? = null, var url: String? = null) {
    companion object {
        fun fromJson(element: JsonElement): People =
            People(
                element.getPropertyOrNull("name"),
                element.getPropertyOrNull("email"),
                element.getPropertyOrNull("url")
            )
    }

    fun toJson(): JsonObject {
        return JsonObject().also { obj ->
            name?.let { obj.addProperty("name", it) }
            email?.let { obj.addProperty("email", it) }
            url?.let { obj.addProperty("url", it) }
        }
    }
}