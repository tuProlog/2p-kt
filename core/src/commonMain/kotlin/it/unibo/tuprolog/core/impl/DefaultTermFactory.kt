package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFactory
import it.unibo.tuprolog.core.Terms
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.List as LogicList

internal object DefaultTermFactory : TermFactory {
    private val emptyLogicBlock = EmptyBlockImpl()
    private val emptyLogicList = EmptyListImpl()
    private val trueTruth = TruthImpl(Terms.TRUE_FUNCTOR, true)
    private val failedTruth = TruthImpl(Terms.FAIL_FUNCTOR, false)
    private val falseTruth = TruthImpl(Terms.FALSE_FUNCTOR, false)

    override fun fail(): Truth = failedTruth

    override fun emptyLogicList(): EmptyList = emptyLogicList

    override fun emptyBlock(): EmptyBlock = emptyLogicBlock

    override fun varOf(name: String): Var = VarImpl(name)

    override fun varOf(name: Char): Var = varOf(name.toChar())

    override fun atomOf(value: String): Atom =
        when (value) {
            Terms.EMPTY_LIST_FUNCTOR -> Empty.list()
            Terms.EMPTY_BLOCK_FUNCTOR -> Empty.block()
            Terms.TRUE_FUNCTOR -> Truth.TRUE
            Terms.FAIL_FUNCTOR -> Truth.FAIL
            Terms.FALSE_FUNCTOR -> Truth.FALSE
            else -> AtomImpl(value)
        }

    override fun atomOf(value: Char): Atom = atomOf(value.toString())

    override fun structOf(
        functor: String,
        vararg args: Term,
    ): Struct = structOf(functor, args.toList())

    override fun structOf(
        functor: String,
        args: Sequence<Term>,
    ): Struct = structOf(functor, args.toList())

    override fun structOf(
        functor: String,
        args: Iterable<Term>,
    ): Struct =
        when (args) {
            is List<Term> -> structOf(functor, args)
            else -> structOf(functor, args.toList())
        }

    override fun structOf(
        functor: String,
        args: List<Term>,
    ): Struct =
        when {
            args.size == 2 && Terms.CONS_FUNCTOR == functor -> consOf(args.first(), args.last())
            args.size == 2 && Terms.CLAUSE_FUNCTOR == functor && args.first().isStruct ->
                ruleOf(args.first().castToStruct(), args.last())
            args.size == 2 && Terms.TUPLE_FUNCTOR == functor -> tupleOf(args)
            args.size == 2 && Terms.INDICATOR_FUNCTOR == functor -> indicatorOf(args.first(), args.last())
            args.size == 1 && Terms.BLOCK_FUNCTOR == functor -> blockOf(args)
            args.size == 1 && Terms.CLAUSE_FUNCTOR == functor -> directiveOf(args.first())
            args.isEmpty() -> atomOf(functor)
            else -> StructImpl(functor, args)
        }

    override fun structTemplateOf(
        functor: String,
        arity: Int,
    ): Struct {
        require(arity >= 0) { "Arity must be a non-negative integer" }
        return structOf(functor, (0 until arity).map { anonymousVar() })
    }

    override fun foldedStructOf(
        operator: String,
        terms: List<Term>,
    ): Struct = foldedStructOf(operator, terms, null)

    override fun foldedStructOf(
        operator: String,
        terms: List<Term>,
        terminal: Term?,
    ): Struct =
        when {
            operator == Terms.CONS_FUNCTOR && terminal == emptyLogicList -> logicListOf(terms)
            operator == Terms.CONS_FUNCTOR && terminal == null -> logicListFrom(terms)
            operator == Terms.TUPLE_FUNCTOR -> tupleOf(terms + listOfNotNull(terminal))
            terminal == null -> {
                require(terms.size >= 2) { "Struct requires at least two terms to fold" }
                terms.slice(0 until terms.lastIndex - 1)
                    .foldRight(structOf(operator, terms[terms.lastIndex - 1], terms[terms.lastIndex])) { a, b ->
                        structOf(operator, a, b)
                    }
            }
            else -> {
                require(terms.isNotEmpty()) { "Struct requires at least two terms to fold" }
                terms.slice(0 until terms.lastIndex)
                    .foldRight(structOf(operator, terms[terms.lastIndex], terminal)) { a, b ->
                        structOf(operator, a, b)
                    }
            }
        }

