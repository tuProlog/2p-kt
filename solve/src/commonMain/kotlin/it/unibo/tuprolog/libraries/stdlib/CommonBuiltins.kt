package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.libraries.stdlib.function.*
import it.unibo.tuprolog.libraries.stdlib.primitive.*

object CommonBuiltins : LibraryAliased by Library.of(
        alias = "prolog.lang",
        operatorSet = OperatorSet.DEFAULT,
        theory = CommonRules,
        primitives = sequenceOf(
                ArithmeticEqual,
                ArithmeticNotEqual,
                ArithmeticGreaterThan,
                ArithmeticGreaterThanOrEqualTo,
                ArithmeticLowerThan,
                ArithmeticLowerThanOrEqualTo,
                UnifiesWith,
                NotUnifiableWith,
                TermIdentical,
                TermNotIdentical,
                Natural,
                Is,
                Halt
        ).map { it.descriptionPair }.toMap(),
        functions = sequenceOf(
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
        ).map { it.descriptionPair }.toMap()
)
