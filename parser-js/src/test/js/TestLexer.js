const antlr4 = require("antlr4");
const Lexer = require("./PrologLexer").PrologLexer;
const assert = require('assert');
const StringType = require("./StringType").STRINGTYPE

//Operators
var input = "1 + a :- b"
var chars = new antlr4.InputStream(input);
lexer = new Lexer(chars);
//Must be empty
console.log("Operators: " + lexer.getOperators())
//Must by false
console.log("Is 'red' an operator? " + lexer.isOperator("red"))
lexer.addOperators("+",":-")
//Must be + and :-
console.log("Operators: " + lexer.getOperators())
//Must be true
console.log("Is '+' an operator? " + lexer.isOperator("+"))
let tokens = new antlr4.CommonTokenStream(lexer);
//Tokens, for now empty
console.log(tokens)
//Unquote
assert("String"===lexer.unquote("'String'"))
assert(!lexer.isOperator("violet"))
assert(lexer.isOperator(":-"))
escaped = "test\\nsec\\rvisible"
assert(JSON.stringify("test\nsec\rvisible") === lexer.escape(JSON.stringify(escaped),StringType.SINGLE_QUOTED))