package it.unibo.tuprolog.solve

/** Conformance tests for the ISO standard order of terms comparison operators `\==`/2, `==`/2, `@>`/2, `@>=`/2, `@<`/2 and `@=<`/2 (each comparing their two arguments *as terms*, without arithmetic evaluation, unlike the arithmetic comparisons in [arithContract]). */
fun termContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("term/term-diff-1") { "\\=="(intOf(1), intOf(1)) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-diff-2") { "\\=="("X", "X") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-diff-3") { "\\=="(intOf(1), intOf(2)) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-diff-4") { "\\=="("X", intOf(1)) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-diff-5") { "\\=="("X", "Y") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-diff-6") { "\\=="(`_`, `_`) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-diff-7") { "\\=="("X", "a"("X")) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-diff-8") { "\\=="("f"("a"), "f"("a")) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-eq-1") { "=="(intOf(1), intOf(1)) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-eq-2") { "=="("X", "X") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-eq-3") { "=="(intOf(1), intOf(2)) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-eq-4") { "=="("X", intOf(1)) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-eq-5") { "=="("X", "Y") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-eq-6") { "=="(`_`, `_`) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-eq-7") { "=="("X", "a"("X")) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-eq-8") { "=="("f"("a"), "f"("a")) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-greater-than-1") { "@>"(realOf(1.0), intOf(1)) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-greater-than-2") { "@>"("aardvark", "zebra") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-greater-than-3") { "@>"("short", "short") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-greater-than-4") { "@>"("short", "shorter") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-greater-than-5") { "@>"("foo"("b"), "foo"("a")) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-greater-than-6") { "@>"("X", "X") yields { kotlin.collections.listOf(it.no()) } }

    case("term/term-greater-than-7") {
        "@>"("foo"("a", "X"), "foo"("b", "Y")) yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("term/term-greater-than-eq-1") {
        "@>="(realOf(1.0), intOf(1)) yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("term/term-greater-than-eq-2") { "@>="("aardvark", "zebra") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-greater-than-eq-3") { "@>="("short", "short") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-greater-than-eq-4") { "@>="("short", "shorter") yields { kotlin.collections.listOf(it.no()) } }

    case("term/term-greater-than-eq-5") {
        "@>="("foo"("b"), "foo"("a")) yields {
            kotlin.collections.listOf(it.yes())
        }
    }

    case("term/term-greater-than-eq-6") { "@>="("X", "X") yields { kotlin.collections.listOf(it.yes()) } }

    case("term/term-greater-than-eq-7") {
        "@>="("foo"("a", "X"), "foo"("b", "Y")) yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("term/term-less-than-1") { "@<"(realOf(1.0), intOf(1)) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-2") { "@<"("aardvark", "zebra") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-3") { "@<"("short", "short") yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-less-than-4") { "@<"("short", "shorter") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-5") { "@<"("foo"("b"), "foo"("a")) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-less-than-6") { "@<"("X", "X") yields { kotlin.collections.listOf(it.no()) } }

    case("term/term-less-than-7") {
        "@<"("foo"("a", "X"), "foo"("b", "Y")) yields {
            kotlin.collections.listOf(it.yes())
        }
    }

    case("term/term-less-than-eq-1") { "@=<"(realOf(1.0), intOf(1)) yields { kotlin.collections.listOf(it.yes()) } }

    case("term/term-less-than-eq-2") {
        "@=<"(realOf(1.1), realOf(1.1)) yields {
            kotlin.collections.listOf(it.yes())
        }
    }

    case("term/term-less-than-eq-3") {
        "@=<"(realOf(1.0), realOf(1.2)) yields {
            kotlin.collections.listOf(it.yes())
        }
    }

    case("term/term-less-than-eq-4") { "@=<"(intOf(1), intOf(1)) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-eq-5") { "@=<"(intOf(1), intOf(2)) yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-eq-6") { "@=<"("aardvark", "zebra") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-eq-7") { "@=<"("short", "short") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-eq-8") { "@=<"("short", "shorter") yields { kotlin.collections.listOf(it.yes()) } }
    case("term/term-less-than-eq-9") { "@=<"("foo"("b"), "foo"("a")) yields { kotlin.collections.listOf(it.no()) } }
    case("term/term-less-than-eq-10") { "@=<"("X", "X") yields { kotlin.collections.listOf(it.yes()) } }

    case("term/term-less-than-eq-11") {
        "@=<"("foo"("a", "X"), "foo"("b", "Y")) yields {
            kotlin.collections.listOf(it.yes())
        }
    }
}
