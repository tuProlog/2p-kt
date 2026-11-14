package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeSameInstanceAs

private class CastableTermCase(
    val id: String,
    val term: Term,
)

class TermCastSpec :
    FunSpec({
        val terms =
            listOf(
                CastableTermCase("integer", Integer.ONE),
                CastableTermCase("real", Real.ONE_HALF),
                CastableTermCase("atom", Atom.of("a")),
                CastableTermCase("struct", Struct.of("f", Var.of("X"))),
                CastableTermCase("variable", Var.of("X")),
                CastableTermCase("empty-list", EmptyList()),
                CastableTermCase("empty-block", EmptyBlock()),
                CastableTermCase("list", List.of(Integer.ZERO, Real.ZERO)),
                CastableTermCase("block", Block.of(Integer.ZERO, Real.ZERO)),
                CastableTermCase("tuple", Tuple.of(Integer.ZERO, Real.ZERO)),
                CastableTermCase("indicator", Indicator.of("a", 2)),
                CastableTermCase("directive", Goal.of(Atom.of("b"))),
                CastableTermCase("rule", Rule.of(Atom.of("c"), Atom.of("d"))),
                CastableTermCase("fact", Fact.of(Atom.of("e"))),
                CastableTermCase("fail", Truth.FAIL),
            )

        val asTypeTester =
            object : TermVisitor<Unit> {
                override fun defaultValue(term: Term) = Unit

                override fun visitVar(term: Var) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldNotBeNull()
                }

                override fun visitStruct(term: Struct) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitAtom(term: Atom) {
                    term.asAtom().shouldNotBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldNotBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitTruth(term: Truth) {
                    term.asAtom().shouldNotBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldNotBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldNotBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitInteger(term: Integer) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldNotBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldNotBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldNotBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitReal(term: Real) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldNotBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldNotBeNull()
                    term.asReal().shouldNotBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitBlock(term: Block) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldNotBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitEmptyBlock(term: EmptyBlock) {
                    term.asAtom().shouldNotBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldNotBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldNotBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldNotBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitCons(term: Cons) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldNotBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldNotBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitEmptyList(term: EmptyList) {
                    term.asAtom().shouldNotBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldNotBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldNotBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldNotBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitTuple(term: Tuple) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldNotBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitIndicator(term: Indicator) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldNotBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitRule(term: Rule) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldNotBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldNotBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitFact(term: Fact) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldNotBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldNotBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldNotBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }

                override fun visitGoal(term: Goal) {
                    term.asAtom().shouldBeNull()
                    term.asClause().shouldNotBeNull()
                    term.asCons().shouldBeNull()
                    term.asConstant().shouldBeNull()
                    term.asGoal().shouldNotBeNull()
                    term.asEmptyList().shouldBeNull()
                    term.asEmptyBlock().shouldBeNull()
                    term.asFact().shouldBeNull()
                    term.asIndicator().shouldBeNull()
                    term.asInteger().shouldBeNull()
                    term.asList().shouldBeNull()
                    term.asNumeric().shouldBeNull()
                    term.asReal().shouldBeNull()
                    term.asRule().shouldBeNull()
                    term.asBlock().shouldBeNull()
                    term.asStruct().shouldNotBeNull()
                    term.asTerm().shouldNotBeNull()
                    term.asTruth().shouldBeNull()
                    term.asTuple().shouldBeNull()
                    term.asVar().shouldBeNull()
                }
            }

        val castToTypeTester =
            object : TermVisitor<Unit> {
                override fun defaultValue(term: Term) = Unit

                override fun visitVar(term: Var) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    shouldThrow<ClassCastException> { term.castToStruct() }
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    term.castToVar().shouldNotBeNull()
                }

                override fun visitStruct(term: Struct) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitAtom(term: Atom) {
                    term.castToAtom().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    term.castToConstant().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitTruth(term: Truth) {
                    term.castToAtom().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    term.castToConstant().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    term.castToTruth().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitInteger(term: Integer) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    term.castToConstant().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    term.castToInteger().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToList() }
                    term.castToNumeric().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    shouldThrow<ClassCastException> { term.castToStruct() }
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitReal(term: Real) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    term.castToConstant().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    term.castToNumeric().shouldNotBeNull()
                    term.castToReal().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    shouldThrow<ClassCastException> { term.castToStruct() }
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitBlock(term: Block) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    term.castToBlock().shouldNotBeNull()
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitEmptyBlock(term: EmptyBlock) {
                    term.castToAtom().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    term.castToConstant().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    term.castToEmptyBlock().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    term.castToBlock().shouldNotBeNull()
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitCons(term: Cons) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    term.castToCons().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    term.castToList().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitEmptyList(term: EmptyList) {
                    term.castToAtom().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    term.castToConstant().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    term.castToEmptyList().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    term.castToList().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitTuple(term: Tuple) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    term.castToTuple().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitIndicator(term: Indicator) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    shouldThrow<ClassCastException> { term.castToClause() }
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    term.castToIndicator().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitRule(term: Rule) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    term.castToClause().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    term.castToRule().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitFact(term: Fact) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    term.castToClause().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    shouldThrow<ClassCastException> { term.castToGoal() }
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    term.castToFact().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    term.castToRule().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }

                override fun visitGoal(term: Goal) {
                    shouldThrow<ClassCastException> { term.castToAtom() }
                    term.castToClause().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToCons() }
                    shouldThrow<ClassCastException> { term.castToConstant() }
                    term.castToGoal().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToEmptyList() }
                    shouldThrow<ClassCastException> { term.castToEmptyBlock() }
                    shouldThrow<ClassCastException> { term.castToFact() }
                    shouldThrow<ClassCastException> { term.castToIndicator() }
                    shouldThrow<ClassCastException> { term.castToInteger() }
                    shouldThrow<ClassCastException> { term.castToList() }
                    shouldThrow<ClassCastException> { term.castToNumeric() }
                    shouldThrow<ClassCastException> { term.castToReal() }
                    shouldThrow<ClassCastException> { term.castToRule() }
                    shouldThrow<ClassCastException> { term.castToBlock() }
                    term.castToStruct().shouldNotBeNull()
                    term.castToTerm().shouldNotBeNull()
                    shouldThrow<ClassCastException> { term.castToTruth() }
                    shouldThrow<ClassCastException> { term.castToTuple() }
                    shouldThrow<ClassCastException> { term.castToVar() }
                }
            }

        val positiveTypeTester =
            object : TermVisitor<Unit> {
                override fun defaultValue(term: Term) {
                    term.castToTerm() shouldBeSameInstanceAs term
                    term.asTerm() shouldBeSameInstanceAs term
                    (term.castTo<Term>()) shouldBeSameInstanceAs term
                    (term.`as`<Term>()) shouldBeSameInstanceAs term
                }

                override fun visitVar(term: Var) {
                    term.castToVar() shouldBeSameInstanceAs term
                    term.asVar() shouldBeSameInstanceAs term
                    (term.castTo<Var>()) shouldBeSameInstanceAs term
                    (term.`as`<Var>()) shouldBeSameInstanceAs term
                }

                override fun visitStruct(term: Struct) {
                    term.castToStruct() shouldBeSameInstanceAs term
                    term.asStruct() shouldBeSameInstanceAs term
                    (term.castTo<Struct>()) shouldBeSameInstanceAs term
                    (term.`as`<Struct>()) shouldBeSameInstanceAs term
                }

                override fun visitAtom(term: Atom) {
                    term.castToAtom() shouldBeSameInstanceAs term
                    term.asAtom() shouldBeSameInstanceAs term
                    (term.castTo<Atom>()) shouldBeSameInstanceAs term
                    (term.`as`<Atom>()) shouldBeSameInstanceAs term
                }

                override fun visitTruth(term: Truth) {
                    term.castToTruth() shouldBeSameInstanceAs term
                    term.asTruth() shouldBeSameInstanceAs term
                    (term.castTo<Truth>()) shouldBeSameInstanceAs term
                    (term.`as`<Truth>()) shouldBeSameInstanceAs term
                }

                override fun visitInteger(term: Integer) {
                    term.castToInteger() shouldBeSameInstanceAs term
                    term.asInteger() shouldBeSameInstanceAs term
                    (term.castTo<Integer>()) shouldBeSameInstanceAs term
                    (term.`as`<Integer>()) shouldBeSameInstanceAs term
                }

                override fun visitReal(term: Real) {
                    term.castToReal() shouldBeSameInstanceAs term
                    term.asReal() shouldBeSameInstanceAs term
                    (term.castTo<Real>()) shouldBeSameInstanceAs term
                    (term.`as`<Real>()) shouldBeSameInstanceAs term
                }

                override fun visitBlock(term: Block) {
                    term.castToBlock() shouldBeSameInstanceAs term
                    term.asBlock() shouldBeSameInstanceAs term
                    (term.castTo<Block>()) shouldBeSameInstanceAs term
                    (term.`as`<Block>()) shouldBeSameInstanceAs term
                }

                override fun visitEmptyBlock(term: EmptyBlock) {
                    term.castToEmptyBlock() shouldBeSameInstanceAs term
                    term.asEmptyBlock() shouldBeSameInstanceAs term
                    (term.castTo<EmptyBlock>()) shouldBeSameInstanceAs term
                    (term.`as`<EmptyBlock>()) shouldBeSameInstanceAs term
                }

                override fun visitCons(term: Cons) {
                    term.castToCons() shouldBeSameInstanceAs term
                    term.asCons() shouldBeSameInstanceAs term
                    (term.castTo<Cons>()) shouldBeSameInstanceAs term
                    (term.`as`<Cons>()) shouldBeSameInstanceAs term
                }

                override fun visitEmptyList(term: EmptyList) {
                    term.castToEmptyList() shouldBeSameInstanceAs term
                    term.asEmptyList() shouldBeSameInstanceAs term
                    (term.castTo<EmptyList>()) shouldBeSameInstanceAs term
                    (term.`as`<EmptyList>()) shouldBeSameInstanceAs term
                }

                override fun visitTuple(term: Tuple) {
                    term.castToTuple() shouldBeSameInstanceAs term
                    term.asTuple() shouldBeSameInstanceAs term
                    (term.castTo<Tuple>()) shouldBeSameInstanceAs term
                    (term.`as`<Tuple>()) shouldBeSameInstanceAs term
                }

                override fun visitIndicator(term: Indicator) {
                    term.castToIndicator() shouldBeSameInstanceAs term
                    term.asIndicator() shouldBeSameInstanceAs term
                    (term.castTo<Indicator>()) shouldBeSameInstanceAs term
                    (term.`as`<Indicator>()) shouldBeSameInstanceAs term
                }

                override fun visitRule(term: Rule) {
                    term.castToRule() shouldBeSameInstanceAs term
                    term.asRule() shouldBeSameInstanceAs term
                    (term.castTo<Rule>()) shouldBeSameInstanceAs term
                    (term.`as`<Rule>()) shouldBeSameInstanceAs term
                }

                override fun visitFact(term: Fact) {
                    term.castToFact() shouldBeSameInstanceAs term
                    term.asFact() shouldBeSameInstanceAs term
                    (term.castTo<Fact>()) shouldBeSameInstanceAs term
                    (term.`as`<Fact>()) shouldBeSameInstanceAs term
                }

                override fun visitGoal(term: Goal) {
                    term.castToGoal() shouldBeSameInstanceAs term
                    term.asGoal() shouldBeSameInstanceAs term
                    (term.castTo<Goal>()) shouldBeSameInstanceAs term
                    (term.`as`<Goal>()) shouldBeSameInstanceAs term
                }
            }

        withTests(nameFn = { "type-checking-and-casting/${it.id}" }, terms) { case ->
            case.term.accept(positiveTypeTester)
            case.term.accept(asTypeTester)
            case.term.accept(castToTypeTester)
        }
    })