    override fun foldedStructOf(
        operator: String,
        terms: Sequence<Term>,
        terminal: Term?,
    ): Struct = foldedStructOf(operator, terms.toList(), terminal)

    override fun foldedStructOf(
        operator: String,
        terms: Sequence<Term>,
    ): Struct = foldedStructOf(operator, terms, null)

    override fun foldedStructOf(
        operator: String,
        terms: Iterable<Term>,
        terminal: Term?,
    ): Struct = foldedStructOf(operator, if (terms is List<Term>) terms else terms.toList(), terminal)

    override fun foldedStructOf(
        operator: String,
        terms: Iterable<Term>,
    ): Struct = foldedStructOf(operator, terms, null)

    override fun foldedStructOf(
        operator: String,
        vararg terms: Term,
        terminal: Term?,
    ): Struct = foldedStructOf(operator, listOf(*terms), terminal)

    override fun foldedStructOf(
        operator: String,
        vararg terms: Term,
    ): Struct = foldedStructOf(operator, listOf(*terms))

    override fun tupleOf(items: Iterable<Term>): Tuple = tupleOf(items.toList())

    override fun tupleOf(items: Sequence<Term>): Tuple = tupleOf(items.toList())

    override fun tupleOf(items: List<Term>): Tuple {
        require(items.size >= 2) { "Tuples require at least 2 terms" }
        return items
            .slice(0 until items.lastIndex)
            .foldRight(items.last(), ::TupleImpl)
            .castToTuple()
    }

    override fun tupleOf(
        first: Term,
        second: Term,
    ): Tuple = TupleImpl(first, second)

    override fun tupleOf(
        first: Term,
        second: Term,
        vararg others: Term,
    ): Tuple = tupleOf(listOf(first, second, *others))

    private val defaultIfEmpty: () -> Term = { trueTruth }

    override fun wrapAsTupleIfNeeded(
        vararg terms: Term,
        ifEmpty: () -> Term,
    ): Term = wrapAsTupleIfNeeded(terms.asIterable(), ifEmpty)

    override fun wrapAsTupleIfNeeded(vararg terms: Term): Term = wrapAsTupleIfNeeded(terms.asIterable(), defaultIfEmpty)

    override fun wrapAsTupleIfNeeded(
        terms: Iterable<Term>,
        ifEmpty: () -> Term,
    ): Term {
        val i = terms.iterator()
        if (!i.hasNext()) return ifEmpty()
        val first = i.next()
        if (!i.hasNext()) return first
        val items = mutableListOf(first)
        while (i.hasNext()) {
            items.add(i.next())
        }
        return tupleOf(items)
    }

    override fun wrapAsTupleIfNeeded(terms: Iterable<Term>): Term = wrapAsTupleIfNeeded(terms, defaultIfEmpty)

    override fun wrapAsTupleIfNeeded(
        terms: Sequence<Term>,
        ifEmpty: () -> Term,
    ): Term = wrapAsTupleIfNeeded(terms.asIterable(), ifEmpty)

    override fun wrapAsTupleIfNeeded(terms: Sequence<Term>): Term =
        wrapAsTupleIfNeeded(terms.asIterable(), defaultIfEmpty)

    override fun logicListOf(vararg items: Term): LogicList = logicListFrom(items = items, tail = emptyLogicList)

    override fun logicListOf(items: Sequence<Term>): LogicList = logicListFrom(items, emptyLogicList)

    override fun logicListOf(items: Iterable<Term>): LogicList = logicListFrom(items, emptyLogicList)

