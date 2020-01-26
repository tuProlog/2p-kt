const antlr4 = require("antlr4");
const Lexer = require("./PrologLexer").PrologLexer;
const assert = require('assert');
const StringType = require("./StringType").StringType

//Operators
var input = "1 + a :- b";
var chars = new antlr4.InputStream(input);
var lexer = new Lexer(chars);
//Must be empty
console.log("Operators: " + lexer.getOperators())
//Must be false
console.log("Is 'red' an operator? " + lexer.isOperator("red"))
lexer.addOperators("+",":-")
//Must be + and :-
console.log("Operators: " + lexer.getOperators())
//Must be true
console.log("Is '+' an operator? " + lexer.isOperator("+"))
var tokens = antlr4.CommonTokenStream(lexer).tokens
//Tokens, for now empty
console.log(tokens)
//Unquote
assert("String"===lexer.unquote("'String'"))
assert(!lexer.isOperator("violet"))
assert(lexer.isOperator(":-"))
escaped = "test\\nsec\\rvisible"
assert(JSON.stringify("test\nsec\rvisible") === lexer.escape(JSON.stringify(escaped),StringType.SINGLE_QUOTED))
console.log(lexer.getAllTokens())