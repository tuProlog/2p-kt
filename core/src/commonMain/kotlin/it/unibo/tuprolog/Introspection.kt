@file:JvmName("Introspection")

package it.unibo.tuprolog

import kotlin.jvm.JvmName

internal expect fun currentPlatform(): Platform

internal expect fun currentOs(): Os
