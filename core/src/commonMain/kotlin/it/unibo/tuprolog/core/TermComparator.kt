package it.unibo.tuprolog.core

interface TermComparator<T : Term> : Comparator<T> {
    object AtomComparator : TermComparator<Atom> {
        override fun compare(a: Atom, b: Atom): Int =
            compareStringsLocaleIndependently(a.value, b.value)
    }

    object VarComparator : TermComparator<Var> {
        override fun compare(a: Var, b: Var): Int =
            compareStringsLocaleIndependently(a.completeName, b.completeName)
    }

    object RealComparator : TermComparator<Real> {
        override fun compare(a: Real, b: Real): Int =
            a.decimalValue.compareTo(b.decimalValue)
    }

    object IntegerComparator : TermComparator<Integer> {
        override fun compare(a: Integer, b: Integer): Int =
            a.intValue.compareTo(b.intValue)
    }

    object DefaultComparator : TermComparator<Term> {

        private fun compareVarAndTerm(a: Var, b: Term): Int =
            when {
                b.isVariable -> VarComparator.compare(a, b.castToVar())
                else -> -1
            }

        private fun compareRealAndTerm(a: Real, b: Term): Int =
            when {
                b.isReal -> RealComparator.compare(a, b.castToReal())
                b.isVariable -> 1
                else -> -1
            }

        private fun compareIntegerAndTerm(a: Integer, b: Term): Int =
            when {
                b.isInt -> IntegerComparator.compare(a, b.castToInteger())
                b.isVariable || b.isReal -> 1
                else -> -1
            }

        private fun compareAtomAndTerm(a: Atom, b: Term): Int =
            when {
                b.isAtom -> AtomComparator.compare(a, b.castToAtom())
                b.isStruct -> -1
                else -> 1
            }

        private fun compareStructAndTerm(a: Struct, b: Term): Int =
            when {
                b.isStruct -> StructComparator.compare(a, b.castToStruct())
                else -> 1
            }

        override fun compare(a: Term, b: Term): Int =
            when {
                a.isVariable -> compareVarAndTerm(a.castToVar(), b)
                a.isReal -> compareRealAndTerm(a.castToReal(), b)
                a.isInt -> compareIntegerAndTerm(a.castToInteger(), b)
                a.isAtom -> compareAtomAndTerm(a.castToAtom(), b)
                a.isStruct -> compareStructAndTerm(a.castToStruct(), b)
                else -> throw IllegalStateException("Cannot compare ${a::class} with ${b::class}. This is a bug.")
            }
    }

    object StructComparator : TermComparator<Struct> {
        override fun compare(a: Struct, b: Struct): Int {
            return when (val arityDelta = a.arity - b.arity) {
                0 -> when (val functorComparison = a.functor.compareTo(b.functor)) {
                    0 -> {
                        for (i in 0 until a.arity) {
                            val ithArgComparison = DefaultComparator.compare(a[i], b[i])
                            if (ithArgComparison != 0) {
                                return ithArgComparison
                            }
                        }
                        return 0
                    }
                    else -> functorComparison
                }
                else -> arityDelta
            }
        }
    }
}
