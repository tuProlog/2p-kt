package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
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

    scenario("catch-and-throw/iso-error-terms-thrown-by-the-user") {
        solverFactory.answers(
            "catch(throw(error(existence_error(procedure, f/1), f/1)), error(existence_error(procedure, P), _), true)",
        ) shouldBe listOf("yes: P = f / 1")
        solverFactory.answers("throw(error(existence_error(procedure, f/1), f/1))") shouldBe
            listOf("halt: existence_error(procedure, f / 1)")
    }
}
