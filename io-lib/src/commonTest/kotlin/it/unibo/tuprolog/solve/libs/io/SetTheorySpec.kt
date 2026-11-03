package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory

class SetTheorySpec :
    FunSpec({
        test("malformed-text-raises-an-iso-syntax-error") {
            PrologSolverFactory
                .solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))
                .solveOnce("catch(set_theory('foo(.'), error(syntax_error(_), _), true)".parseAsStruct())
                .shouldBeInstanceOf<Solution.Yes>()
        }
    })