    override fun logicListFrom(
        vararg items: Term,
        tail: Term?,
    ): LogicList = logicListFrom(items.asList(), tail)

    override fun logicListFrom(vararg items: Term): LogicList = logicListFrom(items.asList())

    override fun logicListFrom(
        items: Iterable<Term>,
        tail: Term?,
    ): LogicList =
        when (items) {
            is List<Term> -> logicListFrom(items, tail)
            else -> logicListFrom(items.cursor(), tail)
        }

    override fun logicListFrom(items: Iterable<Term>): LogicList = logicListFrom(items, null)

    override fun logicListFrom(
        items: Sequence<Term>,
        tail: Term?,
    ): LogicList = logicListFrom(items.cursor(), tail)

    override fun logicListFrom(items: Sequence<Term>): LogicList = logicListFrom(items, null)

    private fun logicListFrom(
        items: Cursor<out Term>,
        last: Term? = null,
    ): LogicList =
        when {
            items.isOver ->
                (last ?: emptyLogicList).asList()
                    ?: throw IllegalArgumentException(
                        "Cannot create a list out of the provided arguments: $items, $last",
                    )
            last == null -> LazyConsWithImplicitLast(items)
            else -> LazyConsWithExplicitLast(items, last)
        }

    override fun logicListFrom(
        items: List<Term>,
        tail: Term?,
    ): LogicList {
        if (items.isEmpty()) {
            return (tail ?: emptyLogicList).asList()
                ?: throw IllegalArgumentException("Cannot create a list out of the provided arguments: $items, $tail")
        }
        val i = items.asReversed().iterator()
        var right =
            if (tail == null) {
                i.next()
                items.last()
            } else {
                tail
            }
        while (i.hasNext()) {
            right = consOf(i.next(), right)
        }
        return right.castToList()
    }

    override fun logicListFrom(items: List<Term>): LogicList = logicListFrom(items, null)

    override fun blockOf(vararg items: Term): Block = blockOf(items.toList())

    override fun blockOf(items: Iterable<Term>): Block = blockOf(items.toList())

    override fun blockOf(items: List<Term>): Block =
        when {
            items.isEmpty() -> Block.empty()
            items.size == 1 -> BlockImpl(items.single())
            else -> BlockImpl(tupleOf(items))
        }

    override fun blockOf(items: Sequence<Term>): Block = blockOf(items.toList())

    override fun factOf(head: Struct): Fact = FactImpl(head)

    override fun factOf(
        functor: String,
        vararg args: Term,
    ): Fact = factOf(structOf(functor, *args))

    override fun factOf(
        functor: String,
        args: Iterable<Term>,
    ): Fact = factOf(structOf(functor, args))

    override fun factOf(
        functor: String,
        args: Sequence<Term>,
    ): Fact = factOf(structOf(functor, args))

    override fun factTemplateOf(
        functor: String,
        arity: Int,
    ): Fact = factOf(structTemplateOf(functor, arity))

    override fun ruleOf(
        head: Struct,
        body: Sequence<Term>,
    ): Rule = ruleOf(head, body.asIterable())

    override fun ruleOf(
        head: Struct,
        vararg goals: Term,
    ): Rule = ruleOf(head, goals.asIterable())

    override fun ruleOf(
        head: Struct,
        body: Iterable<Term>,
    ): Rule {
        val i = body.iterator()
        if (!i.hasNext()) return factOf(head)
        val first = i.next()
        if (!i.hasNext() && first.isTrue) return factOf(head)
        return RuleImpl(head, wrapAsTupleIfNeeded(body))
    }

    override fun ruleTemplateOf(
        functor: String,
        arity: Int,
    ): Rule = ruleOf(structTemplateOf(functor, arity), anonymousVar())

    override fun directiveOf(
        firstGoal: Term,
        vararg otherGoals: Term,
    ): Directive = directiveOf(listOf(firstGoal, *otherGoals))

