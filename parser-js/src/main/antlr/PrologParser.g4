parser grammar PrologParser;

options {
  tokenVocab=PrologLexer;
}

@header {
var DynamicParserModule = require("./DynamicParser")
var DynamicParser = DynamicParserModule.DynamicParser
var Associativity = require("./Associativity").Associativity

var P0 = 1201;
var TOP = 1200;
var BOTTOM = 0;
var WITH_COMMA = [];
var NO_COMMA = [","];
var NO_COMMA_PIPE = [",", "|"];

var XF = Associativity.XF;
var YF = Associativity.YF;
var XFX = Associativity.XFX;
var XFY = Associativity.XFY;
var YFX = Associativity.YFX;
var FX = Associativity.FX;
var FY = Associativity.FY;
var PREFIX = Associativity.PREFIX;
var NON_PREFIX = Associativity.NON_PREFIX;
var INFIX = Associativity.INFIX;
}

@members {
   DynamicParser.call(this,input);
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
locals[isOver]
    : clause
    | EOF { $isOver = true; }
    ;

clause
    : expression[P0, WITH_COMMA] FULL_STOP
    ;

expression[priority, disabled]
locals[isTerm, associativity, bottom]
    : ( left=term { $isTerm = true; }
        (
            ( { this.lookaheadLeq(YFX, $priority, $disabled) }? operators+=op[YFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = YFX;; $bottom = $op.priority + 1; }
                (
                    { this.lookaheadEq(YFX, $op.priority, $disabled) }? operators+=op[YFX]
                        right+=expression[$op.priority - 1, $disabled]
                )*

            | { this.lookaheadLeq(XFY, $priority, $disabled) }? operators+=op[XFY]
                right+=expression[$op.priority, $disabled]
                { $associativity = XFY ;; $bottom = $op.priority; }
                (
                    { this.lookaheadEq(XFY, $op.priority, $disabled) }? operators+=op[XFY]
                        right+=expression[$op.priority, $disabled]
                )*

            | { this.lookaheadLeq(XFX, $priority, $disabled) }? operators+=op[XFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = XFX ;; $bottom = $op.priority + 1; }

            | { this.lookaheadLeq(YF, $priority, $disabled) }? operators+=op[YF]
                { $associativity = YF ;; $bottom = $op.priority; }
                (
                    { this.lookaheadEq(YF, $op.priority, $disabled) }? operators+=op[YF]
                )*

            | { this.lookaheadLeq(XF, $priority, $disabled) }? operators+=op[XF]
                { $associativity = XF ;; $bottom = $op.priority + 1; }


            ) { $isTerm = false; }
        )?

    | { this.lookaheadLeq(FX, $priority, $disabled) }? operators+=op[FX]
        { $isTerm = false;
        $associativity = FX ;; $bottom = $op.priority + 1;; }
        right+=expression[$op.priority - 1, $disabled]

    | { this.lookaheadLeq(FY, $priority, $disabled) }? operators+=op[FY]
        { $isTerm = false;; $associativity = FY ;; $bottom = $op.priority;; }
        ({ this.lookaheadEq(FY, $op.priority, $disabled) }? operators+=op[FY])*
        right+=expression[$op.priority, $disabled] { $associativity = FY ; }

    ) (
          { this.lookahead(NON_PREFIX, $priority, $bottom, $disabled) }?
              outers+=outer[$priority, $bottom, $disabled]
              { $bottom = $outer.priority; }
      )*
    ;

outer[top, bottom, disabled]
returns[priority]
locals[isTerm, associativity, newBottom]
    : (
        { this.lookahead(YFX, $top, $bottom, $disabled) }? operators+=op[YFX]
            { $associativity = YFX ;;$priority = $op.priority;;$newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]
            (
                { this.lookaheadEq(YFX, $op.priority, $disabled) }? operators+=op[YFX]
                    right+=expression[$op.priority - 1, $disabled]
            )*

        | { this.lookahead(XFY, $top, $bottom, $disabled) }? operators+=op[XFY]
            { $associativity = XFY ;;$priority = $op.priority;;$newBottom = $op.priority; }
            right+=expression[$op.priority, $disabled]
            (
                { this.lookaheadEq(XFY, $op.priority, $disabled) }? operators+=op[XFY]
                    right+=expression[$op.priority, $disabled]
            )*

        | { this.lookahead(XFX, $top, $bottom, $disabled) }? operators+=op[XFX]
            { $associativity = XFX ;; $priority = $op.priority;;$newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]

        | { this.lookahead(YF, $top, $bottom, $disabled) }? operators+=op[YF]
            { $associativity = YF ;; $priority = $op.priority;;$newBottom = $op.priority; }
            (
                { this.lookaheadEq(YF, $op.priority, $disabled) }? operators+=op[YF]
            )*

        | { this.lookahead(XF, $top, $bottom, $disabled) }? operators+=op[XF]
            { $associativity = XF ;;$priority = $op.priority;;$newBottom = $op.priority + 1; }

    ) (
        { this.lookahead(NON_PREFIX, $top, $newBottom, $disabled) }?
            outers+=outer[$top, $newBottom, $disabled]
            { $priority = $outer.priority; }
    )?
    ;

op[associativity]
returns[priority]
    : symbol=(OPERATOR|COMMA|PIPE|SIGN) { $priority = this.getOperatorPriority($symbol, $associativity); }
//    | symbol=COMMA { $priority = getOperatorPriority($symbol, $associativity); }
//    | symbol=PIPE { $priority = getOperatorPriority($symbol, $associativity); }
    ;

term
locals[isNum, isVar, isList, isStruct, isExpr, isSet]
    : LPAR expression[P0, WITH_COMMA] { $isExpr = true; } RPAR
    | number { $isNum = true; }
    | variable { $isVar = true; }
    | structure { $isStruct = true; }
    | list { $isList = true;  }
    | set { $isSet = true;  }
    ;

number
locals[isInt, isReal]
    : integer { $isInt = true; }
    | real { $isReal = true; }
    ;

integer
locals[isHex, isOct, isBin, isChar]
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
locals[isAnonymous]
    : value=VARIABLE { $isAnonymous = this.isAnonymous($value); }
    ;

structure
locals[arity = 0, isTruth, isList, isSet, isString, isCut]
    : functor=BOOL { $isTruth = true; }
    | functor=EMPTY_LIST { $isList = true; }
    | functor=CUT { $isCut = true; }
    | functor=EMPTY_SET { $isSet = true; }
    | functor=DQ_STRING { $isString = true; }
    | LPAR functor=(OPERATOR|COMMA|PIPE|SIGN) RPAR
    | functor=SQ_STRING { $isString = true; } (LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR)?
    | functor=(ATOM|EMPTY_SET) (LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR)?
    | { !this.lookaheadIs(PREFIX) }? functor=(OPERATOR|COMMA|PIPE|SIGN) LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR?
    ;

list
locals[length = 0, hasTail]
    : LSQUARE items+=expression[P0, NO_COMMA_PIPE] { $length++; } (COMMA items+=expression[P0, NO_COMMA_PIPE] { $length++; })* (PIPE { $hasTail = true; } tail=expression[P0, WITH_COMMA])? RSQUARE
    ;

set
locals[length = 0]
    : LBRACE items+=expression[P0, NO_COMMA] { $length++; } (COMMA items+=expression[P0, NO_COMMA] { $length++; })* RBRACE
    ;