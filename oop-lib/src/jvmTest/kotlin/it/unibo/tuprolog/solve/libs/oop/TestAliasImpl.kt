package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.oop.primitives.Register
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass
import kotlin.test.assertTrue

class TestAliasImpl(private val solverFactory: SolverFactory) : TestAlias {

    private fun aliasesByObject(obj: Any?): List<String> {
        val ref = when (obj) {
            is KClass<*> -> TypeRef.of(obj)
            null -> ObjectRef.NULL
            else -> ObjectRef.of(obj)
        }
        return OOPLib.clauses.asSequence()
            .filterIsInstance<Rule>()
            .filter { it.head matches Struct.of(Alias.FUNCTOR, Var.anonymous(), ref) }
            .map { it.head[0] as? Atom }
            .filterNotNull()
            .map { it.value }
            .toList()
    }

    private fun assertAliasIsPresent(solver: Solver, alias: String, type: KClass<*>) {
        logicProgramming {
            val query1 = Alias.FUNCTOR(alias, T)
            assertSolutionEquals(
                ktListOf(query1.yes(T to TypeRef.of(type))),
                solver.solveList(query1)
            )
            val query2 = Alias.FUNCTOR(A, TypeRef.of(type))
            assertSolutionEquals(
                aliasesByObject(type).map { query2.yes(A to it) },
                solver.solveList(query2)
            )
        }
    }

    private fun assertAliasIsPresent(solver: Solver, alias: String, obj: Any?) {
        logicProgramming {
            val query1 = Alias.FUNCTOR(alias, T)
            assertSolutionEquals(
                ktListOf(query1.yes(T to ObjectRef.of(obj))),
                solver.solveList(query1)
            )
            val query2 = Alias.FUNCTOR(A, ObjectRef.of(obj))
            assertSolutionEquals(
                aliasesByObject(obj).map { query2.yes(A to it) },
                solver.solveList(query2)
            )
        }
    }

