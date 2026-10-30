package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.testing.CliktCommandTestResult
import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.library.Library

/** One run of the REPL's command line: its arguments, what the user types, and what must show up. */
private data class RunCase(
    val id: String,
    val argv: List<String>,
    val stdin: String = "",
    val stdout: List<String> = emptyList(),
    val stderr: List<String> = emptyList(),
    val notInStdout: List<String> = emptyList(),
    val status: Int = 0,
)

private val solveCases =
    listOf(
        RunCase("ground-yes", listOf("solve", "true."), stdout = listOf("yes: true")),
        RunCase("no", listOf("solve", "fail."), stdout = listOf("no.")),
        RunCase(
            "first-solution-only-without-semicolon",
            listOf("solve", "member(X, [1, 2])."),
            stdout = listOf("X = 1"),
            notInStdout = listOf("X = 2"),
        ),
        RunCase(
            "semicolon-asks-for-the-next-solution",
            listOf("solve", "member(X, [1, 2])."),
            stdin = ";\n",
            stdout = listOf("X = 1", "X = 2"),
        ),
        RunCase(
            "number-of-solutions",
            listOf("solve", "-n", "2", "member(X, [1, 2, 3])."),
            stdout = listOf("X = 1", "X = 2"),
            notInStdout = listOf("X = 3"),
        ),
        RunCase(
            "error-is-printed-as-halt",
            listOf("solve", "atom_length(X, L)."),
            stdout = listOf("halt: ", "is unexpectedly not instantiated"),
        ),
        RunCase("parse-error-goes-to-stderr", listOf("solve", "foo(."), stderr = listOf("# ")),
        RunCase("halt-exits-with-zero", listOf("solve", "halt."), stdout = listOf("# goodbye.")),
        RunCase("halt-with-status", listOf("solve", "halt(3)."), stdout = listOf("# goodbye."), status = 3),
        RunCase(
            "timeout-option",
            listOf("-t", "10", "solve", "sleep(2000)."),
            stdout = listOf("time"),
        ),
    )

private val loopCases =
    listOf(
        RunCase(
            "answers-each-query-until-end-of-input",
            emptyList(),
            stdin = "X = 1.\nfail.\n",
            stdout = listOf("X = 1", "no.", "# goodbye."),
        ),
        RunCase("multiline-query", emptyList(), stdin = "X =\n1.\n", stdout = listOf("X = 1")),
        RunCase(
            "parse-error-does-not-stop-the-loop",
            emptyList(),
            stdin = "foo(.\ntrue.\n",
            stdout = listOf("yes: true"),
            stderr = listOf("# "),
        ),
        RunCase("end-of-input-inside-a-query", emptyList(), stdin = "X =\n", stdout = listOf("# goodbye.")),
        RunCase("halt-ends-the-loop", emptyList(), stdin = "halt.\ntrue.\n", notInStdout = listOf("yes: true")),
        RunCase(
            "warnings-go-to-stderr-with-their-stack-trace",
            emptyList(),
            stdin = "set_prolog_flag(unknown, warning), undefined_predicate.\n",
            stdout = listOf("no."),
            stderr = listOf("undefined_predicate", "#    at "),
        ),
    )

internal fun repl(vararg libraries: Library) = TuPrologCmd(*libraries).subcommands(TuPrologSolveQuery())

internal fun CliktCommandTestResult.shouldShow(
    stdout: List<String> = emptyList(),
    stderr: List<String> = emptyList(),
) {
    stdout.forEach { this.stdout shouldContain it }
    stderr.forEach { this.stderr shouldContain it }
}

class TuPrologCmdSpec :
    FunSpec({
        withTests(nameFn = { "solve/${it.id}" }, solveCases) { case ->
            val result = repl().test(case.argv, stdin = case.stdin)
            result.shouldShow(case.stdout, case.stderr)
            case.notInStdout.forEach { result.stdout shouldNotContain it }
            result.statusCode shouldBe case.status
        }

        withTests(nameFn = { "loop/${it.id}" }, loopCases) { case ->
            val result = repl().test(case.argv, stdin = case.stdin)
            result.shouldShow(case.stdout, case.stderr)
            case.notInStdout.forEach { result.stdout shouldNotContain it }
            result.statusCode shouldBe case.status
        }

        test("banner/names-the-version-and-every-library-once") {
            val result = repl().test("solve true.")
            result.shouldShow(stdout = listOf("# 2P-Kt version ", "Successfully loaded library `prolog.io"))
            // the solve subcommand builds the only solver: a second one would load every theory file twice
            "# 2P-Kt version ".toRegex().findAll(result.stdout).count() shouldBe 1
        }

        test("additional-libraries/are-loaded-and-usable") {
            val custom = Library.of("custom", clauses = listOf(Clause.of(Struct.of("custom_fact", Atom.of("ok")))))
            repl(custom).test(listOf("solve", "custom_fact(X).")).shouldShow(
                stdout = listOf("Successfully loaded library `custom`", "X = ok"),
            )
        }
    })
