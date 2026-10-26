package it.unibo.tuprolog

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

internal expect val expectedPlatform: Platform

class PlatformSpec :
    FunSpec({
        test("current-platform") {
            assertEquals(expectedPlatform, Info.PLATFORM)
        }
    })
