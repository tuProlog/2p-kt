@file:JsModule("@tuprolog/parser-utils")
@file:JsNonModule

package it.unibo.tuprolog.parser

external class PrologParser(input: TokenStream) {
    companion object {
        val EOF: Int
        val VARIABLE: Int
        val INTEGER: Int
        val HEX: Int
        val OCT: Int
        val BINARY: Int
        val SIGN: Int
        val FLOAT: Int
        val CHAR: Int
        val BOOL: Int
        val LPAR: Int
        val RPAR: Int
        val LSQUARE: Int
        val RSQUARE: Int
        val EMPTY_LIST: Int
        val LBRACE: Int
        val RBRACE: Int
        val EMPTY_SET: Int
        val SQ_STRING: Int
        val DQ_STRING: Int
        val COMMA: Int
        val PIPE: Int
        val CUT: Int
        val FULL_STOP: Int
        val WHITE_SPACES: Int
        val COMMENT: Int
        val LINE_COMMENT: Int
        val OPERATOR: Int
        val ATOM: Int

        val RULE_singletonTerm: Int
        val RULE_singletonExpression: Int
        val RULE_theory: Int
        val RULE_optClause: Int
        val RULE_clause: Int
        val RULE_expression: Int
        val RULE_outer: Int
        val RULE_op: Int
        val RULE_term: Int
        val RULE_number: Int
        val RULE_integer: Int
        val RULE_real: Int
        val RULE_variable: Int
        val RULE_structure: Int
        val RULE_list: Int
        val RULE_set: Int
    }

    val _interp: dynamic
    var _errHandler: ErrorStrategy

    fun reset()

    fun addParseListener(listener: PrologParserListener)
    fun addErrorListener(listener: dynamic)
    fun removeErrorListeners()

    fun isOperator(operator: String): Boolean
    fun getTokenStream(): CommonTokenStream
    fun addOperator(functor: String, associativity: String, priority: Int)
    fun singletonTerm(): SingletonTermContext
    fun singletonExpression(): SingletonExpressionContext
    fun theory(): TheoryContext
    fun optClause(): OptClauseContext
    fun clause(): ClauseContext
    fun expression(): ExpressionContext
    fun outer(): OuterContext
    fun op(): OpContext
    fun term(): TermContext
    fun number(): NumberContext
    fun integer(): IntegerContext
    fun real(): RealContext
    fun variable(): VariableContext
    fun structure(): StructureContext
    fun list(): ListContext
    fun set(): SetContext
}

external class SingletonTermContext : ParserRuleContext {
    fun term(): TermContext
}

external class SingletonExpressionContext : ParserRuleContext {
    fun expression(): ExpressionContext
}

external class TheoryContext : ParserRuleContext {
    fun clause(): ClauseContext
}

external class OptClauseContext : ParserRuleContext {
    val isOver: Boolean

    fun clause(): ClauseContext
}

external class ClauseContext : ParserRuleContext {
    fun expression(): ExpressionContext
}

external class ExpressionContext : ParserRuleContext {
    val priority: Int
    val disabled: Boolean
    val isTerm: Boolean
    val associativity: String
    val bottom: Int
    val left: TermContext?
    val _op: OpContext?
    val operators: Array<OpContext>
    val _expression: ExpressionContext?
    val right: Array<ExpressionContext>
    val _outer: OuterContext?
    val outers: Array<OuterContext>

    fun term(): TermContext
    fun op(): OpContext
    fun expression(): ExpressionContext
    fun outer(): OuterContext
}

external class OuterContext : ParserRuleContext {
    val top: Int
    val bottom: Int
    val priority: Int
    val isTerm: Boolean
    val associativity: String
    val _op: OpContext
    val operators: Array<OpContext>
    val _expression: ExpressionContext?
    val right: Array<ExpressionContext>
    val _outer: OuterContext?
    val outers: Array<OuterContext>

    fun op(): OpContext
    fun expression(): ExpressionContext
    fun outer(): OuterContext
}

external class OpContext : ParserRuleContext {
    val priority: Int
    val associativity: String
    val symbol: Token
}

external class TermContext : ParserRuleContext {
    val isNum: Boolean
    val isVar: Boolean
    val isList: Boolean
    val isStruct: Boolean
    val isExpr: Boolean
    val isSet: Boolean

    fun variable(): VariableContext
    fun structure(): StructureContext
    fun list(): ListContext
    fun set(): SetContext
    fun number(): NumberContext
    fun expression(): ExpressionContext
}

external class NumberContext : ParserRuleContext {
    val isInt: Boolean
    val isReal: Boolean
    fun integer(): IntegerContext
    fun real(): RealContext
}

external class IntegerContext : ParserRuleContext {
    val isHex: Boolean
    val isOct: Boolean
    val isBin: Boolean
    val isChar: Boolean
    val value: Token
    val sign: Token?
}

external class RealContext : ParserRuleContext {
    val value: Token
    val sign: Token?
}

external class VariableContext : ParserRuleContext {
    val isAnonymous: Boolean
    val value: Token
}

external class StructureContext : ParserRuleContext {
    val arity: Int
    val isTruth: Boolean
    val isList: Boolean
    val isSet: Boolean
    val isString: Boolean
    val isCut: Boolean
    val functor: Token
    val _expression: ExpressionContext?
    val args: Array<ExpressionContext>

    fun expression(): ExpressionContext
}

external class ListContext : ParserRuleContext {
    val length: Int
    val hasTail: Boolean
    val _expression: ExpressionContext
    val items: Array<ExpressionContext>
    val tail: ExpressionContext?

    fun expression(): ExpressionContext
}

external class SetContext : ParserRuleContext {
    val length: Int
    val _expression: ExpressionContext
    val items: Array<ExpressionContext>

    fun expression(): ExpressionContext
}
