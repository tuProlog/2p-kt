package it.unibo.tuprolog.serialize

actual object ObjectsUtils {
    actual fun parseAsObject(
        string: String,
        mimeType: MimeType,
    ): Any = mimeType.objectMapper.readValue(string, java.lang.Object::class.java)

    actual fun deeplyEqual(
        obj1: Any?,
        obj2: Any?,
    ): Boolean =
        when {
            obj1 is Number && obj2 is Number -> obj1.toString() == obj2.toString()
            obj1 is List<*> && obj2 is List<*> ->
                when {
                    obj1.size != obj2.size -> false
                    else ->
                        obj1.asSequence().zip(obj2.asSequence()).all {
                            deeplyEqual(it.first, it.second)
                        }
                }
            obj1 is Map<*, *> && obj2 is Map<*, *> ->
                when {
                    obj1.size != obj2.size -> false
                    obj1.keys != obj2.keys -> false
                    else -> obj1.keys.all { deeplyEqual(obj1[it], obj2[it]) }
                }
            else -> obj1 == obj2
        }
}
