package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.collections.List as KtList

private class CompoundCase(
    val id: String,
    val compound: Term,
    val expected: Term,
)

class TermSpec :
    FunSpec({
        @Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
        val X = Var.of("X")

        val sub = Substitution.unifier(X, Integer.ONE)

        val compoundCases =
            listOf(
                CompoundCase("custom-struct", MyStruct("f", listOf(X)), MyStruct("f", listOf(Integer.ONE))),
                CompoundCase("struct", Struct.of("g", X), Struct.of("g", Integer.ONE)),
                CompoundCase("list", List.of(X), List.of(Integer.ONE)),
                CompoundCase("block", Block.of(X), Block.of(Integer.ONE)),
                CompoundCase("tuple", Tuple.of(X, X), Tuple.of(Integer.ONE, Integer.ONE)),
                CompoundCase(
                    "fact-of-custom-struct",
                    Fact.of(MyStruct("f", listOf(X))),
                    Fact.of(MyStruct("f", listOf(Integer.ONE))),
                ),
                CompoundCase("fact-of-struct", Fact.of(Struct.of("g", X)), Fact.of(Struct.of("g", Integer.ONE))),
                CompoundCase("fact-of-list", Fact.of(List.of(X)), Fact.of(List.of(Integer.ONE))),
                CompoundCase("fact-of-block", Fact.of(Block.of(X)), Fact.of(Block.of(Integer.ONE))),
                CompoundCase("fact-of-tuple", Fact.of(Tuple.of(X, X)), Fact.of(Tuple.of(Integer.ONE, Integer.ONE))),
            )

        fun compounds(
            property: String,
            test: (CompoundCase) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, compoundCases) { test(it) }

        compounds("substitution-apply-to-returns-null-with-failed-substitution") {
            assertNull(Substitution.failed().applyTo(it.compound))
        }

        compounds("term-apply-throw-exception-with-failed-substitution") {
            val e = assertFailsWith<SubstitutionApplicationException> { it.compound.apply(Substitution.failed()) }
            assertSame(it.compound, e.term)
            assertEquals(Substitution.failed(), e.substitution)
        }

        compounds("term-get-throw-exception-with-failed-substitution") {
            val e = assertFailsWith<SubstitutionApplicationException> { it.compound[Substitution.failed()] }
            assertSame(it.compound, e.term)
            assertEquals(Substitution.failed(), e.substitution)
        }

        compounds("substitution-apply-to-preserves-type") { assertEquals(it.expected, sub.applyTo(it.compound)) }

        compounds("term-apply-preserves-type") { assertEquals(it.expected, it.compound.apply(sub)) }

        compounds("term-get-preserves-type") { assertEquals(it.expected, it.compound[sub]) }

        compounds("term-fresh-copy-preserves-type") {
            assertTrue { it.compound.freshCopy().equals(it.compound, useVarCompleteName = false) }
        }

        compounds("term-fresh-copy-with-scope-preserves-type") {
            assertTrue { it.compound.freshCopy(Scope.of("Y")).equals(it.compound, useVarCompleteName = false) }
        }
    })

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
