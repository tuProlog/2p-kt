package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentNonVarContract() =
    concurrentContract {
        case("non-var/non-var-number") { nonvar(33.3) yields { listOf(it.yes()) } }
        case("non-var/non-var-foo") { nonvar("foo") yields { listOf(it.yes()) } }
        case("non-var/non-var-foo-cl") { nonvar("Foo") yields { listOf(it.no()) } }

        case("non-var/non-var-foo-assignment") {
            ("foo" eq "Foo") and nonvar("Foo") yields {
                listOf(it.yes("Foo" to "foo"))
            }
        }

        case("non-var/non-var-any-term") { nonvar(`_`) yields { listOf(it.no()) } }
        case("non-var/non-var") { nonvar("a"("b")) yields { listOf(it.yes()) } }
    }
