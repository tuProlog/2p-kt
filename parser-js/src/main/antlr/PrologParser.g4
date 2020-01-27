parser grammar PrologParser;

options {
  tokenVocab=PrologLexer;
}

@header {
var DynamicParser = require("./DynamicParser").DynamicParser
var Associativity = require("./Associativity").Associativity
}

@members {
   DynamicParser.call(this);
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
            ( { this.lookaheadLeq(YFX, $priority, $disabled) }? operators+=op[Associativity.YFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = Associativity.YFX ;
                $bottom = $op.priority + 1; }
                (
                    { this.lookaheadEq(Associativity.YFX, $op.priority, $disabled) }? operators+=op[YFX]
                        right+=expression[$op.priority - 1, $disabled]
                )*

            | { this.lookaheadLeq(Associativity.XFY, $priority, $disabled) }? operators+=op[Associativity.XFY]
                right+=expression[$op.priority, $disabled]
                { $associativity = Associativity.XFY ;
                $bottom = $op.priority; }
                (
                    { this.lookaheadEq(Associativity.XFY, $op.priority, $disabled) }? operators+=op[Associativity.XFY]
                        right+=expression[$op.priority, $disabled]
                )*

            | { this.lookaheadLeq(Associativity.XFX, $priority, $disabled) }? operators+=op[Associativity.XFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = Associativity.XFX ;
                $bottom = $op.priority + 1; }

            | { this.lookaheadLeq(Associativity.YF, $priority, $disabled) }? operators+=op[Associativity.YF]
                { $associativity = Associativity.YF ;
                $bottom = $op.priority; }
                (
                    { this.lookaheadEq(Associativity.YF, $op.priority, $disabled) }? operators+=op[Associativity.YF]
                )*

            | { this.lookaheadLeq(Associativity.XF, $priority, $disabled) }? operators+=op[Associativity.XF]
                { $associativity = Associativity.XF ;
                $bottom = $op.priority + 1; }


            ) { $isTerm = false; }
        )?

    | { this.lookaheadLeq(Associativity.FX, $priority, $disabled) }? operators+=op[Associativity.FX]
        { $isTerm = false;
        $associativity = Associativity.FX ;
        $bottom = $op.priority + 1; }
        right+=expression[$op.priority - 1, $disabled]

    | { this.lookaheadLeq(Associativity.FY, $priority, $disabled) }? operators+=op[Associativity.FY]
        { $isTerm = false;
        $associativity = Associativity.FY ;
        $bottom = $op.priority; }
        ({ this.lookaheadEq(Associativity.FY, $op.priority, $disabled) }? operators+=op[Associativity.FY])*
        right+=expression[$op.priority, $disabled] { $associativity = Associativity.FY ; }

    ) (
          { this.lookahead(Associativity.NON_PREFIX, $priority, $bottom, $disabled) }?
              outers+=outer[$priority, $bottom, disabled]
              { $bottom = $outer.priority; }
      )*
    ;

outer[top, bottom, disabled = Array()]
returns[priority]
locals[isTerm, associativity, newBottom]
    : (
        { this.lookahead(Associativity.YFX, $top, $bottom, $disabled) }? operators+=op[Associativity.YFX]
            { $associativity = Associativity.YFX ;
            $priority = $op.priority;
            $newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]
            (
                { this.lookaheadEq(Associativity.YFX, $op.priority, $disabled) }? operators+=op[Associativity.YFX]
                    right+=expression[$op.priority - 1, $disabled]
            )*

        | { this.lookahead(Associativity.XFY, $top, $bottom, $disabled) }? operators+=op[Associativity.XFY]
            { $associativity = Associativity.XFY ;
            $priority = $op.priority;
            $newBottom = $op.priority; }
            right+=expression[$op.priority, $disabled]
            (
                { lookaheadEq(Associativity.XFY, $op.priority, $disabled) }? operators+=op[Associativity.XFY]
                    right+=expression[$op.priority, $disabled]
            )*

        | { this.lookahead(Associativity.XFX, $top, $bottom, $disabled) }? operators+=op[Associativity.XFX]
            { $associativity = Associativity.XFX ;
            $priority = $op.priority;
            $newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]

        | { this.lookahead(Associativity.YF, $top, $bottom, $disabled) }? operators+=op[Associativity.YF]
            { $associativity = Associativity.YF ;
            $priority = $op.priority;
            $newBottom = $op.priority; }
            (
                { this.lookaheadEq(Associativity.YF, $op.priority, $disabled) }? operators+=op[Associativity.YF]
            )*

        | { this.lookahead(Associativity.XF, $top, $bottom, $disabled) }? operators+=op[Associativity.XF]
            { $associativity = Associativity.XF ;
            $priority = $op.priority;
            $newBottom = $op.priority + 1; }

    ) (
        { this.lookahead(Associativity.NON_PREFIX, $top, $newBottom, $disabled) }?
            outers+=outer[$top, $newBottom, disabled]
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
    | { !this.lookaheadIs(Associativity.PREFIX) }? functor=(OPERATOR|COMMA|PIPE|SIGN) LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR?
    ;

list
locals[length = 0, hasTail]
    : LSQUARE items+=expression[P0, NO_COMMA_PIPE] { $length++; } (COMMA items+=expression[P0, NO_COMMA_PIPE] { $length++; })* (PIPE { $hasTail = true; } tail=expression[P0, WITH_COMMA])? RSQUARE
    ;

set
locals[length = 0]
    : LBRACE items+=expression[P0, NO_COMMA] { $length++; } (COMMA items+=expression[P0, NO_COMMA] { $length++; })* RBRACE
    ;