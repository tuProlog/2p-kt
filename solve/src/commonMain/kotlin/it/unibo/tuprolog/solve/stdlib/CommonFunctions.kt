package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.FunctionWrapper
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.stdlib.function.AbsoluteValue
import it.unibo.tuprolog.solve.stdlib.function.Addition
import it.unibo.tuprolog.solve.stdlib.function.ArcTangent
import it.unibo.tuprolog.solve.stdlib.function.BitwiseAnd
import it.unibo.tuprolog.solve.stdlib.function.BitwiseComplement
import it.unibo.tuprolog.solve.stdlib.function.BitwiseLeftShift
import it.unibo.tuprolog.solve.stdlib.function.BitwiseOr
import it.unibo.tuprolog.solve.stdlib.function.BitwiseRightShift
import it.unibo.tuprolog.solve.stdlib.function.Ceiling
import it.unibo.tuprolog.solve.stdlib.function.Cosine
import it.unibo.tuprolog.solve.stdlib.function.Exponential
import it.unibo.tuprolog.solve.stdlib.function.Exponentiation
import it.unibo.tuprolog.solve.stdlib.function.FloatFractionalPart
import it.unibo.tuprolog.solve.stdlib.function.FloatIntegerPart
import it.unibo.tuprolog.solve.stdlib.function.FloatingPointDivision
import it.unibo.tuprolog.solve.stdlib.function.Floor
import it.unibo.tuprolog.solve.stdlib.function.IntegerDivision
import it.unibo.tuprolog.solve.stdlib.function.Modulo
import it.unibo.tuprolog.solve.stdlib.function.Multiplication
import it.unibo.tuprolog.solve.stdlib.function.NaturalLogarithm
import it.unibo.tuprolog.solve.stdlib.function.Remainder
import it.unibo.tuprolog.solve.stdlib.function.Round
import it.unibo.tuprolog.solve.stdlib.function.Sign
import it.unibo.tuprolog.solve.stdlib.function.SignReversal
import it.unibo.tuprolog.solve.stdlib.function.Sine
import it.unibo.tuprolog.solve.stdlib.function.SquareRoot
import it.unibo.tuprolog.solve.stdlib.function.Subtraction
import it.unibo.tuprolog.solve.stdlib.function.ToFloat
import it.unibo.tuprolog.solve.stdlib.function.Truncate

object CommonFunctions {
    val wrappers: Sequence<FunctionWrapper<*>> =
        sequenceOf(
            AbsoluteValue,
            Addition,
            ArcTangent,
            BitwiseAnd,
            BitwiseComplement,
            BitwiseLeftShift,
            BitwiseOr,
            BitwiseRightShift,
            Ceiling,
            Cosine,
            Exponential,
            Exponentiation,
            ToFloat,
            FloatFractionalPart,
            FloatIntegerPart,
            FloatingPointDivision,
            Floor,
            IntegerDivision,
            Modulo,
            Multiplication,
            NaturalLogarithm,
            Remainder,
            Round,
            Sign,
            SignReversal,
            Sine,
            SquareRoot,
            Subtraction,
            Truncate
        )

    val functions: Map<Signature, LogicFunction> = wrappers.map { it.descriptionPair }.toMap()
}
