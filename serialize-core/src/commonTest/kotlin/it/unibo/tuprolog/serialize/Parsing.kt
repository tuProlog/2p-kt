package it.unibo.tuprolog.serialize

expect fun parseAsObject(string: String, mimeType: MimeType): Any

expect fun deeplyEqual(obj1: Any?, obj2: Any?): Boolean