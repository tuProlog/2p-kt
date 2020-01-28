@file:JsModule("./PrologParserVisitor")
@file:JsNonModule

package it.unibo.tuprolog.parser

open external class PrologParserVisitor {

    fun <T>visitSingletonTerm(ctx: SingletonTermContext): T
    fun <T>visitSingletonExpression(ctx: SingletonTermContext): T
    fun <T>visitTheory(ctx: TheoryContext): T
    fun <T>visitOptClause(ctx: OptClauseContext): T
    fun <T>visitClause(ctx: ClauseContext): T
    fun <T>visitExpression(ctx: ExpressionContext): T
    fun <T>visitOuter(ctx: OuterContext): T
    fun <T>visitOp(ctx: OpContext): T
    fun <T>visitTerm(ctx: TermContext): T
    fun <T>visitNumber(ctx: NumberContext): T
    fun <T>visitInteger(ctx: IntegerContext): T
    fun <T>visitReal(ctx: RealContext): T
    fun <T>visitVariable(ctx: VariableContext): T
    fun <T>visitStructure(ctx: StructureContext): T
    fun <T>visitList(ctx: ListContext): T
    fun <T>visitSet(ctx: SetContext): T
}