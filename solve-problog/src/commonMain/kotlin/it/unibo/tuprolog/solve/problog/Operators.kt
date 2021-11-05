@file:JvmName("Operators")

package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName

const val ANNOTATION_FUNCTOR = "::"

@JvmField
val ANNOTATION_OPERATOR = Operator(ANNOTATION_FUNCTOR, Specifier.XFY, 900)

@JvmField
val PROBLOG_SPECIFIC_OPERATORS = OperatorSet(ANNOTATION_OPERATOR)

@JvmField
val PROBLOG_OPERATORS = OperatorSet.DEFAULT + PROBLOG_SPECIFIC_OPERATORS
