package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class TermCastTest {
    private val terms =
        listOf(
            Integer.ONE,
            Real.ONE_HALF,
            Atom.of("a"),
            Struct.of("f", Var.of("X")),
            Var.of("X"),
            EmptyList(),
            EmptyBlock(),
            List.of(Integer.ZERO, Real.ZERO),
            Block.of(Integer.ZERO, Real.ZERO),
            Tuple.of(Integer.ZERO, Real.ZERO),
            Indicator.of("a", 2),
            Directive.of(Atom.of("b")),
            Rule.of(Atom.of("c"), Atom.of("d")),
            Fact.of(Atom.of("e")),
            Truth.FAIL,
        )

    @Test
    fun testTypeCheckingAndCasting() {
        for (term in terms) {
            term.accept(positiveTypeTester)
            term.accept(asTypeTester)
            term.accept(castToTypeTester)
        }
    }

    private val asTypeTester =
        object : TermVisitor<Unit> {
            override fun defaultValue(term: Term) = Unit

            override fun visitVar(term: Var) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNotNull(term.asVar())
            }

            override fun visitStruct(term: Struct) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitAtom(term: Atom) {
                assertNotNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNotNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitTruth(term: Truth) {
                assertNotNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNotNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNotNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitInteger(term: Integer) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNotNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNotNull(term.asInteger())
                assertNull(term.asList())
                assertNotNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitReal(term: Real) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNotNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNotNull(term.asNumeric())
                assertNotNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitBlock(term: Block) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNotNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitEmptyBlock(term: EmptyBlock) {
                assertNotNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNotNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNotNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNotNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitCons(term: Cons) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNotNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNotNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitEmptyList(term: EmptyList) {
                assertNotNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNotNull(term.asConstant())
                assertNull(term.asDirective())
                assertNotNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNotNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitTuple(term: Tuple) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNotNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitIndicator(term: Indicator) {
                assertNull(term.asAtom())
                assertNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNotNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitRule(term: Rule) {
                assertNull(term.asAtom())
                assertNotNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNotNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitFact(term: Fact) {
                assertNull(term.asAtom())
                assertNotNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNotNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNotNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }

            override fun visitDirective(term: Directive) {
                assertNull(term.asAtom())
                assertNotNull(term.asClause())
                assertNull(term.asCons())
                assertNull(term.asConstant())
                assertNotNull(term.asDirective())
                assertNull(term.asEmptyList())
                assertNull(term.asEmptyBlock())
                assertNull(term.asFact())
                assertNull(term.asIndicator())
                assertNull(term.asInteger())
                assertNull(term.asList())
                assertNull(term.asNumeric())
                assertNull(term.asReal())
                assertNull(term.asRule())
                assertNull(term.asBlock())
                assertNotNull(term.asStruct())
                assertNotNull(term.asTerm())
                assertNull(term.asTruth())
                assertNull(term.asTuple())
                assertNull(term.asVar())
            }
        }

    private val castToTypeTester =
        object : TermVisitor<Unit> {
            override fun defaultValue(term: Term) = Unit

            override fun visitVar(term: Var) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertFailsWith(ClassCastException::class) { term.castToStruct() }
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertNotNull(term.castToVar())
            }

            override fun visitStruct(term: Struct) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitAtom(term: Atom) {
                assertNotNull(term.castToAtom())
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertNotNull(term.castToConstant())
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitTruth(term: Truth) {
                assertNotNull(term.castToAtom())
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertNotNull(term.castToConstant())
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertNotNull(term.castToTruth())
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitInteger(term: Integer) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertNotNull(term.castToConstant())
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertNotNull(term.castToInteger())
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertNotNull(term.castToNumeric())
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertFailsWith(ClassCastException::class) { term.castToStruct() }
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitReal(term: Real) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertNotNull(term.castToConstant())
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertNotNull(term.castToNumeric())
                assertNotNull(term.castToReal())
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertFailsWith(ClassCastException::class) { term.castToStruct() }
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitBlock(term: Block) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertNotNull(term.castToBlock())
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitEmptyBlock(term: EmptyBlock) {
                assertNotNull(term.castToAtom())
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertNotNull(term.castToConstant())
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertNotNull(term.castToEmptyBlock())
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertNotNull(term.castToBlock())
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitCons(term: Cons) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertNotNull(term.castToCons())
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertNotNull(term.castToList())
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitEmptyList(term: EmptyList) {
                assertNotNull(term.castToAtom())
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertNotNull(term.castToConstant())
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertNotNull(term.castToEmptyList())
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertNotNull(term.castToList())
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitTuple(term: Tuple) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertNotNull(term.castToTuple())
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitIndicator(term: Indicator) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertFailsWith(ClassCastException::class) { term.castToClause() }
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertNotNull(term.castToIndicator())
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitRule(term: Rule) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertNotNull(term.castToClause())
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertNotNull(term.castToRule())
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitFact(term: Fact) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertNotNull(term.castToClause())
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertFailsWith(ClassCastException::class) { term.castToDirective() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertNotNull(term.castToFact())
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertNotNull(term.castToRule())
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }

            override fun visitDirective(term: Directive) {
                assertFailsWith(ClassCastException::class) { term.castToAtom() }
                assertNotNull(term.castToClause())
                assertFailsWith(ClassCastException::class) { term.castToCons() }
                assertFailsWith(ClassCastException::class) { term.castToConstant() }
                assertNotNull(term.castToDirective())
                assertFailsWith(ClassCastException::class) { term.castToEmptyList() }
                assertFailsWith(ClassCastException::class) { term.castToEmptyBlock() }
                assertFailsWith(ClassCastException::class) { term.castToFact() }
                assertFailsWith(ClassCastException::class) { term.castToIndicator() }
                assertFailsWith(ClassCastException::class) { term.castToInteger() }
                assertFailsWith(ClassCastException::class) { term.castToList() }
                assertFailsWith(ClassCastException::class) { term.castToNumeric() }
                assertFailsWith(ClassCastException::class) { term.castToReal() }
                assertFailsWith(ClassCastException::class) { term.castToRule() }
                assertFailsWith(ClassCastException::class) { term.castToBlock() }
                assertNotNull(term.castToStruct())
                assertNotNull(term.castToTerm())
                assertFailsWith(ClassCastException::class) { term.castToTruth() }
                assertFailsWith(ClassCastException::class) { term.castToTuple() }
                assertFailsWith(ClassCastException::class) { term.castToVar() }
            }
        }

    private val positiveTypeTester =
        object : TermVisitor<Unit> {
            override fun defaultValue(term: Term) {
                assertSame(term, term.castToTerm())
                assertSame(term, term.asTerm())
                assertSame(term, term.castTo<Term>())
                assertSame(term, term.`as`<Term>())
            }

            override fun visitVar(term: Var) {
                assertSame(term, term.castToVar())
                assertSame(term, term.asVar())
                assertSame(term, term.castTo<Var>())
                assertSame(term, term.`as`<Var>())
            }

            override fun visitStruct(term: Struct) {
                assertSame(term, term.castToStruct())
                assertSame(term, term.asStruct())
                assertSame(term, term.castTo<Struct>())
                assertSame(term, term.`as`<Struct>())
            }

            override fun visitAtom(term: Atom) {
                assertSame(term, term.castToAtom())
                assertSame(term, term.asAtom())
                assertSame(term, term.castTo<Atom>())
                assertSame(term, term.`as`<Atom>())
            }

            override fun visitTruth(term: Truth) {
                assertSame(term, term.castToTruth())
                assertSame(term, term.asTruth())
                assertSame(term, term.castTo<Truth>())
                assertSame(term, term.`as`<Truth>())
            }

            override fun visitInteger(term: Integer) {
                assertSame(term, term.castToInteger())
                assertSame(term, term.asInteger())
                assertSame(term, term.castTo<Integer>())
                assertSame(term, term.`as`<Integer>())
            }

            override fun visitReal(term: Real) {
                assertSame(term, term.castToReal())
                assertSame(term, term.asReal())
                assertSame(term, term.castTo<Real>())
                assertSame(term, term.`as`<Real>())
            }

            override fun visitBlock(term: Block) {
                assertSame(term, term.castToBlock())
                assertSame(term, term.asBlock())
                assertSame(term, term.castTo<Block>())
                assertSame(term, term.`as`<Block>())
            }

            override fun visitEmptyBlock(term: EmptyBlock) {
                assertSame(term, term.castToEmptyBlock())
                assertSame(term, term.asEmptyBlock())
                assertSame(term, term.castTo<EmptyBlock>())
                assertSame(term, term.`as`<EmptyBlock>())
            }

            override fun visitCons(term: Cons) {
                assertSame(term, term.castToCons())
                assertSame(term, term.asCons())
                assertSame(term, term.castTo<Cons>())
                assertSame(term, term.`as`<Cons>())
            }

            override fun visitEmptyList(term: EmptyList) {
                assertSame(term, term.castToEmptyList())
                assertSame(term, term.asEmptyList())
                assertSame(term, term.castTo<EmptyList>())
                assertSame(term, term.`as`<EmptyList>())
            }

            override fun visitTuple(term: Tuple) {
                assertSame(term, term.castToTuple())
                assertSame(term, term.asTuple())
                assertSame(term, term.castTo<Tuple>())
                assertSame(term, term.`as`<Tuple>())
            }

            override fun visitIndicator(term: Indicator) {
                assertSame(term, term.castToIndicator())
                assertSame(term, term.asIndicator())
                assertSame(term, term.castTo<Indicator>())
                assertSame(term, term.`as`<Indicator>())
            }

            override fun visitRule(term: Rule) {
                assertSame(term, term.castToRule())
                assertSame(term, term.asRule())
                assertSame(term, term.castTo<Rule>())
                assertSame(term, term.`as`<Rule>())
            }

            override fun visitFact(term: Fact) {
                assertSame(term, term.castToFact())
                assertSame(term, term.asFact())
                assertSame(term, term.castTo<Fact>())
                assertSame(term, term.`as`<Fact>())
            }

            override fun visitDirective(term: Directive) {
                assertSame(term, term.castToDirective())
                assertSame(term, term.asDirective())
                assertSame(term, term.castTo<Directive>())
                assertSame(term, term.`as`<Directive>())
            }
        }
}
