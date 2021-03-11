package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.Unknown
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/*
 * [(set_prolog_flag(unknown, fail), current_prolog_flag(unknown, V)), [[V <-- fail]]].
 * [set_prolog_flag(X, warning), instantiation_error].
 * [set_prolog_flag(5, decimals), type_error(atom,5)].
 * [set_prolog_flag(date, 'July 1999'), domain_error(prolog_flag,date)].
 * [set_prolog_flag(debug, no), domain_error(flag_value,debug+no)].
 * [set_prolog_flag(max_arity, 40), permission_error(modify, flag, max_arity)].
 *
 * [set_prolog_flag(double_quotes, atom), success].
 * [X = "fred", [[X <-- fred]]].
 *
 * [set_prolog_flag(double_quotes, codes), success].
 * [X = "fred", [[X <-- [102,114,101,100]]]].
 *
 * [set_prolog_flag(double_quotes, chars), success].
 * [X = "fred", [[X <-- [f,r,e,d]]]].
 * [current_prolog_flag(debug, off), success].
 * [(set_prolog_flag(unknown, warning), current_prolog_flag(unknown, warning)), success].
 * [(set_prolog_flag(unknown, warning), current_prolog_flag(unknown, error)), failure].
 * [current_prolog_flag(debug, V), [[V <-- off]]].
 * [current_prolog_flag(5, V), type_error(atom,5)].
 * [current_prolog_flag(warning, V), domain_error(prolog_flag,warning)].
 */
class TestFlagsImpl (private val solverFactory: SolverFactory) : TestFlags {
    override fun defaultLastCallOptimizationIsOn() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(LastCallOptimization.ON, LastCallOptimization.defaultValue)

            val query = current_prolog_flag(LastCallOptimization.name, LastCallOptimization.ON)

            assertSolutionEquals(
                ktListOf(query.yes()),
                solver.solve(query, shortDuration).toList()
            )
        }
    }

    override fun defaultUnknownIsWarning() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(Unknown.WARNING, Unknown.defaultValue)

            val query = current_prolog_flag(Unknown.name, Unknown.WARNING)

            assertSolutionEquals(
                ktListOf(query.yes()),
                solver.solve(query, shortDuration).toList()
            )
        }
    }

    override fun settingUnknownToAdmissibleValueSucceeds() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            for (value in Unknown.admissibleValues) {

                val query = set_prolog_flag(Unknown.name, value) and current_prolog_flag(Unknown.name, V)

                assertSolutionEquals(
                    ktListOf(query.yes(V to value)),
                    solver.solve(query, shortDuration).toList()
                )
            }
        }
    }

    override fun flagsNamesMustBeAtoms() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val solutions = solver.solve(current_prolog_flag(F, `_`), shortDuration).toList()

            assertTrue { solutions.isNotEmpty() }

            for (sol in solutions.filterIsInstance<Solution.Yes>()) {
                assertTrue { sol.substitution[F] is Atom }
            }

            for (term in ktListOf(5, "f"("x"), 2.3).map { it.toTerm() }) {
                var query = current_prolog_flag(term, `_`);
                assertEquals(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("current_prolog_flag", 2),
                            TypeError.Expected.ATOM,
                            term,
                            0
                        )
                    ),
                    solver.solveOnce(query)
                )

                query = set_prolog_flag(term, "value");
                assertEquals(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("set_prolog_flag", 2),
                            TypeError.Expected.ATOM,
                            term,
                            0
                        )
                    ),
                    solver.solveOnce(query)
                )
            }
        }
    }

    override fun gettingMissingFlagsFails() {
        TODO("Not yet implemented")
    }

    override fun settingMissingFlagsSucceeds() {
        TODO("Not yet implemented")
    }

    override fun gettingFlagsByVariableEnumeratesFlags() {
        TODO("Not yet implemented")
    }

    override fun settingFlagsByVariableGeneratesInstantiationError() {
        TODO("Not yet implemented")
    }

    override fun settingWrongValueToLastCallOptimizationProvokesDomainError() {
        TODO("Not yet implemented")
    }

    override fun attemptingToEditMaxArityFlagProvokesPermissionError() {
        TODO("Not yet implemented")
    }
}