package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.theory.Theory

/**
 * Hand-written theories (and their expected goal-to-solutions mappings), covering `->/2`, `;/2` and `member/2`,
 * consumed by [TestSolver]'s `testIfThen1`/`testIfThen2`/`testIfThenElse1`/`testIfThenElse2`/`testMember` cases, as
 * an alternative/complement to the standard-manual examples gathered in [PrologStandardExampleTheories].
 */
object CustomTheories {
    /**
     * A theory with two solutions for `a/1` (`a(1)` and `a(2)`, in this order) and a single fact each for `b/1`
     * (`b(1)`) and `c/1` (`c(2)`), used together with [ifThenTheory2] (which lists `a/1`'s facts in the opposite
     * order) to check that `->/2` and `;/2` commit to the *first* solution of their condition.
     */
    val ifThenTheory1: Theory by lazy {
        logicProgramming {
            theoryOf(
                fact { "a"(1) },
                fact { "a"(2) },
                fact { "b"(1) },
                fact { "c"(2) },
            )
        }
    }

    /** Same facts as [ifThenTheory1], but with the two `a/1` clauses in reverse order (`a(2)` before `a(1)`). */
    val ifThenTheory2: Theory by lazy {
        logicProgramming {
            theoryOf(
                fact { "a"(2) },
                fact { "a"(1) },
                fact { "b"(1) },
                fact { "c"(2) },
            )
        }
    }

    /**
     * `->/2` request goals over [ifThenTheory1] and their expected [Solution]s, checking that the `then` (`->/2`)
     * combinator commits to the first solution of its condition (here, `a(1)`) and never backtracks into it.
     */
    val ifThen1ToSolution by lazy {
        logicProgramming {
            listOf(
                ("a"("X") then "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") then "c"("X")).hasSolutions(
                    { no() },
                ),
                ("a"(1) then "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"(2) then "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"(1) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"(2) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                (("a"("X") and "!") then "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                (("a"("X") and "!") then "c"("X")).hasSolutions(
                    { no() },
                ),
                (("a"("X") and ("X" greaterThan 1)) then "b"("X")).hasSolutions(
                    { no() },
                ),
                (("a"("X") and ("X" greaterThan 1)) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                (("a"("X") and "!" and ("X" greaterThan 1)) then "b"("X")).hasSolutions(
                    { no() },
                ),
                (("a"("X") and "!" and ("X" greaterThan 1)) then "c"("X")).hasSolutions(
                    { no() },
                ),
            )
        }
    }

    /** Same `->/2` request goals as [ifThen1ToSolution], but run over [ifThenTheory2] (`a/1` clauses reversed). */
    val ifThen2ToSolution by lazy {
        logicProgramming {
            listOf(
                ("a"("X") then "b"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"(1) then "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"(2) then "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"(1) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"(2) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                (("a"("X") and "!") then "b"("X")).hasSolutions(
                    { no() },
                ),
                (("a"("X") and "!") then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                (("a"("X") and ("X" greaterThan 1)) then "b"("X")).hasSolutions(
                    { no() },
                ),
                (("a"("X") and ("X" greaterThan 1)) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                (("a"("X") and "!" and ("X" greaterThan 1)) then "b"("X")).hasSolutions(
                    { no() },
                ),
                (("a"("X") and "!" and ("X" greaterThan 1)) then "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
            )
        }
    }

    /**
     * `then ... or ...` (if-then-else) request goals over [ifThenTheory1] and their expected [Solution]s, checking
     * that the "then" branch is taken whenever the condition has a solution, and the "else" branch otherwise —
     * again committing to the condition's first solution only.
     */
    val ifThenElse1ToSolution by lazy {
        logicProgramming {
            listOf(
                ("a"("X") then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") and ("X" greaterThan 1) then "b"("X") or "c"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and ("X" lowerThan 2) then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") and "!" then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") and "!" and ("X" greaterThan 1) then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") and "!" and ("X" lowerThan 2) then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") then "c"("X") or "b"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and ("X" greaterThan 1) then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") and ("X" lowerThan 2) then "c"("X") or "b"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and "!" then "c"("X") or "b"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and "!" and ("X" greaterThan 1) then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") and "!" and ("X" lowerThan 2) then "c"("X") or "b"("X")).hasSolutions(
                    { no() },
                ),
            )
        }
    }

    /** Same if-then-else request goals as [ifThenElse1ToSolution], but run over [ifThenTheory2] (`a/1` reversed). */
    val ifThenElse2ToSolution by lazy {
        logicProgramming {
            listOf(
                ("a"("X") then "b"("X") or "c"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and ("X" greaterThan 1) then "b"("X") or "c"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and ("X" lowerThan 2) then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
                ("a"("X") and "!" then "b"("X") or "c"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and "!" and ("X" greaterThan 1) then "b"("X") or "c"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and "!" and ("X" lowerThan 2) then "b"("X") or "c"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") and ("X" greaterThan 1) then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") and ("X" lowerThan 2) then "c"("X") or "b"("X")).hasSolutions(
                    { no() },
                ),
                ("a"("X") and "!" then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") and "!" and ("X" greaterThan 1) then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 2) },
                ),
                ("a"("X") and "!" and ("X" lowerThan 2) then "c"("X") or "b"("X")).hasSolutions(
                    { yes("X" to 1) },
                ),
            )
        }
    }

    /**
     * `member/2` request goals and their expected [Solution]s: enumerating every element of a ground list via
     * backtracking, and failing to unify against a list of non-matching structs.
     */
    val memberGoalToSolution by lazy {
        logicProgramming {
            listOf(
                "member"("X", logicListOf("a", "b", "c")).hasSolutions(
                    { yes("X" to "a") },
                    { yes("X" to "b") },
                    { yes("X" to "c") },
                    { no() },
                ),
                "member"("f"("X"), logicListOf("a"(1), "b"(2), "c"(3))).hasSolutions(
                    { no() },
                ),
            )
        }
    }
}
