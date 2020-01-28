const antlr4 = require("antlr4")
const PrologLexer = require("./PrologLexer").PrologLexer
const PrologParser = require("./PrologParser").PrologParser
const BufferedTokenStream = require("antlr4/BufferedTokenStream").BufferedTokenStream
const CommonTokenStream = require("antlr4/CommonTokenStream").CommonTokenStream

let input  = "a + b"
let chars = antlr4.CharStreams.fromString(input)
console.log(chars)
let lexer = new PrologLexer(chars)
console.log(lexer)
console.log(lexer)
let tokens = new BufferedTokenStream(lexer)
console.log(tokens)
tokens = new CommonTokenStream(lexer)
console.log(tokens)
let parser = new PrologParser(tokens)
parser.setTokenStream(tokens)
console.log(parser)
parser.singletonTerm()