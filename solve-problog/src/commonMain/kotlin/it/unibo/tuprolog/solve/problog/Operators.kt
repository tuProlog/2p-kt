@file:JvmName("Operators")

package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import kotlin.jvm.JvmName

const val ANNOTATION_FUNCTOR = "::"

val ANNOTATION_OPERATOR = Operator(ANNOTATION_FUNCTOR, Specifier.XFY, 900)

val PROBLOG_SPECIFIC_OPERATORS = OperatorSet(ANNOTATION_OPERATOR)

val PROBLOG_OPERATORS = OperatorSet.DEFAULT + PROBLOG_SPECIFIC_OPERATORS
