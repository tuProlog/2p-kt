package it.unibo.tuprolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal expect val expectedPlatform: Platform

class PlatformSpec :
    FunSpec({
        test("current-platform") {
            Info.PLATFORM shouldBe expectedPlatform
        }
    })
