@file:JsModule("./PrologParserVisitor")
@file:JsNonModule

package it.unibo.tuprolog.parser

external class PrologParserVisitor {

    fun visitSingletonTerm(): SingletonTermContext
    fun visitSingletonExpression(): SingletonExpressionContext
    fun visitTheory(): TheoryContext
    fun visitOptClause(): OptClauseContext
    fun visitClause(): ClauseContext
    fun visitExpression(): ExpressionContext
    fun visitOuter(): OuterContext
    fun visitOp(): OpContext
    fun visitTerm(): TermContext
    fun visitNumber(): NumberContext
    fun visitInteger(): IntegerContext
    fun visitReal(): RealContext
    fun visitVariable(): VariableContext
    fun visitStructure(): StructureContext
    fun visitList(): ListContext
    fun visitSet(): SetContext
}