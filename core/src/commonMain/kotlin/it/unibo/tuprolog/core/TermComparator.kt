package it.unibo.tuprolog.core

/**
 * A [Comparator] for a specific sort of [Term], following the standard logic-term total order: variables
 * order before numbers, which order before atoms, which order before compound terms (compared first by `arity`,
 * then `functor`, then arguments left-to-right; a [Pattern]'s variable functor orders before any atom functor). [DefaultComparator] implements the full order across any
 * two [Term]s and backs [Term.compareTo]; the other nested objects handle one specific sub-type each, and are
 * mostly useful when only same-sort terms are ever compared (e.g. sorting a list of [Atom]s).
 */
interface TermComparator<T : Term> : Comparator<T> {
    /** Compares two [Atom]s by their [Atom.value], independently of the current locale. */
    object AtomComparator : TermComparator<Atom> {
        override fun compare(
            a: Atom,
            b: Atom,
        ): Int = compareStringsLocaleIndependently(a.value, b.value)
    }

    /** Compares two [Var]s by their [Var.completeName], independently of the current locale. */
    object VarComparator : TermComparator<Var> {
        override fun compare(
            a: Var,
            b: Var,
        ): Int = compareStringsLocaleIndependently(a.completeName, b.completeName)
    }

    /** Compares two [Real]s by their [Real.value]. */
    object RealComparator : TermComparator<Real> {
        override fun compare(
            a: Real,
            b: Real,
        ): Int = a.decimalValue.compareTo(b.decimalValue)
    }

    /** Compares two [Integer]s by their [Integer.value]. */
    object IntegerComparator : TermComparator<Integer> {
        override fun compare(
            a: Integer,
            b: Integer,
        ): Int = a.intValue.compareTo(b.intValue)
    }

    /**
     * Implements the standard logic-term total order across any two [Term]s: variables order before numbers,
     * which order before atoms, which order before structures. This is what backs [Term.compareTo].
     */
    object DefaultComparator : TermComparator<Term> {
        private fun compareVarAndTerm(
            a: Var,
            b: Term,
        ): Int =
            when {
                b.isVar -> VarComparator.compare(a, b.castToVar())
                else -> -1
            }

        private fun compareRealAndTerm(
            a: Real,
            b: Term,
        ): Int =
            when {
                b.isReal -> RealComparator.compare(a, b.castToReal())
                b.isVar -> 1
                else -> -1
            }

        private fun compareIntegerAndTerm(
            a: Integer,
            b: Term,
        ): Int =
            when {
                b.isInteger -> IntegerComparator.compare(a, b.castToInteger())
                b.isVar || b.isReal -> 1
                else -> -1
            }

        private fun compareAtomAndTerm(
            a: Atom,
            b: Term,
        ): Int =
            when {
                b.isAtom -> AtomComparator.compare(a, b.castToAtom())
                b.isCompound -> -1
                else -> 1
            }

        private fun compareCompoundAndTerm(
            a: Compound,
            b: Term,
        ): Int =
            when {
                b.isCompound -> CompoundComparator.compare(a, b.castToCompound())
                else -> 1
            }

        override fun compare(
            a: Term,
            b: Term,
        ): Int =
            when {
                a.isVar -> compareVarAndTerm(a.castToVar(), b)
                a.isReal -> compareRealAndTerm(a.castToReal(), b)
                a.isInteger -> compareIntegerAndTerm(a.castToInteger(), b)
                a.isAtom -> compareAtomAndTerm(a.castToAtom(), b)
                a.isCompound -> compareCompoundAndTerm(a.castToCompound(), b)
                else -> throw IllegalStateException("Cannot compare ${a::class} with ${b::class}. This is a bug.")
            }
    }

    /** Compares two [Struct]s first by [Struct.arity], then by [Struct.functor], then argument-wise, left to right. */
    object StructComparator : TermComparator<Struct> {
        override fun compare(
            a: Struct,
            b: Struct,
        ): Int = CompoundComparator.compare(a, b)
    }

    /**
     * Compares two [Compound]s first by [Compound.arity], then by functor, then argument-wise, left to right.
     * [Struct] functors are compared as strings, [Pattern] functors as variables, and any variable functor orders
     * before any [String] functor.
     */
    object CompoundComparator : TermComparator<Compound> {
        private fun compareFunctors(
            a: Compound,
            b: Compound,
        ): Int =
            when {
                a.isPattern && b.isPattern ->
                    VarComparator.compare(
                        a.castToPattern().functor,
                        b.castToPattern().functor,
                    )
                a.isPattern -> -1
                b.isPattern -> 1
                else -> a.castToStruct().functor.compareTo(b.castToStruct().functor)
            }

        override fun compare(
            a: Compound,
            b: Compound,
        ): Int {
            // The last argument is compared by looping rather than by recursion, so that right-nested terms
            // (lists, tuples, blocks) take O(1) native stack.
            var x = a
            var y = b
            while (true) {
                if (x === y) return 0
                val arityDelta = x.arity - y.arity
                if (arityDelta != 0) return arityDelta
                val functorComparison = compareFunctors(x, y)
                if (functorComparison != 0) return functorComparison
                val last = x.arity - 1
                if (last < 0) return 0
                for (i in 0 until last) {
                    val ithArgComparison = DefaultComparator.compare(x[i], y[i])
                    if (ithArgComparison != 0) return ithArgComparison
                }
                val lastOfX = x[last]
                val lastOfY = y[last]
                if (!lastOfX.isCompound || !lastOfY.isCompound || lastOfX.isAtom || lastOfY.isAtom) {
                    return DefaultComparator.compare(lastOfX, lastOfY)
                }
                x = lastOfX.castToCompound()
                y = lastOfY.castToCompound()
            }
        }
    }
}
