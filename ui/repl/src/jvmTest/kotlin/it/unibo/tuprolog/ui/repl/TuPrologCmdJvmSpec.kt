package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import java.io.File

/** Options only the JVM supports: `-T`/`--theory` (the JS REPL cannot read files yet) and `--oop`. */
class TuPrologCmdJvmSpec :
    FunSpec({
        fun theoryFile(text: String): String =
            File
                .createTempFile("theory", ".pl")
                .apply {
                    deleteOnExit()
                    writeText(text)
                }.path

        test("loads-the-clauses-into-the-knowledge-base") {
            val file = theoryFile("parent(a, b).\nparent(b, c).\n")
            repl().test(listOf("-T", file, "solve", "parent(a, X).")).shouldShow(
                stdout = listOf("# Successfully loaded 2 clauses from $file", "X = b"),
            )
        }

        test("merges-several-files") {
            val first = theoryFile("p(1).\n")
            val second = theoryFile("q(2).\n")
            repl().test(listOf("-T", first, "--theory", second, "solve", "p(X), q(Y).")).shouldShow(
                stdout = listOf("X = 1", "Y = 2"),
            )
        }

        test("a-file-that-does-not-parse-is-reported-and-skipped") {
            val broken = theoryFile("p(1).\np(.\n")
            val result = repl().test(listOf("-T", broken, "solve", "true."))
            result.shouldShow(
                stdout = listOf("yes: true"),
                stderr = listOf("# Error while parsing theory file: $broken", "#     Line   : 2"),
            )
            result.stdout shouldNotContain "Successfully loaded 1 clauses"
        }

        test("a-missing-file-is-silently-skipped") {
            val result = repl().test(listOf("-T", "/no/such/theory.pl", "solve", "true."))
            result.shouldShow(stdout = listOf("yes: true"))
            result.stderr shouldBe ""
        }

        test("oop-option-loads-the-oop-library") {
            repl().test(listOf("--oop", "solve", "true.")).shouldShow(
                stdout = listOf("Successfully loaded library `prolog.oop"),
            )
        }
    })
