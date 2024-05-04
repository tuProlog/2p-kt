package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DefaultTermFactory
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.collections.List
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

interface TermFactory {
    @JsName("fail")
    fun fail(): Truth

    @JsName("emptyLogicList")
    fun emptyLogicList(): EmptyList

    @JsName("emptyBlock")
    fun emptyBlock(): EmptyBlock

    @JsName("varOf")
    fun varOf(name: String): Var

    @JsName("varOfChar")
    fun varOf(name: Char): Var

    @JsName("atomOf")
    fun atomOf(value: String): Atom

    @JsName("atomOfChar")
    fun atomOf(value: Char): Atom

    @JsName("structOf")
    fun structOf(
        functor: String,
        vararg args: Term,
    ): Struct

    @JsName("structOfSequence")
    fun structOf(
        functor: String,
        args: Sequence<Term>,
    ): Struct

    @JsName("structOfIterable")
    fun structOf(
        functor: String,
        args: Iterable<Term>,
    ): Struct

    @JsName("structOfList")
    fun structOf(
        functor: String,
        args: List<Term>,
    ): Struct

    @JsName("structTemplate")
    fun structTemplateOf(
        functor: String,
        arity: Int,
    ): Struct

    @JsName("foldedStructOfListNullTerminated")
    fun foldedStructOf(
        operator: String,
        terms: List<Term>,
    ): Struct

    @JsName("foldedStructOfList")
    fun foldedStructOf(
        operator: String,
        terms: List<Term>,
        terminal: Term?,
    ): Struct

    @JsName("foldedStructOfSequence")
    fun foldedStructOf(
        operator: String,
        terms: Sequence<Term>,
        terminal: Term?,
    ): Struct

    @JsName("foldedStructOfSequenceNullTerminated")
    fun foldedStructOf(
        operator: String,
        terms: Sequence<Term>,
    ): Struct

    @JsName("foldedStructOfIterable")
    fun foldedStructOf(
        operator: String,
        terms: Iterable<Term>,
        terminal: Term?,
    ): Struct

    @JsName("foldedStructOfIterableNullTerminated")
    fun foldedStructOf(
        operator: String,
        terms: Iterable<Term>,
    ): Struct

    @JsName("foldedStructOf")
    fun foldedStructOf(
        operator: String,
        vararg terms: Term,
        terminal: Term?,
    ): Struct

    @JsName("foldedStructOfNullTerminated")
    fun foldedStructOf(
        operator: String,
        vararg terms: Term,
    ): Struct

    @JsName("tupleOfMany")
    fun tupleOf(
        first: Term,
        second: Term,
        vararg others: Term,
    ): Tuple

    @JsName("tupleOf")
    fun tupleOf(
        first: Term,
        second: Term,
    ): Tuple

    @JsName("tupleOfIterable")
    fun tupleOf(items: Iterable<Term>): Tuple

    @JsName("tupleOfList")
    fun tupleOf(items: List<Term>): Tuple

    @JsName("tupleOfSequence")
    fun tupleOf(items: Sequence<Term>): Tuple

    @JsName("wrapAsTupleIfNeeded")
    fun wrapAsTupleIfNeeded(
        vararg terms: Term,
        ifEmpty: () -> Term,
    ): Term

    @JsName("wrapAsTupleIfNeededOrTrue")
    fun wrapAsTupleIfNeeded(vararg terms: Term): Term

    @JsName("wrapIterableAsTupleIfNeeded")
    fun wrapAsTupleIfNeeded(
        terms: Iterable<Term>,
        ifEmpty: () -> Term,
    ): Term

    @JsName("wrapIterableAsTupleIfNeededOrTrue")
    fun wrapAsTupleIfNeeded(terms: Iterable<Term>): Term

    @JsName("wrapSequenceAsTupleIfNeeded")
    fun wrapAsTupleIfNeeded(
        terms: Sequence<Term>,
        ifEmpty: () -> Term,
    ): Term

    @JsName("wrapSequenceAsTupleIfNeededOrTrue")
    fun wrapAsTupleIfNeeded(terms: Sequence<Term>): Term

    @JsName("logicListOf")
    fun logicListOf(vararg items: Term): LogicList

    @JsName("logicListOfIterable")
    fun logicListOf(items: Iterable<Term>): LogicList

    @JsName("logicListOfSequence")
    fun logicListOf(items: Sequence<Term>): LogicList

    @JsName("logicListFromWithTail")
    fun logicListFrom(
        vararg items: Term,
        tail: Term?,
    ): LogicList

    @JsName("logicListFrom")
    fun logicListFrom(vararg items: Term): LogicList

    @JsName("logicListFromIterableWithTail")
    fun logicListFrom(
        items: Iterable<Term>,
        tail: Term?,
    ): LogicList

    @JsName("logicListFromIterable")
    fun logicListFrom(items: Iterable<Term>): LogicList

    @JsName("logicListFromSequenceWithTail")
    fun logicListFrom(
        items: Sequence<Term>,
        tail: Term?,
    ): LogicList

    @JsName("logicListFromSequence")
    fun logicListFrom(items: Sequence<Term>): LogicList

