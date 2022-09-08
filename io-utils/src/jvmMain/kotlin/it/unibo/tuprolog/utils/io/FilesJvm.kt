package it.unibo.tuprolog.utils.io

actual fun fileOf(path: String): File = JvmFile(path)
