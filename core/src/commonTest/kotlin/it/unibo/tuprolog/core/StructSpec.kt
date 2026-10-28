package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.StructImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualsUsingVariablesSimpleNames
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNoEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropLast
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

/**
 * Test class for [Struct] companion object
 *
 * @author Enrico
 */
class StructSpec :
    FunSpec({
        val arbitraryFoldedStructElements = listOf(Atom.of("hello"), Atom.of("world"), Atom.of("!"))

        val arbitraryFoldedStructCorrectInstance =
            StructImpl(
                "f",
                arrayOf(
                    Atom.of("hello"),
                    StructImpl(
                        "f",
                        arrayOf(
                            Atom.of("world"),
                            Atom.of("!"),
                        ),
                    ),
                ),
            )

        fun structsOf(
            kind: String,
            filter: (StructUtils.StructCase) -> Boolean,
            expected: (StructUtils.StructCase) -> Term,
        ) = withTests(
            nameFn = { "of/creates-$kind/${it.id}" },
            StructUtils.mixedStructCases.filter(filter),
        ) { case ->
            assertEqualities(expected(case), Struct.of(case.functor, case.args))
        }

        withTests(nameFn = { "of/special/${it.id}" }, StructUtils.specialStructCases) { case ->
            assertNotEquals(StructImpl::class, Struct.of(case.functor, case.args)::class)
        }

        withTests(nameFn = { "of/non-special/${it.id}" }, StructUtils.nonSpecialStructCases) { case ->
            assertEqualities(StructImpl(case.functor, case.args), Struct.of(case.functor, case.args.asSequence()))
        }

        structsOf(
            "cons",
            { it.args.size == 2 && Cons.FUNCTOR == it.functor },
        ) { Cons.of(it.args.first(), it.args.last()) }

        structsOf("rule", { it.args.size == 2 && Clause.FUNCTOR == it.functor && it.args.first() is Struct }) {
            Rule.of(it.args.first() as Struct, it.args.last())
        }

        structsOf("tuple", { it.args.size == 2 && Tuple.FUNCTOR == it.functor }) { Tuple.of(it.args) }

        structsOf("block", { it.args.size == 1 && Block.FUNCTOR == it.functor }) { Block.of(it.args) }

        structsOf("directive", { it.args.size == 1 && Clause.FUNCTOR == it.functor }) { Directive.of(it.args.first()) }

        structsOf("atom", { it.args.isEmpty() }) { Atom.of(it.functor) }

        structsOf("indicator", { it.args.size == 2 && Indicator.FUNCTOR == it.functor }) {
            Indicator.of(it.args.first(), it.args.last())
        }

        test("struct-fold-can-create-folded-cons-instances-empty-list-terminated") {
            val correctInstances = ConsUtils.onlyConsEmptyListTerminatedElementLists.map { List.of(it) }
            val toBeTested =
                ConsUtils.onlyConsEmptyListTerminatedElementLists.map { Struct.fold(Cons.FUNCTOR, it, EmptyList()) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-fold-can-create-folded-cons-instances-pipe-terminated") {
            val correctInstances = ConsUtils.onlyConsPipeTerminatedElementLists.map { List.from(it) }
            val toBeTested = ConsUtils.onlyConsPipeTerminatedElementLists.map { Struct.fold(Cons.FUNCTOR, it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-fold-can-create-tuple-without-specifying-terminal") {
            val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it) }
            val toBeTested = TupleUtils.tupleInstancesElementLists.map { Struct.fold(Tuple.FUNCTOR, it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-fold-can-create-tuple-specifying-terminal") {
            val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it) }
            val toBeTested =
                TupleUtils.tupleInstancesElementLists.map { Struct.fold(Tuple.FUNCTOR, it.dropLast(), it.last()) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-fold-without-terminal-requires-two-or-more-terms") {
            assertFailsWith<IllegalArgumentException> { Struct.fold("f", emptySequence()) }
            assertFailsWith<IllegalArgumentException> { Struct.fold("f", sequenceOf(Var.anonymous())) }
            Struct.fold("f", sequenceOf(Var.anonymous(), Var.anonymous()))
        }

        test("struct-of-with-terminal-requires-one-or-more-terms") {
            assertFailsWith<IllegalArgumentException> { Struct.fold("f", emptySequence(), Var.anonymous()) }
            Struct.fold("f", sequenceOf(Var.anonymous()), Var.anonymous())
        }

        test("struct-fold-creates-arbitrary-folded-structs-without-specifying-terminal") {
            val toBeTested = Struct.fold("f", *arbitraryFoldedStructElements.toTypedArray())

            assertEqualities(arbitraryFoldedStructCorrectInstance, toBeTested)
        }

        test("struct-fold-creates-arbitrary-folded-structs-specifying-terminal") {
            val toBeTested =
                Struct.fold(
                    "f",
                    arbitraryFoldedStructElements.dropLast().asIterable(),
                    arbitraryFoldedStructElements.last(),
                )

            assertEqualities(arbitraryFoldedStructCorrectInstance, toBeTested)
        }

        test("equality-of-non-ground-structs-works-as-expected") {
            val f = Struct.of("f", Var.anonymous())
            val g = Struct.of("g", Var.anonymous())

            assertEqualities(f, f)
            assertNoEqualities(f, g)

            assertEqualsUsingVariablesSimpleNames(Struct.of("f", Var.of("_")), f)
            assertStructurallyEquals(Struct.of("f", Var.of("_")), f)
            assertEqualsUsingVariablesSimpleNames(Struct.of("f", Var.anonymous()), f)
            assertStructurallyEquals(Struct.of("f", Var.anonymous()), f)

            assertNotEquals(Struct.of("f", Var.of("_")), f)
            assertNotSame(Struct.of("f", Var.of("_")), f)
            assertNotEquals(Struct.of("f", Var.anonymous()), f)
            assertNotSame(Struct.of("f", Var.anonymous()), f)

            val fX = Struct.of("f", Var.of("X"))
            val gX = Struct.of("g", Var.of("X"))

            assertEqualities(fX, fX)
            assertNoEqualities(fX, gX)

            assertEqualsUsingVariablesSimpleNames(Struct.of("f", Var.of("X")), fX)
            assertStructurallyEquals(Struct.of("f", Var.of("X")), fX)

            assertNotEquals(Struct.of("f", Var.of("X")), fX)
            assertNotSame(Struct.of("f", Var.of("X")), fX)
        }
    })
