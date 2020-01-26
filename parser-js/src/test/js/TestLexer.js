var antlr4 = require("antlr4");
var Lexer = require("./PrologLexer").PrologLexer
var assert = require('assert')


//Operators
var input = "1 + a :- b"
var chars = new antlr4.InputStream(input);
lexer = new Lexer(chars);
//Must be empty
console.log("Operators: " + lexer.getOperators())
//Must by false
console.log("Is red an operator? " + lexer.isOperator("miao"))
lexer.addOperators("+",":-")
//Must be + and :-
console.log("Operators: " + lexer.getOperators())
//Must be true
console.log("Is + an operator? " + lexer.isOperator("+"))
var tokens  = new antlr4.CommonTokenStream(lexer);
//Tokens, for now empty
console.log(tokens)
//Unquote
assert("String",lexer.unquote("'String'"))
assert(!lexer.isOperator("violet"))
assert(lexer.isOperator(":-"))
escaped = "test\\ninvisible\\rvisible"
console.log("escaped: " + escaped + " lexered: " + lexer.escape(escaped))
assert("test\nvisible",lexer.escape(escaped))