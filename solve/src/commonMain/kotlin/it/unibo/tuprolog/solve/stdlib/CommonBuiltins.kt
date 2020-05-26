package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.stdlib.function.*
import it.unibo.tuprolog.solve.stdlib.primitive.*
import it.unibo.tuprolog.solve.stdlib.primitive.Float as FloatPrimitive

object CommonBuiltins : AliasedLibrary by Library.of(
    alias = "prolog.lang",
    operatorSet = OperatorSet.DEFAULT,
    theory = CommonRules.theory,
    primitives = sequenceOf<PrimitiveWrapper<*>>(
        ArithmeticEqual,
        ArithmeticGreaterThan,
        ArithmeticGreaterThanOrEqualTo,
        ArithmeticLowerThan,
        ArithmeticLowerThanOrEqualTo,
        ArithmeticNotEqual,
        Assert,
        AssertA,
        AssertZ,
        Atom,
        Atomic,
        Callable,
        Compound,
        EnsureExecutable,
        FindAll,
        FloatPrimitive,
        Ground,
        Halt,
        Integer,
        Is,
        TermNotIdentical,
        Natural,
        NewLine,
        NonVar,
        NotUnifiableWith,
        Number,
        TermIdentical,
        UnifiesWith,
        Var,
        Write
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
