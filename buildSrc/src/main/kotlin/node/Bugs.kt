package node

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class Bugs(var url: String? = null, var email: String? = null) {
    companion object {
        fun fromJson(element: JsonElement): Bugs =
            Bugs(
                element.getPropertyOrNull("url"),
                element.getPropertyOrNull("email")
            )
    }

    fun toJson(): JsonObject {
        return JsonObject().also { obj ->
            url?.let { obj.addProperty("url", it) }
            email?.let { obj.addProperty("email", it) }
        }
    }
}