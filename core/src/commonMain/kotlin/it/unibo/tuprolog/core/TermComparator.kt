package it.unibo.tuprolog.core

interface TermComparator<T : Term> : Comparator<T> {
    object AtomComparator : TermComparator<Atom> {
        override fun compare(a: Atom, b: Atom): Int =
            a.value.compareTo(b.value)
    }

    object VarComparator : TermComparator<Var> {
        override fun compare(a: Var, b: Var): Int =
            a.completeName.compareTo(b.completeName)
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
            when (b) {
                is Var -> VarComparator.compare(a, b)
                else -> 1
            }

        private fun compareRealAndTerm(a: Real, b: Term): Int =
            when (b) {
                is Real -> RealComparator.compare(a, b)
                is Var -> -1
                else -> 1
            }

        private fun compareIntegerAndTerm(a: Integer, b: Term): Int =
            when (b) {
                is Integer -> IntegerComparator.compare(a, b)
                is Var, is Real -> -1
                else -> -1
            }

        private fun compareAtomAndTerm(a: Atom, b: Term): Int =
            when (b) {
                is Atom -> AtomComparator.compare(a, b)
                is Struct -> 1
                else -> -1
            }

        private fun compareStructAndTerm(a: Struct, b: Term): Int =
            when (b) {
                is Struct -> StructComparator.compare(a, b)
                else -> -1
            }

        override fun compare(a: Term, b: Term): Int =
            when (a) {
                is Var -> compareVarAndTerm(a, b)
                is Real -> compareRealAndTerm(a, b)
                is Integer -> compareIntegerAndTerm(a, b)
                is Atom -> compareAtomAndTerm(a, b)
                is Struct -> compareStructAndTerm(a, b)
                else -> throw IllegalStateException("Cannot compare ${a::class} with ${b::class}. This is a bug.")
            }
    }

    object StructComparator : TermComparator<Struct> {
        override fun compare(a: Struct, b: Struct): Int {
            return when (val arityDelta = a.arity - b.arity) {
                0 -> when (val functorComparison = a.functor.compareTo(b.functor)) {
                    0 -> {
                        for (i in 0 until a.arity) {
                            val ithArgComparison = DefaultComparator.compare(a, b)
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