package it.unibo.tuprolog.utils.io

internal actual object TempPathFinder {
    actual val tempDirectory: String by lazy {
        System.getProperty("java.io.tmpdir")
    }

    actual fun file(name: String, extension: String): String =
        java.io.File.createTempFile(name, ".$extension").absolutePath

    actual fun directory(name: String): String =
        java.io.File.createTempFile(name, "").absolutePath
}
