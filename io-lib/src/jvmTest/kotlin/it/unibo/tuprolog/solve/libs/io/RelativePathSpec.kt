package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import java.io.File

private const val RELATIVE_FILE = "build/tmp/relative-path-spec.txt"

/** Paths relative to the working directory, given where a URL is expected. */
class RelativePathSpec :
    FunSpec({
        withTests(nameFn = { "url-of/$it" }, listOf("t.pl", "./t.pl", "dir/t.pl")) { path ->
            File(Url.of(path).toURL().toURI()) shouldBe File(path).absoluteFile.normalize()
        }

        test("open/reads-a-file-given-by-a-relative-path") {
            File(RELATIVE_FILE).apply { parentFile.mkdirs() }.writeText("hello")
            val solution =
                PrologSolverFactory
                    .solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))
                    .solveOnce("open('$RELATIVE_FILE', read, S), get_char(S, C), close(S)".parseAsStruct())
            solution.shouldBeInstanceOf<Solution.Yes>()
            solution.substitution.getByName("C") shouldBe Atom.of("h")
        }
    })