    @JsName("logicListFromListWithTail")
    fun logicListFrom(
        items: List<Term>,
        tail: Term?,
    ): LogicList

    @JsName("logicListFromList")
    fun logicListFrom(items: List<Term>): LogicList

    @JsName("blockOf")
    fun blockOf(vararg items: Term): Block

    @JsName("blockOfList")
    fun blockOf(items: List<Term>): Block

    @JsName("blockOfIterable")
    fun blockOf(items: Iterable<Term>): Block

    @JsName("blockOfSequence")
    fun blockOf(items: Sequence<Term>): Block

    @JsName("factOfStruct")
    fun factOf(head: Struct): Fact

    @JsName("factOf")
    fun factOf(
        functor: String,
        vararg args: Term,
    ): Fact

    @JsName("factOfIterable")
    fun factOf(
        functor: String,
        args: Iterable<Term>,
    ): Fact

    @JsName("factOfSequence")
    fun factOf(
        functor: String,
        args: Sequence<Term>,
    ): Fact

    @JsName("factTemplateOf")
    fun factTemplateOf(
        functor: String,
        arity: Int,
    ): Fact

    @JsName("ruleOf")
    fun ruleOf(
        head: Struct,
        vararg goals: Term,
    ): Rule

    @JsName("ruleOfIterable")
    fun ruleOf(
        head: Struct,
        body: Iterable<Term>,
    ): Rule

    @JsName("ruleOfSequence")
    fun ruleOf(
        head: Struct,
        body: Sequence<Term>,
    ): Rule

    @JsName("ruleTemplateOf")
    fun ruleTemplateOf(
        functor: String,
        arity: Int,
    ): Rule

    @JsName("directiveOf")
    fun directiveOf(
        firstGoal: Term,
        vararg otherGoals: Term,
    ): Directive

    @JsName("directiveOfIterable")
    fun directiveOf(body: Iterable<Term>): Directive

    @JsName("directiveOfSequence")
    fun directiveOf(body: Sequence<Term>): Directive

    @JsName("directiveTemplateOfLength")
    fun directiveTemplate(length: Int): Directive

    @JsName("directiveTemplate")
    fun directiveTemplate(): Directive

    @JsName("clauseOf")
    fun clauseOf(
        head: Struct?,
        vararg goals: Term,
    ): Clause

    @JsName("clauseOfIterable")
    fun clauseOf(
        head: Struct?,
        body: Iterable<Term>,
    ): Clause

    @JsName("clauseOfSequence")
    fun clauseOf(
        head: Struct?,
        body: Sequence<Term>,
    ): Clause

    @JsName("consOf")
    fun consOf(
        head: Term,
        tail: Term,
    ): Cons

    @JsName("singletonCons")
    fun consOf(head: Term): Cons

    @JsName("indicatorOf")
    fun indicatorOf(
        name: Term,
        arity: Term,
    ): Indicator

    @JsName("indicatorOfStringInt")
    fun indicatorOf(
        name: String,
        arity: Int,
    ): Indicator

    @JsName("anonymousVar")
    fun anonymousVar(): Var

    @JsName("numOfBigDecimal")
    fun numOf(value: BigDecimal): Real

    @JsName("numOfDouble")
    fun numOf(value: Double): Real

    @JsName("numOfFloat")
    fun numOf(value: Float): Real

    @JsName("numOfBigInteger")
    fun numOf(value: BigInteger): Integer

    @JsName("numOfInt")
    fun numOf(value: Int): Integer

    @JsName("numOfLong")
    fun numOf(value: Long): Integer

    @JsName("numOfShort")
    fun numOf(value: Short): Integer

    @JsName("numOfByte")
    fun numOf(value: Byte): Integer

    @JsName("parseNum")
    fun numOf(value: String): Numeric

    @JsName("numOf")
    fun numOf(value: Number): Numeric

    @JsName("intOfBigInteger")
    fun intOf(value: BigInteger): Integer

    @JsName("intOf")
    fun intOf(value: Int): Integer

    @JsName("intOfLong")
    fun intOf(value: Long): Integer

    @JsName("intOfShort")
    fun intOf(value: Short): Integer

    @JsName("intOfByte")
    fun intOf(value: Byte): Integer

    @JsName("parseInt")
    fun intOf(value: String): Integer

    @JsName("parseIntRadix")
    fun intOf(
        value: String,
        radix: Int,
    ): Integer

    @JsName("realOfBigDecimal")
    fun realOf(value: BigDecimal): Real

    @JsName("realOf")
    fun realOf(value: Double): Real

    @JsName("realOfFloat")
    fun realOf(value: Float): Real

    @JsName("parseReal")
    fun realOf(value: String): Real

    @JsName("truthOf")
    fun truthOf(value: Boolean): Truth

    @JsName("parseTruth")
    fun truthOf(value: String): Truth

    @JsName("objectRef")
    fun objectRef(value: Any?): ObjectRef

    @JsName("nullRef")
    fun nullRef(): NullRef

    companion object {
        @JsName("default")
        @get:JvmName("default")
        @JvmStatic
        val default: TermFactory = DefaultTermFactory
    }
}
