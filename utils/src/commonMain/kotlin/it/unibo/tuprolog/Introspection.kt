@file:JvmName("Introspection")

package it.unibo.tuprolog

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("currentPlatform")
expect fun currentPlatform(): Platform

@JsName("currentOs")
expect fun currentOs(): Os
