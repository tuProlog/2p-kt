@file:JsModule("@tuprolog/parser-utils")
@file:JsNonModule

package it.unibo.tuprolog.parser

@Suppress("TooManyFunctions")
open external class PrologParserListener {
    open fun enterSingletonTerm(ctx: SingletonTermContext)

    open fun exitSingletonTerm(ctx: SingletonTermContext)

    open fun enterSingletonExpression(ctx: SingletonExpressionContext)

    open fun exitSingletonExpression(ctx: SingletonExpressionContext)

    open fun enterTheory(ctx: TheoryContext)

    open fun exitTheory(ctx: TheoryContext)

    open fun enterOptClause(ctx: OptClauseContext)

    open fun exitOptClause(ctx: OptClauseContext)

    open fun enterClause(ctx: ClauseContext)

    open fun exitClause(ctx: ClauseContext)

    open fun enterExpression(ctx: ExpressionContext)

    open fun exitExpression(ctx: ExpressionContext)

    open fun enterOuter(ctx: OuterContext)

    open fun exitOuter(ctx: OuterContext)

    open fun enterOp(ctx: OpContext)

    open fun exitOp(ctx: OpContext)

    open fun enterTerm(ctx: TermContext)

    open fun exitTerm(ctx: TermContext)

    open fun enterNumber(ctx: NumberContext)

    open fun exitNumber(ctx: NumberContext)

    open fun enterInteger(ctx: IntegerContext)

    open fun exitInteger(ctx: IntegerContext)

    open fun enterReal(ctx: RealContext)

    open fun exitReal(ctx: RealContext)

    open fun enterVariable(ctx: VariableContext)

    open fun exitVariable(ctx: VariableContext)

    open fun enterStructure(ctx: StructureContext)

    open fun exitStructure(ctx: StructureContext)

    open fun enterList(ctx: ListContext)

    open fun exitList(ctx: ListContext)

    open fun enterBlock(ctx: BlockContext)

    open fun exitBlock(ctx: BlockContext)
}
