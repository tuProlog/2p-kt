package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.StructImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropLast
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/**
 * Test class for [Struct] companion object
 *
 * @author Enrico
 */
internal class StructTest {

    private val correctNonSpecialStructInstances =
        StructUtils.nonSpecialStructs.map { (functor, args) -> StructImpl(functor, args) }

    @Test
    fun structOfShouldCreateSubClassInstanceWithSpecialStructs() {
        val specialStructInstances = StructUtils.specialStructs.map { (functor, args) -> Struct.of(functor, *args) }
        specialStructInstances.forEach { assertNotEquals(StructImpl::class, it::class) }
    }

    @Test
    fun structOfCreatesCorrectNonSpecificStructInstances() {
        onCorrespondingItems(
            correctNonSpecialStructInstances,
            StructUtils.nonSpecialStructs.map { (functor, args) -> Struct.of(functor, args.asSequence()) },
            ::assertEqualities
        )
    }

    @Test
    fun structOfCreatesConsIfNeeded() {
        val consStructs = StructUtils.mixedStructs.filter { (functor, args) ->
            args.size == 2 && Cons.FUNCTOR == functor
        }

        val correctInstances = consStructs.map { (_, args) -> Cons.of(args.first(), args.last()) }
        val toBeTested = consStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfCreatesRuleIfNeeded() {
        val ruleStructs = StructUtils.mixedStructs.filter { (functor, args) ->
            args.size == 2 && Clause.FUNCTOR == functor && args.first() is Struct
        }

        val correctInstances = ruleStructs.map { (_, args) -> Rule.of(args.first() as Struct, args.last()) }
        val toBeTested = ruleStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfCreatesTupleIfNeeded() {
        val tupleStructs = StructUtils.mixedStructs.filter { (functor, args) ->
            args.size == 2 && Tuple.FUNCTOR == functor
        }

        val correctInstances = tupleStructs.map { (_, args) -> Tuple.of(args.toList()) }
        val toBeTested = tupleStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfCreatesSetIfNeeded() {
        val setStructs = StructUtils.mixedStructs.filter { (functor, args) ->
            args.size == 1 && Set.FUNCTOR == functor
        }

        val correctInstances = setStructs.map { (_, args) -> Set.of(*args) }
        val toBeTested = setStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfCreatesDirectiveIfNeeded() {
        val directiveStructs = StructUtils.mixedStructs.filter { (functor, args) ->
            args.size == 1 && Clause.FUNCTOR == functor
        }

        val correctInstances = directiveStructs.map { (_, args) -> Directive.of(args.first()) }
        val toBeTested = directiveStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfCreatesAtomIfNeeded() {
        val atomStructs = StructUtils.mixedStructs.filter { (_, args) -> args.isEmpty() }

        val correctInstances = atomStructs.map { (functor, _) -> Atom.of(functor) }
        val toBeTested = atomStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structOfCreatesIndicatorIfNeeded() {
        val indicatorStructs = StructUtils.mixedStructs.filter { (functor, args) ->
            args.size == 2 && Indicator.FUNCTOR == functor
        }

        val correctInstances = indicatorStructs.map { (_, args) -> Indicator.of(args.first(), args.last()) }
        val toBeTested = indicatorStructs.map { (functor, args) -> Struct.of(functor, *args) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structFoldCanCreateFoldedConsInstancesEmptyListTerminated() {
        val correctInstances = ConsUtils.onlyConsEmptyListTerminatedElementLists.map { List.of(it) }
        val toBeTested =
            ConsUtils.onlyConsEmptyListTerminatedElementLists.map { Struct.fold(Cons.FUNCTOR, it, EmptyList()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structFoldCanCreateFoldedConsInstancesPipeTerminated() {
        val correctInstances = ConsUtils.onlyConsPipeTerminatedElementLists.map { List.from(it.dropLast(), it.last()) }
        val toBeTested = ConsUtils.onlyConsPipeTerminatedElementLists.map { Struct.fold(Cons.FUNCTOR, it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structFoldCanCreateTupleWithoutSpecifyingTerminal() {
        val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it) }
        val toBeTested = TupleUtils.tupleInstancesElementLists.map { Struct.fold(Tuple.FUNCTOR, it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structFoldCanCreateTupleSpecifyingTerminal() {
        val correctInstances = TupleUtils.tupleInstancesElementLists.map { Tuple.of(it) }
        val toBeTested =
            TupleUtils.tupleInstancesElementLists.map { Struct.fold(Tuple.FUNCTOR, it.dropLast(), it.last()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun structFoldWithoutTerminalRequiresTwoOrMoreTerms() {
        assertFailsWith<IllegalArgumentException> { Struct.fold("f", emptySequence()) }
        assertFailsWith<IllegalArgumentException> { Struct.fold("f", sequenceOf(Var.anonymous())) }
        Struct.fold("f", sequenceOf(Var.anonymous(), Var.anonymous()))
    }

    @Test
    fun structOfWithTerminalRequiresOneOrMoreTerms() {
        assertFailsWith<IllegalArgumentException> { Struct.fold("f", emptySequence(), Var.anonymous()) }
        Struct.fold("f", sequenceOf(Var.anonymous()), Var.anonymous())
    }


    private val arbitraryFoldedStructElements = listOf(Atom.of("hello"), Atom.of("world"), Atom.of("!"))
    private val arbitraryFoldedStructCorrectInstance =
        StructImpl(
            "f", arrayOf(
                Atom.of("hello"),
                StructImpl(
                    "f", arrayOf(
                        Atom.of("world"),
                        Atom.of("!")
                    )
                )
            )
        )

    @Test
    fun structFoldCreatesArbitraryFoldedStructsWithoutSpecifyingTerminal() {
        val toBeTested = Struct.fold("f", *arbitraryFoldedStructElements.toTypedArray())

        assertEqualities(arbitraryFoldedStructCorrectInstance, toBeTested)
    }

    @Test
    fun structFoldCreatesArbitraryFoldedStructsSpecifyingTerminal() {
        val toBeTested = Struct.fold(
            "f",
            arbitraryFoldedStructElements.dropLast().asIterable(),
            arbitraryFoldedStructElements.last()
        )

        assertEqualities(arbitraryFoldedStructCorrectInstance, toBeTested)
    }
}