    override fun testDefaultAliases() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))

            assertAliasIsPresent(solver, "string", String::class)
            assertAliasIsPresent(solver, "array", Array::class)
            assertAliasIsPresent(solver, "arraylist", ArrayList::class)
            assertAliasIsPresent(solver, "int", Int::class)
            assertAliasIsPresent(solver, "integer", Int::class)
            assertAliasIsPresent(solver, "double", Double::class)
            assertAliasIsPresent(solver, "float", Float::class)
            assertAliasIsPresent(solver, "long", Long::class)
            assertAliasIsPresent(solver, "short", Short::class)
            assertAliasIsPresent(solver, "byte", Byte::class)
            assertAliasIsPresent(solver, "char", Char::class)
            assertAliasIsPresent(solver, "bool", Boolean::class)
            assertAliasIsPresent(solver, "boolean", Boolean::class)
            assertAliasIsPresent(solver, "any", Any::class)
            assertAliasIsPresent(solver, "nothing", Nothing::class)
            assertAliasIsPresent(solver, "big_integer", BigInteger::class)
            assertAliasIsPresent(solver, "big_decimal", BigDecimal::class)
            assertAliasIsPresent(solver, "system", System::class)
            assertAliasIsPresent(solver, "math", Math::class)
            assertAliasIsPresent(solver, "stdout", System.out)
            assertAliasIsPresent(solver, "stderr", System.err)
            assertAliasIsPresent(solver, "stdin", System.`in`)
        }
    }

    override fun testAliasIsBacktrackable() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))

            val query = Alias.FUNCTOR(A, T)
            val solutions = solver.solveList(query)
            val expected = ktListOf(
                query.yes(A to "string", T to TypeRef.of(String::class)),
                query.yes(A to "array", T to TypeRef.of(Array::class)),
                query.yes(A to "list", T to TypeRef.of(List::class)),
                query.yes(A to "arraylist", T to TypeRef.of(ArrayList::class)),
                query.yes(A to "map", T to TypeRef.of(Map::class)),
                query.yes(A to "hashmap", T to TypeRef.of(LinkedHashMap::class)),
                query.yes(A to "int", T to TypeRef.of(Int::class)),
                query.yes(A to "integer", T to TypeRef.of(Int::class)),
                query.yes(A to "double", T to TypeRef.of(Double::class)),
                query.yes(A to "float", T to TypeRef.of(Float::class)),
                query.yes(A to "long", T to TypeRef.of(Long::class)),
                query.yes(A to "short", T to TypeRef.of(Short::class)),
                query.yes(A to "byte", T to TypeRef.of(Byte::class)),
                query.yes(A to "char", T to TypeRef.of(Char::class)),
                query.yes(A to "bool", T to TypeRef.of(Boolean::class)),
                query.yes(A to "boolean", T to TypeRef.of(Boolean::class)),
                query.yes(A to "any", T to TypeRef.of(Any::class)),
                query.yes(A to "nothing", T to TypeRef.of(Nothing::class)),
                query.yes(A to "big_integer", T to TypeRef.of(BigInteger::class)),
                query.yes(A to "big_decimal", T to TypeRef.of(BigDecimal::class)),
                query.yes(A to "system", T to TypeRef.of(System::class)),
                query.yes(A to "math", T to TypeRef.of(Math::class)),
                query.yes(A to "stdout", T to ObjectRef.of(System.out)),
                query.yes(A to "stderr", T to ObjectRef.of(System.err)),
                query.yes(A to "stdin", T to ObjectRef.of(System.`in`))
            )
            assertSolutionEquals(expected, solutions)
        }
    }

    override fun testRegisterAndAlias() {
        val bInstance = ObjectRef.of(A.B(1, "a"))
        val bType = TypeRef.of(A.B::class)

        val bInstanceAlias = Alias.forObject("bInstance", bInstance.`object`)
        val bTypeAlias = Alias.forObject("bType", bType.type)

        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))

            val registerQuery1 = Register.functor(bInstanceAlias.ref, bInstanceAlias.alias)
            assertSolutionEquals(
                ktListOf(registerQuery1.yes()),
                solver.solveList(registerQuery1)
            )
            assertTrue { bInstanceAlias.implementation in solver.staticKb }
            assertTrue { solver.dynamicKb.size == 0L }

            val aliasQuery1 = bInstanceAlias.implementation.head
            assertSolutionEquals(
                ktListOf(aliasQuery1.yes()),
                solver.solveList(aliasQuery1)
            )

            val registerQuery2 = Register.functor(bTypeAlias.ref, bTypeAlias.alias)
            assertSolutionEquals(
                ktListOf(registerQuery2.yes()),
                solver.solveList(registerQuery2)
            )
            assertTrue { bTypeAlias.implementation in solver.staticKb }
            assertTrue { solver.dynamicKb.size == 0L }

            val aliasQuery2 = bTypeAlias.implementation.head
            assertSolutionEquals(
                ktListOf(aliasQuery2.yes()),
                solver.solveList(aliasQuery2)
            )
        }
    }

    override fun testRegisterWithWrongArguments() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib))

            var query = Register.functor(X, "alias")
            assertSolutionEquals(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Register.signature,
                        X,
                        0
                    )
                ),
                solver.solveOnce(query)
            )

            query = Register.functor(ObjectRef.NULL, A)
            assertSolutionEquals(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Register.signature,
                        A,
                        1
                    )
                ),
                solver.solveOnce(query)
            )

            query = Register.functor(3, "alias")
            assertSolutionEquals(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Register.signature,
                        TypeError.Expected.REFERENCE,
                        intOf(3),
                        0
                    )
                ),
                solver.solveOnce(query)
            )

            query = Register.functor(ObjectRef.NULL, 3)
            assertSolutionEquals(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Register.signature,
                        TypeError.Expected.CALLABLE,
                        intOf(3),
                        1
                    )
                ),
                solver.solveOnce(query)
            )
        }
    }
}
