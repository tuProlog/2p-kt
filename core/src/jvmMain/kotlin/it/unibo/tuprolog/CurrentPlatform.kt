package it.unibo.tuprolog

internal actual fun currentPlatform(): Platform =
    if (System.getProperty("java.vm.name").contains("Dalvik", ignoreCase = true)) {
        Platform.ANDROID
    } else {
        Platform.JVM
    }
