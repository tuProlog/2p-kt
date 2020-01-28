@file:JsModule("./PrologParserVisitor")
@file:JsNonModule

package it.unibo.tuprolog.parser

open external class PrologParserVisitor {

    open fun visitSingletonTerm(ctx: SingletonTermContext): dynamic
    open fun visitSingletonExpression(ctx: SingletonExpressionContext): dynamic
    open fun visitTheory(ctx: TheoryContext): dynamic
    open fun visitOptClause(ctx: OptClauseContext): dynamic
    open fun visitClause(ctx: ClauseContext): dynamic
    open fun visitExpression(ctx: ExpressionContext): dynamic
    open fun visitOuter(ctx: OuterContext): dynamic
    open fun visitOp(ctx: OpContext): dynamic
    open fun visitTerm(ctx: TermContext): dynamic
    open fun visitNumber(ctx: NumberContext): dynamic
    open fun visitInteger(ctx: IntegerContext): dynamic
    open fun visitReal(ctx: RealContext): dynamic
    open fun visitVariable(ctx: VariableContext): dynamic
    open fun visitStructure(ctx: StructureContext): dynamic
    open fun visitList(ctx: ListContext): dynamic
    open fun visitSet(ctx: SetContext): dynamic
}