    override fun directiveOf(body: Iterable<Term>): Directive {
        require(body.any()) { "Directive requires at least one body element" }
        return DirectiveImpl(wrapAsTupleIfNeeded(body))
    }

    override fun directiveOf(body: Sequence<Term>): Directive = directiveOf(body.asIterable())

    override fun directiveTemplate(length: Int): Directive {
        require(length > 0)
        return directiveOf((0 until length).map { anonymousVar() })
    }

    override fun directiveTemplate(): Directive = directiveTemplate(1)

    override fun clauseOf(
        head: Struct?,
        body: Sequence<Term>,
    ): Clause = clauseOf(head, body.asIterable())

    override fun clauseOf(
        head: Struct?,
        vararg goals: Term,
    ): Clause = clauseOf(head, goals.asIterable())

    override fun clauseOf(
        head: Struct?,
        body: Iterable<Term>,
    ) = when (head) {
        null -> {
            require(body.any()) { "If Clause head is null, at least one body element, is required" }
            directiveOf(body.asIterable())
        }
        else -> ruleOf(head, body)
    }

    override fun consOf(
        head: Term,
        tail: Term,
    ): Cons = ConsImpl(head, tail)

    override fun consOf(head: Term): Cons = consOf(head, emptyLogicList)

    override fun indicatorOf(
        name: Term,
        arity: Term,
    ): Indicator = IndicatorImpl(name, arity)

    override fun indicatorOf(
        name: String,
        arity: Int,
    ): Indicator = indicatorOf(atomOf(name), intOf(arity))

    override fun anonymousVar(): Var = varOf(Terms.ANONYMOUS_VAR_NAME)

    override fun numOf(value: BigDecimal): Real = realOf(value)

    override fun numOf(value: Double): Real = realOf(value)

    override fun numOf(value: Float): Real = realOf(value)

    override fun numOf(value: BigInteger): Integer = intOf(value)

    override fun numOf(value: Int): Integer = intOf(value)

    override fun numOf(value: Long): Integer = intOf(value)

    override fun numOf(value: Short): Integer = intOf(value)

    override fun numOf(value: Byte): Integer = intOf(value)

    override fun numOf(value: String): Numeric =
        try {
            intOf(value)
        } catch (_: NumberFormatException) {
            realOf(value)
        }

    override fun numOf(value: Number): Numeric =
        when (value) {
            // avoiding string format is necessary for "floats", to maintain full precision during conversions
            is Double -> numOf(value)
            is Int -> numOf(value)
            is Float -> numOf(value)
            is Long -> numOf(value)
            is Short -> numOf(value)
            is Byte -> numOf(value)
            else -> numOf(value.toString())
        }

    override fun intOf(value: BigInteger): Integer = IntegerImpl(value)

    override fun intOf(value: Int): Integer = intOf(BigInteger.of(value))

    override fun intOf(value: Long): Integer = intOf(BigInteger.of(value))

    override fun intOf(value: Short): Integer = intOf(BigInteger.of(value.toLong()))

    override fun intOf(value: Byte): Integer = intOf(BigInteger.of(value.toLong()))

    override fun intOf(value: String): Integer = intOf(BigInteger.of(value))

    override fun intOf(
        value: String,
        radix: Int,
    ): Integer = intOf(BigInteger.of(value, radix))

    override fun realOf(value: BigDecimal): Real = RealImpl(value)

    override fun realOf(value: Double): Real = realOf(BigDecimal.of(value))

    override fun realOf(value: Float): Real = realOf(BigDecimal.of(value))

    override fun realOf(value: String): Real = realOf(BigDecimal.of(value))

    override fun truthOf(value: Boolean): Truth = if (value) trueTruth else falseTruth

    override fun truthOf(value: String): Truth =
        when (value) {
            Terms.TRUE_FUNCTOR -> trueTruth
            Terms.FALSE_FUNCTOR -> falseTruth
            Terms.FAIL_FUNCTOR -> failedTruth
            else -> throw IllegalArgumentException("Cannot parse $value as a Truth value")
        }
}
