parser grammar PrologParser;

options {
  superClass=DynamicParser;
  tokenVocab=PrologLexer;
}

@header {
package it.unibo.tuprolog.parser;
import java.util.*;
import it.unibo.tuprolog.parser.dynamic.*;
import static it.unibo.tuprolog.parser.dynamic.Associativity.*;
import org.antlr.v4.runtime.RuleContext;
}

@members {
    private static boolean isAnonymous(Token token) {
        return isAnonymous(token.getText());
    }

    private static boolean isAnonymous(String name) {
        return name.length() == 1 && name.charAt(0) == '_';
    }

    public static final int P0 = 1201;
    public static final int TOP = 1200;
    public static final int BOTTOM = 0;

    public static final String[] WITH_COMMA = new String[0];
    public static final String[] NO_COMMA = new String[] { "," };
    public static final String[] NO_COMMA_PIPE = new String[] { ",", "|" };

    public static final ExpressionContext NO_ROOT = null;
}

singletonTerm
    : term FULL_STOP? EOF
    ;

singletonExpression
    : expression[P0, WITH_COMMA] FULL_STOP? EOF
    ;

theory
    : (clauses+=clause)* EOF
    ;

optClause
locals[boolean isOver]
    : clause
    | EOF { $isOver = true; }
    ;

clause
    : expression[P0, WITH_COMMA] FULL_STOP
    ;

expression[int priority, String[] disabled]
locals[boolean isTerm, Associativity associativity, int bottom]
    : ( left=term { $isTerm = true; }
        (
            ( { lookaheadLeq(YFX, $priority, $disabled) }? operators+=op[YFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = YFX; $bottom = $op.priority + 1; }
                (
                    { lookaheadEq(YFX, $op.priority, $disabled) }? operators+=op[YFX]
                        right+=expression[$op.priority - 1, $disabled]
                )*

            | { lookaheadLeq(XFY, $priority, $disabled) }? operators+=op[XFY]
                right+=expression[$op.priority, $disabled]
                { $associativity = XFY; $bottom = $op.priority; }
                (
                    { lookaheadEq(XFY, $op.priority, $disabled) }? operators+=op[XFY]
                        right+=expression[$op.priority, $disabled]
                )*

            | { lookaheadLeq(XFX, $priority, $disabled) }? operators+=op[XFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = XFX; $bottom = $op.priority + 1; }

            | { lookaheadLeq(YF, $priority, $disabled) }? operators+=op[YF]
                { $associativity = YF; $bottom = $op.priority; }
                (
                    { lookaheadEq(YF, $op.priority, $disabled) }? operators+=op[YF]
                )*

            | { lookaheadLeq(XF, $priority, $disabled) }? operators+=op[XF]
                { $associativity = XF; $bottom = $op.priority + 1; }


            ) { $isTerm = false; }
        )?

    | { lookaheadLeq(FX, $priority, $disabled) }? operators+=op[FX]
        { $isTerm = false; $associativity = FX; $bottom = $op.priority + 1; }
        right+=expression[$op.priority - 1, $disabled]

    | { lookaheadLeq(FY, $priority, $disabled) }? operators+=op[FY]
        { $isTerm = false; $associativity = FY; $bottom = $op.priority; }
        ({ lookaheadEq(FY, $op.priority, $disabled) }? operators+=op[FY])*
        right+=expression[$op.priority, $disabled] { $associativity = FY; }

    ) (
          { lookahead(NON_PREFIX, $priority, $bottom, $disabled) }?
              outers+=outer[$priority, $bottom, disabled]
              { $bottom = $outer.priority; }
      )*
    ;

