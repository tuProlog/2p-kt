package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentCopyTermContract() =
    concurrentContract {
        case("copy-term/copy-x-num") { copy_term(X, 3) yields { listOf(it.yes()) } }
        case("copy-term/copy-any-a") { copy_term(`_`, "a") yields { listOf(it.yes()) } }
        case("copy-term/copy-sum") { copy_term(atomOf("a") + X, X + "b") yields { listOf(it.yes(X to "a")) } }
        case("copy-term/copy-any-any") { copy_term(`_`, `_`) yields { listOf(it.yes()) } }
        case("copy-term/copy-triple-sum") { copy_term(X + X + Y, A + B + B) yields { listOf(it.yes(A to B)) } }
        case("copy-term/copy-aa") { copy_term("a", "a") yields { listOf(it.yes()) } }
        case("copy-term/copy-ab") { copy_term("a", "b") yields { listOf(it.no()) } }
        case("copy-term/copy-f") { copy_term("f"("a"), "f"(X)) yields { listOf(it.yes(X to "a")) } }

        case("copy-term/double-copy") {
            copy_term(atom("a") + X, X + "b") and copy_term(atom("a") + X, X + "b") yields {
                listOf(it.no())
            }
        }
    }
