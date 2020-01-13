lexer grammar PrologLexer;

@header {
var DynamicLexer = require("./DynamicLexer").DynamicLexer
// TODO import SINGLE_QUOTED & DOUBLE_QUOTED
}

@members {
Object.defineProperty(PrologLexer.prototype, "dynamicLexer", {
        get : function() {
                if (this._dynamicLexer === undefined) {
                    this._dynamicLexer = new DynamicLexer();
                }
                return this._dynamicLexer;
        }
});

PrologLexer.prototype.escape = function() {
	return this.dynamicLexer.escape.apply(this.dynamicLexer, arguments);
};

PrologLexer.prototype.unquote = function() {
	return this.dynamicLexer.unquote.apply(this.dynamicLexer, arguments);
};

PrologLexer.prototype.isOperator = function() {
	return this.dynamicLexer.isOperator.apply(this.dynamicLexer, arguments);
};
}

tokens { VARIABLE }

INTEGER
    : Digit+
    ;

HEX
    : Zero [xX] HexDigit+
    ;

OCT
    : Zero [oO] OctDigit+
    ;

BINARY
    : Zero [bB] BinDigit+
    ;

SIGN
    : '+' | '-'
    ;

FLOAT
    : Digit+ '.' Digit+ ( [eE] SIGN? Digit+ )?
    ;

CHAR
    : Zero '\'' ((~[\n\t\r\f]) | Escapable | DoubleDQ | DoubleSQ) { setText(escape(getText(), SINGLE_QUOTED)); }
    ;

BOOL
    : 'true'
    | 'fail'
    ;

LPAR
    : '('
    ;

RPAR
    : ')'
    ;

//EMPTY_TUPLE
//    : '(' Ws* ')'
//    ;

LSQUARE
    : '['
    ;

RSQUARE
    : ']'
    ;

EMPTY_LIST
    : LSQUARE Ws* RSQUARE
    ;

LBRACE
    : '{'
    ;

RBRACE
    : '}'
    ;

EMPTY_SET
    : LBRACE Ws* RBRACE
    ;

VARIABLE
    : [_A-Z] [_A-Za-z0-9]*
    ;

SQ_STRING
    : '\'' ((~[\\\n']) | Escapable | DoubleSQ)* '\'' { setText(escape(unquote(getText()), SINGLE_QUOTED)); }
    ;

DQ_STRING
    : '"' ((~[\\\n"]) | Escapable | DoubleDQ)* '"' { setText(escape(unquote(getText()), DOUBLE_QUOTED)); }
    ;

COMMA
    : ','
    ;

PIPE
    : '|'
    ;

CUT
    : '!'
    ;

FULL_STOP
    : '.' Ws* (COMMENT? FullStopTerminator)
    ;

fragment FullStopTerminator
    : EOF | LINE_COMMENT | Ws+
    ;

WHITE_SPACES
    : Ws+ -> skip
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '%' (~[\r\n])* -> skip
    ;

OPERATOR
    : (Symbols | Atom) { isOperator(getText()) }?
    ;

ATOM
    : (Symbols | Atom) { !isOperator(getText()) }?
    ;

fragment Symbols
    : OpSymbol+ { !getText().startsWith("/*") }?
    | '!'
    | ';'
    ;

fragment Escapable
    : '\\'
        ( [abfnrtv'`"]
        | '\\'
        | ('\r'? '\n')
        | (OctDigit+ '\\')
        | ([xX] HexDigit+ '\\')
        )
    ;

fragment DoubleSQ
    : '\'\''
    ;

fragment DoubleDQ
    : '""'
    ;

fragment OpSymbol
    : [+*/\\^<>=~:.?@#$&\-]
    ;

fragment Atom
    : [a-z][A-Za-z0-9_]*
    ;

fragment Ws
    : [ \t\r\n]
    ;

fragment OctDigit
    : [0-7]
    ;

fragment BinDigit
    : [0-1]
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment Digit
    : [0-9]
    ;

fragment Zero
    : '0'
    ;