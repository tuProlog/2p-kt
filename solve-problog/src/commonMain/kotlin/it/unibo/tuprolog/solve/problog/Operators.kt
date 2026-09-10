@file:JvmName("Operators")

package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName

/**
 * The functor of ProbLog's probability-annotation operator, `::`, used to attach a probability to a clause's or
 * fact's head (e.g. `0.3::burglary.` states that `burglary` holds with probability 0.3). Theories parsed with
 * [PROBLOG_OPERATORS] in scope (see [ProblogSolverFactory.defaultBuiltins]) recognize this operator.
 */
@JsName("ANNOTATION_FUNCTOR")
const val ANNOTATION_FUNCTOR = "::"

/** The [Operator] declaration for [ANNOTATION_FUNCTOR]: infix, right-associative (`xfy`), with priority 900 --
 * lower (binding tighter) than the `:-` neck operator, so that `0.3::burglary :- foo.` parses as
 * `(0.3::burglary) :- foo.` rather than `0.3::(burglary :- foo)`. */
@JvmField
@JsName("ANNOTATION_OPERATOR")
val ANNOTATION_OPERATOR = Operator(ANNOTATION_FUNCTOR, Specifier.XFY, 900)

/** The [OperatorSet] containing only the ProbLog-specific operators added on top of standard Prolog, i.e.
 * [ANNOTATION_OPERATOR]. */
@JvmField
@JsName("PROBLOG_SPECIFIC_OPERATORS")
val PROBLOG_SPECIFIC_OPERATORS = OperatorSet(ANNOTATION_OPERATOR)

/** The full [OperatorSet] used by `:solve-problog`: [OperatorSet.DEFAULT] (standard Prolog operators) plus
 * [PROBLOG_SPECIFIC_OPERATORS]. Theories and queries meant to use ProbLog's `::` notation should be parsed with
 * this operator set (e.g. `theoryString.parseAsTheory(PROBLOG_OPERATORS)`, or equivalently
 * `ProblogSolverFactory.defaultBuiltins.operators`). */
@JvmField
@JsName("PROBLOG_OPERATORS")
val PROBLOG_OPERATORS = OperatorSet.DEFAULT + PROBLOG_SPECIFIC_OPERATORS
