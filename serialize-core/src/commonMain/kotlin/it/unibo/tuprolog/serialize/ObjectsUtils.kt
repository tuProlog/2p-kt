package it.unibo.tuprolog.serialize

expect object ObjectsUtils {
    fun parseAsObject(
        string: String,
        mimeType: MimeType,
    ): Any

    fun deeplyEqual(
        obj1: Any?,
        obj2: Any?,
    ): Boolean
}
