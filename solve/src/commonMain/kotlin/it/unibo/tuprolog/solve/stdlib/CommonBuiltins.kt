package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.documentationOf
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
import it.unibo.tuprolog.solve.library.mergeDocumentation
import it.unibo.tuprolog.solve.primitive.Primitive

/**
 * The `prolog.lang` standard [it.unibo.tuprolog.solve.library.Library]: the ISO-mandated core built-in predicates,
 * arithmetic functions, and control-construct rules every 2P-Kt solver is expected to ship with.
 *
 * It merely assembles the other `Common*` singletons in this package: [CommonRules.clauses] as [clauses],
 * [CommonPrimitives.primitives] as [primitives], and [CommonFunctions.functions] as [functions], plus the default
 * operator table ([it.unibo.tuprolog.core.operators.OperatorSet.DEFAULT]). It is what
 * [it.unibo.tuprolog.solve.SolverFactory.defaultBuiltins] is expected to return, and what
 * `solverWithDefaultBuiltins(...)`/`mutableSolverWithDefaultBuiltins(...)` add on top of any other
 * [it.unibo.tuprolog.solve.library.Runtime].
 */
object CommonBuiltins : AbstractLibrary() {
    override val alias: String
        get() = "prolog.lang"

    override val help: String
        get() =
            """
            `library('prolog.lang')`

            Standard Prolog built-ins, arithmetic functions, control constructs, and operators.
            """.trimIndent()

    override val documentation: Map<Signature, String>
        get() = mergeDocumentation(super.documentation, documentationOf(CommonRules.wrappers), operatorDocumentation)

    /** Documentation of the operators which are not backed by any predicate or function of this library. */
    private val operatorDocumentation: Map<Signature, String> =
        mapOf(
            Signature(":-", 2) to
                """
                `+Head :- +Body`

                Separates the head of a rule from its body in a theory: `Head` holds whenever `Body` can be proved. `Body` is a goal, typically a conjunction (`,/2`) of sub-goals.
                """.trimIndent(),
            Signature(":-", 1) to
                """
                `:- +Directive`

                Marks a directive in a theory, i.e. a goal to be handled while the theory is loaded rather than a clause. This implementation recognises `dynamic/1`, `static/1`, `initialization/1`, `solve/1`, `include/1`, `load/1`, `op/3`, `set_flag/2` and `set_prolog_flag/2`; any other directive is retained in the theory without special treatment.
                """.trimIndent(),
            Signature("?-", 1) to
                """
                `?- +Goal`

                Conventional prefix marking a query, as in `?- member(X, [1, 2]).` It is part of the standard operator table so that such terms can be read and written, but this implementation gives it no special meaning when loading a theory.
                """.trimIndent(),
            Signature("-->", 2) to
                """
                `+Head --> +Body`

                Separates the head of a Definite Clause Grammar (DCG) rule from its body. The operator is part of the standard operator table, but this implementation performs no DCG translation: a `Head --> Body` clause is stored as an ordinary `-->/2` fact rather than expanded into a predicate with two extra list arguments.
                """.trimIndent(),
            Signature("^", 2) to
                """
                `+Variable ^ +Goal`

                Existential quantification for `bagof/3` and `setof/3`: in `bagof(T, V^G, L)` the variables of `V` are not used to group the solutions of `G`. `V` may be a single variable or a tuple of variables, e.g. `(X, Y)^G`; the current implementation only recognises one outermost `^` (nested forms such as `X^Y^G` and lists of variables are not supported). Outside such goals it has no meaning: in particular, no arithmetic function `^/2` is defined (use `**/2` for exponentiation).
                """.trimIndent(),
            Signature("+", 1) to
                """
                `+ +Term`

                Prefix plus, part of the standard operator table, so that `+ X` is read as `+(X)`. The parser folds it into numeric literals (`+ 1` is read as `1`), but this library defines no arithmetic function `+/1`: evaluating any other `+(X)` term, e.g. `Y = 1, X is +Y` or `X is +(1.5)`, raises a type error (`evaluable`).
                """.trimIndent(),
        )

    override val operators: OperatorSet
        get() = OperatorSet.DEFAULT

    override val clauses: List<Clause>
        get() = CommonRules.clauses

    override val primitives: Map<Signature, Primitive>
        get() = CommonPrimitives.primitives

    override val functions: Map<Signature, LogicFunction>
        get() = CommonFunctions.functions
}
