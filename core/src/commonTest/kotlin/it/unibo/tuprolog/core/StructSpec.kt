package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
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
        val correctNonSpecialStructInstances =
            StructUtils.nonSpecialStructs.map { (functor, args) -> StructImpl(functor, args) }

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

        test("struct-of-should-create-sub-class-instance-with-special-structs") {
            val specialStructInstances = StructUtils.specialStructs.map { (functor, args) -> Struct.of(functor, args) }
            specialStructInstances.forEach { assertNotEquals(StructImpl::class, it::class) }
        }

        test("struct-of-creates-correct-non-specific-struct-instances") {
            onCorrespondingItems(
                correctNonSpecialStructInstances,
                StructUtils.nonSpecialStructs.map { (functor, args) -> Struct.of(functor, args.asSequence()) },
                ::assertEqualities,
            )
        }

        test("struct-of-creates-cons-if-needed") {
            val consStructs =
                StructUtils.mixedStructs.filter { (functor, args) ->
                    args.size == 2 && Cons.FUNCTOR == functor
                }

            val correctInstances = consStructs.map { (_, args) -> Cons.of(args.first(), args.last()) }
            val toBeTested = consStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-creates-rule-if-needed") {
            val ruleStructs =
                StructUtils.mixedStructs.filter { (functor, args) ->
                    args.size == 2 && Clause.FUNCTOR == functor && args.first() is Struct
                }

            val correctInstances = ruleStructs.map { (_, args) -> Rule.of(args.first() as Struct, args.last()) }
            val toBeTested = ruleStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-creates-tuple-if-needed") {
            val tupleStructs =
                StructUtils.mixedStructs.filter { (functor, args) ->
                    args.size == 2 && Tuple.FUNCTOR == functor
                }

            val correctInstances = tupleStructs.map { (_, args) -> Tuple.of(args.toList()) }
            val toBeTested = tupleStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-creates-set-if-needed") {
            val setStructs =
                StructUtils.mixedStructs.filter { (functor, args) ->
                    args.size == 1 && Block.FUNCTOR == functor
                }

            val correctInstances = setStructs.map { (_, args) -> Block.of(args) }
            val toBeTested = setStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-creates-directive-if-needed") {
            val directiveStructs =
                StructUtils.mixedStructs.filter { (functor, args) ->
                    args.size == 1 && Clause.FUNCTOR == functor
                }

            val correctInstances = directiveStructs.map { (_, args) -> Directive.of(args.first()) }
            val toBeTested = directiveStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-creates-atom-if-needed") {
            val atomStructs = StructUtils.mixedStructs.filter { (_, args) -> args.isEmpty() }

            val correctInstances = atomStructs.map { (functor, _) -> Atom.of(functor) }
            val toBeTested = atomStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("struct-of-creates-indicator-if-needed") {
            val indicatorStructs =
                StructUtils.mixedStructs.filter { (functor, args) ->
                    args.size == 2 && Indicator.FUNCTOR == functor
                }

            val correctInstances = indicatorStructs.map { (_, args) -> Indicator.of(args.first(), args.last()) }
            val toBeTested = indicatorStructs.map { (functor, args) -> Struct.of(functor, args) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
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
