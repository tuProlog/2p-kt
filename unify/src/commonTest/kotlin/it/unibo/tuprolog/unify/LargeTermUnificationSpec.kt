package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.WorklistUnificators
import it.unibo.tuprolog.core.List.Companion as LogicList

private const val SIZE = 10_000
private const val DEPTH = 50_000

private fun nested(
    depth: Int,
    leaf: Term,
): Term = (0 until depth).fold(leaf) { term, _ -> Struct.of("f", term) }

/** Unification of large terms is iterative and linear: no stack overflow, no quadratic propagation of bindings. */
class LargeTermUnificationSpec :
    FunSpec({
        val unificator = WorklistUnificators.strict()
        val a = Atom.of("a")
        val b = Atom.of("b")

        test("many-variables") {
            val variables = List(SIZE) { Var.of("X$it") }
            val values = List(SIZE) { Integer.of(it) }
            val mgu = unificator.mgu(Struct.of("f", variables), Struct.of("f", values))
            mgu shouldBe Substitution.of(variables.zip(values))
        }

        test("long-variable-chain") {
            val variables = List(SIZE + 1) { Var.of("X$it") }
            // f(X0, ..., Xn-1, Xn) = f(X1, ..., Xn, a)
            val mgu = unificator.mgu(Struct.of("f", variables), Struct.of("f", variables.drop(1) + a))
            mgu shouldBe Substitution.of(variables.map { it to a })
        }

        test("repeated-variable") {
            val x = Var.of("X")
            unificator.mgu(Struct.of("f", List(SIZE) { x }), Struct.of("f", List(SIZE) { a })) shouldBe
                Substitution.of(x, a)
            unificator.mgu(Struct.of("f", List(SIZE) { x }), Struct.of("f", List(SIZE - 1) { a } + b)) shouldBe
                Substitution.failed()
        }

        test("deep-terms") {
            val x = Var.of("X")
            unificator.mgu(nested(DEPTH, x), nested(DEPTH, a)) shouldBe Substitution.of(x, a)
            unificator.mgu(nested(DEPTH, a), nested(DEPTH, b)) shouldBe Substitution.failed()
            unificator.mgu(x, nested(DEPTH, x)) shouldBe Substitution.failed()
        }

        test("list-of-bound-variables") {
            val variables = List(SIZE) { Var.of("X$it") }
            val values = List(SIZE) { Integer.of(it) }
            val l = Var.of("L")
            // L is bound to a list of variables, which then get bound one by one
            val mgu =
                unificator.mgu(
                    Struct.of("f", l, l),
                    Struct.of("f", LogicList.of(variables), LogicList.of(values)),
                )
            mgu shouldBe Substitution.of(variables.zip(values) + (l to LogicList.of(values)))
        }

        test("partial-lists") {
            val tail = Var.of("T")
            val prefix = List(SIZE) { Integer.of(it) }
            val mgu = unificator.mgu(LogicList.from(prefix, tail), LogicList.of(prefix + a + b))
            mgu shouldBe Substitution.of(tail, LogicList.of(a, b))
            unificator.mgu(LogicList.from(prefix, tail), LogicList.of(prefix.dropLast(1) + b)) shouldBe
                Substitution.failed()
        }
    })
