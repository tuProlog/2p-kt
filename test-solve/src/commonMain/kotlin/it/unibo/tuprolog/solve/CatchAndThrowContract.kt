package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.SystemError

/** Conformance tests for the ISO `catch/3` and `throw/1` built-ins. */
fun catchAndThrowContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("catch-and-throw/catch-throw") {
        (catch(true, C, write("something")) and `throw`("blabla")) yields {
            kotlin.collections.listOf(
                it.halt(
                    SystemError.forUncaughtException(
                        DummyInstances.executionContext,
                        atomOf("blabla"),
                    ),
                ),
            )
        }
    }

    case("catch-and-throw/catch-fail") {
        catch(number_chars("A", "L"), "error"("instantiation_error", `_`), fail) yields {
            kotlin.collections.listOf(
                it.no(),
            )
        }
    }
}
