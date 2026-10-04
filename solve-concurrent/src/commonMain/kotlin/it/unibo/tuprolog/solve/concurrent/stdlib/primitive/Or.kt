package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.rule.Arrow

/**
 * Disjunction (`;/2`), including its `Cond -> Then ; Else` if-then-else form. Implemented directly as a
 * [it.unibo.tuprolog.solve.primitive.Primitive] rather than as a rule (unlike `,/2`, see
 * [it.unibo.tuprolog.solve.concurrent.stdlib.rule.Comma]) so that the two branches -- the condition/then pair, or
 * `first`/`second` in the plain-disjunction case -- can each be solved with their own [ConcurrentSolver] instance,
 * one of the few places in this module's standard library, besides the core `:solve-concurrent` FSM, where two
 * independent sub-resolutions are combined explicitly.
 *
 * Since [it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins] registers this object under the same `;/2`
 * signature as the `Semicolon`/if-then-else rules shared with the other resolution strategies (via
 * `it.unibo.tuprolog.solve.stdlib.CommonBuiltins`), and primitives take precedence over rules (see
 * `it.unibo.tuprolog.solve.concurrent.fsm.StatePrimitiveSelection`), this primitive always wins and those inherited
 * rules are effectively unused here (see the `// TODO removes rule for -> and ; from default builtins` comment in
 * `DefaultBuiltins`).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if either branch is not callable.
 */
object Or : BinaryRelation<ConcurrentExecutionContext>(";") {
    override val help: String =
        """
        `(+Left ; +Right)`

        Disjunction: solutions come from `Left` and from `Right`, each proved by an independent sub-solver. When `Left` is `Condition -> Then`, it acts as if-then-else instead: `Condition` is proved once, then `Then` is proved under its bindings if it succeeded, `Else` (i.e. `Right`) otherwise. Both arguments, and both sides of `->`, must be callable, otherwise a type error is raised.

        **Examples**

        ```prolog
        % solutions may come in any order on this engine
        ?- findall(X, (X = 1 ; X = 2), L), member(1, L), member(2, L).
        yes.

        ?- (X = 1 -> Y = one ; Y = other).
        X = 1, Y = one.

        ?- (fail -> X = a ; X = b).
        X = b.

        ?- catch((throw(oops) -> X = a ; X = b), E, true).
        E = oops.

        ?- (1 ; true).
        throws error(type_error(callable, 1), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ConcurrentExecutionContext>.computeAll(
        first: Term,
        second: Term,
    ): Sequence<Solve.Response> {
        ensuringArgumentIsCallable(0)
        ensuringArgumentIsCallable(1)
        if (first is Struct && first.arity == 2 && first.functor == Arrow.functor) {
            val solver: ConcurrentSolver = subSolver() as ConcurrentSolver
            // TODO open an issue to create and ad-hoc ensuringMethod to serve this purpose
            first.args.forEach {
                if (it !is Struct) {
                    throw TypeError.forGoal(context, signature, TypeError.Expected.CALLABLE, it)
                }
            }
            return when (val condition = solver.solveOnce(first[0] as Struct)) {
                is Solution.Yes ->
                    solver.solve(first[1].apply(condition.substitution).castToStruct()).map {
                        mapSolution(it, condition.substitution)
                    }
                is Solution.No -> solver.solve(second as Struct).map { mapSolution(it) }
                is Solution.Halt -> sequenceOf(mapSolution(condition))
            }
        } else {
            val solver1: ConcurrentSolver = subSolver() as ConcurrentSolver
            val solver2: ConcurrentSolver = subSolver() as ConcurrentSolver
            return (solver1.solve(first.castToStruct()) + solver2.solve(second.castToStruct())).map { mapSolution(it) }
        }
    }

    private fun Solve.Request<ConcurrentExecutionContext>.mapSolution(
        solution: Solution,
        conditionSubstitution: Substitution = Substitution.empty(),
    ): Solve.Response =
        when (solution) {
            is Solution.Yes -> {
                val actualSubstitution = solution.substitution + conditionSubstitution
                if (actualSubstitution is Substitution.Unifier) {
                    replySuccess(actualSubstitution)
                } else {
                    replyFail()
                }
            }
            is Solution.No -> replyFail()
            is Solution.Halt -> replyException(solution.exception.pushContext(context))
        }
}
