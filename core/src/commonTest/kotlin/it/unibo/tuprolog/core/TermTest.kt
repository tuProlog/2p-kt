package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.collections.List as KtList

class TermTest {
    private data class MyStruct(
        override val functor: String,
        override val args: KtList<Term>,
    ) : Struct {
        override fun freshCopy(): Struct = freshCopy(Scope.empty())

        override fun freshCopy(scope: Scope): Struct = MyStruct(functor, args.map { it.freshCopy(scope) })

        override fun addLast(argument: Term): Struct = throw NotImplementedError()

        override fun addFirst(argument: Term): Struct = throw NotImplementedError()

        override fun insertAt(
            index: Int,
            argument: Term,
        ): Struct = throw NotImplementedError()

        override fun setFunctor(functor: String): Struct = throw NotImplementedError()

        override val isFunctorWellFormed: Boolean
            get() = Struct.isWellFormedFunctor(functor)

        override fun setArgs(vararg args: Term): Struct = throw NotImplementedError()

        override fun setArgs(args: Iterable<Term>): Struct = throw NotImplementedError()

        override fun setArgs(args: Sequence<Term>): Struct = throw NotImplementedError()

        override fun equals(other: Any?): Boolean = if (other is Term) equals(other, true) else false

        override fun equals(
            other: Term,
            useVarCompleteName: Boolean,
        ): Boolean =
            other is MyStruct &&
                other.arity == arity &&
                other.functor == functor &&
                other.args.mapIndexed { i, term -> term.equals(args[i], useVarCompleteName) }.all { it }

        override fun structurallyEquals(other: Term): Boolean =
            other is MyStruct &&
                other.arity == arity &&
                other.functor == functor &&
                other.args.mapIndexed { i, term -> term.structurallyEquals(args[i]) }.all { it }

        override val tags: Map<String, Any>
            get() = emptyMap()

        override fun replaceTags(tags: Map<String, Any>): Term = this

        override fun apply(substitution: Substitution): Term =
            if (substitution.isFailed) {
                throw SubstitutionApplicationException(this, substitution)
            } else {
                MyStruct(functor, args.map { it[substitution] })
            }

        override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitStruct(this)
    }

    @Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
    private val X = Var.of("X")

    private val sub = Substitution.unifier(X, Integer.ONE)

    private val compounds =
        listOf(
            MyStruct("f", listOf(X)),
            Struct.of("g", X),
            List.of(X),
            Block.of(X),
            Tuple.of(X, X),
            Fact.of(MyStruct("f", listOf(X))),
            Fact.of(Struct.of("g", X)),
            Fact.of(List.of(X)),
            Fact.of(Block.of(X)),
            Fact.of(Tuple.of(X, X)),
        )

    private val expected =
        listOf(
            MyStruct("f", listOf(Integer.ONE)),
            Struct.of("g", Integer.ONE),
            List.of(Integer.ONE),
            Block.of(Integer.ONE),
            Tuple.of(Integer.ONE, Integer.ONE),
            Fact.of(MyStruct("f", listOf(Integer.ONE))),
            Fact.of(Struct.of("g", Integer.ONE)),
            Fact.of(List.of(Integer.ONE)),
            Fact.of(Block.of(Integer.ONE)),
            Fact.of(Tuple.of(Integer.ONE, Integer.ONE)),
        )

    @Test
    fun substitutionApplyToPreservesReturnsNullWithFailedSubstitution() {
        for (i in compounds.indices) {
            assertNull(Substitution.failed().applyTo(compounds[i]))
        }
    }

    @Test
    fun termApplyThrowExceptionWithFailedSubstitution() {
        for (i in compounds.indices) {
            try {
                compounds[i].apply(Substitution.failed())
            } catch (e: SubstitutionApplicationException) {
                assertSame(compounds[i], e.term)
                assertEquals(Substitution.failed(), e.substitution)
            }
        }
    }

    @Test
    fun termGetThrowExceptionWithFailedSubstitution() {
        for (i in compounds.indices) {
            try {
                compounds[i][Substitution.failed()]
            } catch (e: SubstitutionApplicationException) {
                assertSame(compounds[i], e.term)
                assertEquals(Substitution.failed(), e.substitution)
            }
        }
    }

    @Test
    fun substitutionApplyToPreservesType() {
        for (i in compounds.indices) {
            assertEquals(expected[i], sub.applyTo(compounds[i]))
        }
    }

    @Test
    fun termApplyPreservesType() {
        for (i in compounds.indices) {
            assertEquals(expected[i], compounds[i].apply(sub))
        }
    }

    @Test
    fun termGetPreservesType() {
        for (i in compounds.indices) {
            assertEquals(expected[i], compounds[i][sub])
        }
    }

    @Test
    fun termFreshCopyPreservesType() {
        for (i in compounds.indices) {
            assertTrue { compounds[i].freshCopy().equals(compounds[i], useVarCompleteName = false) }
        }
    }

    @Test
    fun termFreshCopyWithScopePreservesType() {
        for (i in compounds.indices) {
            assertTrue { compounds[i].freshCopy(Scope.of("Y")).equals(compounds[i], useVarCompleteName = false) }
        }
    }
}
