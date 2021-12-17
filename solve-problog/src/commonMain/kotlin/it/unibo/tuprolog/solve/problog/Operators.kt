@file:JvmName("Operators")

package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName

@JsName("ANNOTATION_FUNCTOR")
const val ANNOTATION_FUNCTOR = "::"

@JvmField
@JsName("ANNOTATION_OPERATOR")
val ANNOTATION_OPERATOR = Operator(ANNOTATION_FUNCTOR, Specifier.XFY, 900)

@JvmField
@JsName("PROBLOG_SPECIFIC_OPERATORS")
val PROBLOG_SPECIFIC_OPERATORS = OperatorSet(ANNOTATION_OPERATOR)

@JvmField
@JsName("PROBLOG_OPERATORS")
val PROBLOG_OPERATORS = OperatorSet.DEFAULT + PROBLOG_SPECIFIC_OPERATORS
