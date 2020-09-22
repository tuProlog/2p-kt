@file:JsModule("@tuprolog/parser-utils")
@file:JsNonModule

package it.unibo.tuprolog.parser

open external class PrologParserVisitor<T> {

    open fun visitSingletonTerm(ctx: SingletonTermContext): T
    open fun visitSingletonExpression(ctx: SingletonExpressionContext): T
    open fun visitTheory(ctx: TheoryContext): T
    open fun visitOptClause(ctx: OptClauseContext): T
    open fun visitClause(ctx: ClauseContext): T
    open fun visitExpression(ctx: ExpressionContext): T
    open fun visitOuter(ctx: OuterContext): T
    open fun visitOp(ctx: OpContext): T
    open fun visitTerm(ctx: TermContext): T
    open fun visitNumber(ctx: NumberContext): T
    open fun visitInteger(ctx: IntegerContext): T
    open fun visitReal(ctx: RealContext): T
    open fun visitVariable(ctx: VariableContext): T
    open fun visitStructure(ctx: StructureContext): T
    open fun visitList(ctx: ListContext): T
    open fun visitSet(ctx: SetContext): T
}
