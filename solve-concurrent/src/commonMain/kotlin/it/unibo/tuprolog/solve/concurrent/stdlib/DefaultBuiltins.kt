package it.unibo.tuprolog.solve.concurrent.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Or
import it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.concurrent.stdlib.rule.SpecificRules
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.stdlib.primitive.Arg
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticEqual
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticNotEqual
import it.unibo.tuprolog.solve.stdlib.primitive.Atom
import it.unibo.tuprolog.solve.stdlib.primitive.AtomChars
import it.unibo.tuprolog.solve.stdlib.primitive.AtomCodes
import it.unibo.tuprolog.solve.stdlib.primitive.AtomConcat
import it.unibo.tuprolog.solve.stdlib.primitive.AtomLength
import it.unibo.tuprolog.solve.stdlib.primitive.Atomic
import it.unibo.tuprolog.solve.stdlib.primitive.BagOf
import it.unibo.tuprolog.solve.stdlib.primitive.Between
import it.unibo.tuprolog.solve.stdlib.primitive.Callable
import it.unibo.tuprolog.solve.stdlib.primitive.CharCode
import it.unibo.tuprolog.solve.stdlib.primitive.Clause
import it.unibo.tuprolog.solve.stdlib.primitive.Compound
import it.unibo.tuprolog.solve.stdlib.primitive.CopyTerm
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentOp
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.solve.stdlib.primitive.FindAll
import it.unibo.tuprolog.solve.stdlib.primitive.Float
import it.unibo.tuprolog.solve.stdlib.primitive.Functor
import it.unibo.tuprolog.solve.stdlib.primitive.GetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.Ground
import it.unibo.tuprolog.solve.stdlib.primitive.Halt
import it.unibo.tuprolog.solve.stdlib.primitive.Halt1
import it.unibo.tuprolog.solve.stdlib.primitive.Integer
import it.unibo.tuprolog.solve.stdlib.primitive.Is
import it.unibo.tuprolog.solve.stdlib.primitive.Natural
import it.unibo.tuprolog.solve.stdlib.primitive.NewLine
import it.unibo.tuprolog.solve.stdlib.primitive.NonVar
import it.unibo.tuprolog.solve.stdlib.primitive.NotUnifiableWith
import it.unibo.tuprolog.solve.stdlib.primitive.Number
import it.unibo.tuprolog.solve.stdlib.primitive.NumberChars
import it.unibo.tuprolog.solve.stdlib.primitive.NumberCodes
import it.unibo.tuprolog.solve.stdlib.primitive.Op
import it.unibo.tuprolog.solve.stdlib.primitive.Repeat
import it.unibo.tuprolog.solve.stdlib.primitive.Reverse
import it.unibo.tuprolog.solve.stdlib.primitive.SetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.solve.stdlib.primitive.SetOf
import it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.SubAtom
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermIdentical
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermNotIdentical
import it.unibo.tuprolog.solve.stdlib.primitive.TermNotSame
import it.unibo.tuprolog.solve.stdlib.primitive.TermSame
import it.unibo.tuprolog.solve.stdlib.primitive.UnifiesWith
import it.unibo.tuprolog.solve.stdlib.primitive.Univ
import it.unibo.tuprolog.solve.stdlib.primitive.Var
import it.unibo.tuprolog.theory.Theory

object DefaultBuiltins : AliasedLibrary by CommonBuiltins {

    override val theory: Theory by lazy {
        CommonBuiltins.theory + SpecificRules.theory
    }

    override val primitives: Map<Signature, Primitive> by lazy {
        sequenceOf(
            // Abolish,
            Arg,
            ArithmeticEqual,
            ArithmeticGreaterThan,
            ArithmeticGreaterThanOrEqualTo,
            ArithmeticLowerThan,
            ArithmeticLowerThanOrEqualTo,
            ArithmeticNotEqual,
            // Assert,
            // AssertA,
            // AssertZ,
            Atom,
            AtomChars,
            AtomCodes,
            AtomConcat,
            Atomic,
            AtomLength,
            Between,
            BagOf,
            Callable,
            CharCode,
            Clause,
            Compound,
            CopyTerm,
            CurrentOp,
            CurrentFlag,
            EnsureExecutable,
            FindAll,
            Float,
            Functor,
            GetDurable,
            GetEphemeral,
            GetPersistent,
            Ground,
            Halt,
            Halt1,
            Integer,
            Is,
            Natural,
            NewLine,
            NonVar,
            NotUnifiableWith,
            Number,
            NumberChars,
            NumberCodes,
            Op,
            Or,
            Repeat,
            // Retract,
            // RetractAll,
            Reverse,
            SetDurable,
            SetEphemeral,
            SetOf,
            SetPersistent,
            SetFlag,
            // Sleep,
            SubAtom,
            TermGreaterThan,
            TermGreaterThanOrEqualTo,
            TermIdentical,
            TermLowerThan,
            TermLowerThanOrEqualTo,
            TermNotIdentical,
            TermNotSame,
            TermSame,
            Throw, // Specific impl
            UnifiesWith,
            Univ,
            Var,
            // Write
        ).map { it.descriptionPair }.toMap()
    }
}