outer[int top, int bottom, String[] disabled]
returns[int priority]
locals[boolean isTerm, Associativity associativity, int newBottom]
    : (
        { lookahead(YFX, $top, $bottom, $disabled) }? operators+=op[YFX]
            { $associativity = YFX; $priority = $op.priority; $newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]
            (
                { lookaheadEq(YFX, $op.priority, $disabled) }? operators+=op[YFX]
                    right+=expression[$op.priority - 1, $disabled]
            )*

        | { lookahead(XFY, $top, $bottom, $disabled) }? operators+=op[XFY]
            { $associativity = XFY; $priority = $op.priority; $newBottom = $op.priority; }
            right+=expression[$op.priority, $disabled]
            (
                { lookaheadEq(XFY, $op.priority, $disabled) }? operators+=op[XFY]
                    right+=expression[$op.priority, $disabled]
            )*

        | { lookahead(XFX, $top, $bottom, $disabled) }? operators+=op[XFX]
            { $associativity = XFX; $priority = $op.priority; $newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]

        | { lookahead(YF, $top, $bottom, $disabled) }? operators+=op[YF]
            { $associativity = YF; $priority = $op.priority; $newBottom = $op.priority; }
            (
                { lookaheadEq(YF, $op.priority, $disabled) }? operators+=op[YF]
            )*

        | { lookahead(XF, $top, $bottom, $disabled) }? operators+=op[XF]
            { $associativity = XF; $priority = $op.priority; $newBottom = $op.priority + 1; }

    ) (
        { lookahead(NON_PREFIX, $top, $newBottom, $disabled) }?
            outers+=outer[$top, $newBottom, disabled]
            { $priority = $outer.priority; }
    )?
    ;

op[Associativity associativity]
returns[int priority]
    : symbol=(OPERATOR|COMMA|PIPE|SIGN) { $priority = getOperatorPriority($symbol, $associativity); }
//    | symbol=COMMA { $priority = getOperatorPriority($symbol, $associativity); }
//    | symbol=PIPE { $priority = getOperatorPriority($symbol, $associativity); }
    ;

term
locals[boolean isNum, boolean isVar, boolean isList, boolean isStruct, boolean isExpr, boolean isSet]
    : LPAR expression[P0, WITH_COMMA] { $isExpr = true; } RPAR
    | number { $isNum = true; }
    | variable { $isVar = true; }
    | structure { $isStruct = true; }
    | list { $isList = true;  }
    | set { $isSet = true;  }
    ;

number
locals[boolean isInt, boolean isReal]
    : integer { $isInt = true; }
    | real { $isReal = true; }
    ;

integer
locals[boolean isHex, boolean isOct, boolean isBin, boolean isChar]
    : sign=SIGN?
        ( value=INTEGER
        | value=HEX { $isHex = true; }
        | value=OCT { $isOct = true; }
        | value=BINARY { $isBin = true; }
        | value=CHAR { $isChar = true; }
        )
    ;

real
    : sign=SIGN? value=FLOAT
    ;

variable
locals[boolean isAnonymous]
    : value=VARIABLE { $isAnonymous = isAnonymous($value); }
    ;

structure
locals[int arity = 0, boolean isTruth, boolean isList, boolean isSet, boolean isString, boolean isCut]
    : functor=BOOL { $isTruth = true; }
    | functor=EMPTY_LIST { $isList = true; }
    | functor=CUT { $isCut = true; }
    | functor=EMPTY_SET { $isSet = true; }
    | functor=DQ_STRING { $isString = true; }
    | LPAR functor=(OPERATOR|COMMA|PIPE|SIGN) RPAR
    | functor=SQ_STRING { $isString = true; } (LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR)?
    | functor=(ATOM|EMPTY_SET) (LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR)?
    | { !lookaheadIs(PREFIX) }? functor=(OPERATOR|COMMA|PIPE|SIGN) LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR?
    ;

list
locals[int length = 0, boolean hasTail]
    : LSQUARE items+=expression[P0, NO_COMMA_PIPE] { $length++; } (COMMA items+=expression[P0, NO_COMMA_PIPE] { $length++; })* (PIPE { $hasTail = true; } tail=expression[P0, WITH_COMMA])? RSQUARE
    ;

set
locals[int length = 0]
    : LBRACE items+=expression[P0, NO_COMMA] { $length++; } (COMMA items+=expression[P0, NO_COMMA] { $length++; })* RBRACE
    ;