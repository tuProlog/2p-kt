package it.unibo.tuprolog.serialize

actual object ObjectsUtils {
    actual fun parseAsObject(
        string: String,
        mimeType: MimeType,
    ): Any =
        when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError()
            is MimeType.Yaml -> YAML.parse(string)
            is MimeType.Json -> JSON.parse(string)
        } as Any

    actual fun deeplyEqual(
        obj1: Any?,
        obj2: Any?,
    ): Boolean = JSON.stringify(obj1) == JSON.stringify(obj2)
}
