package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.libraries.stdlib.function.*
import it.unibo.tuprolog.libraries.stdlib.primitive.*
import it.unibo.tuprolog.primitive.PrimitiveWrapper

object CommonBuiltins : LibraryAliased by Library.of(
    alias = "prolog.lang",
    operatorSet = OperatorSet.DEFAULT,
    theory = CommonRules.clauseDb,
    primitives = sequenceOf<PrimitiveWrapper<*>>(
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
        Halt,
        Var,
        NonVar,
        Number,
        Integer,
        Ground,
        it.unibo.tuprolog.libraries.stdlib.primitive.Float,
        Compound,
        Callable,
        Atomic,
        Atom,
        EnsureExecutable
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
