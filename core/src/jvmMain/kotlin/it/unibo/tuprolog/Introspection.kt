package it.unibo.tuprolog

import java.io.File

internal actual fun currentPlatform(): Platform =
    if (System.getProperty("java.vm.name").contains("Dalvik", ignoreCase = true)) {
        Platform.ANDROID
    } else {
        Platform.JVM
    }

internal actual fun currentOs(): Os =
    Os.detect(System.getProperty("os.name")) ?: if (File.separator == "\\") Os.WINDOWS else Os.UNKNOWN_POSIX
