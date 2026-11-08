package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * An atom that hides the variables of the term it wraps (not ground, yet no `variables`), as ProbLog's explanations do:
 * only `apply` reaches those variables, and the machine must still resolve them (#1010).
 */
private class Opaque(
    val wrapped: Term,
) : Atom by Atom.of("<opaque:$wrapped>") {
    override val isConstant: Boolean
        get() = false

    override val isGround: Boolean
        get() = wrapped.isGround

    override fun asConstant(): Constant = this

    override fun freshCopy(): Opaque = Opaque(wrapped.freshCopy())

    override fun freshCopy(scope: Scope): Opaque = Opaque(wrapped.freshCopy(scope))

    override fun get(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): Opaque = apply(substitution, *substitutions)

    override fun apply(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): Opaque = apply(Substitution.of(substitution, *substitutions))

    override fun apply(substitution: Substitution): Opaque =
        if (substitution.isEmpty() || isGround) this else Opaque(wrapped.apply(substitution))
}

// wrap(X, W): W is X, hidden in an opaque atom
private object Wrap : BinaryRelation.Functional<ExecutionContext>("wrap") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution = mgu(second, Opaque(first))
}

// unwrap(W, X): X is the term W hides
private object Unwrap : BinaryRelation.Functional<ExecutionContext>("unwrap") {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution = mgu(second, (first as Opaque).wrapped)
}

private fun solve(query: String): Pair<Struct, List<Solution>> {
    val library = Library.of("opaque", primitives = listOf(Wrap, Unwrap).associate { it.descriptionPair })
    val solver = PrologSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(library))
    val goal = Struct.parse(query)
    return goal to solver.solve(goal).toList()
}

class OpaqueTermsSpec :
    FunSpec({
        test("hidden-variables-resolved-for-primitives-and-answers") {
            val (goal, solutions) = solve("wrap(f(Y), W), Y = 1, unwrap(W, Z)")
            val yes = solutions.single().shouldBeInstanceOf<Solution.Yes>()
            val variable = { name: String -> goal.variables.first { it.name == name } }

            yes.substitution[variable("Z")] shouldBe Struct.parse("f(1)")
            yes.substitution[variable("W")].shouldBeInstanceOf<Opaque>().wrapped shouldBe Struct.parse("f(1)")
        }

        test("hidden-variables-resolved-for-catchers") {
            val (_, solutions) = solve("wrap(Y, W), Y = a, wrap(a, B), catch(throw(B), W, true)")

            solutions.single().shouldBeInstanceOf<Solution.Yes>()
        }
    })
