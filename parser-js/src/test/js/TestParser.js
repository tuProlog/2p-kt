const antlr4 = require("antlr4")
const PrologLexer = require("./PrologLexer").PrologLexer
const PrologParser = require("./PrologParser").PrologParser


let input  = "1 + a"
let chars = new antlr4.InputStream(input)
let lexer = new PrologLexer(chars)
let tokens = new antlr4.CommonTokenStream(lexer)
let parser = new PrologParser(tokens)
parser.buildParseTrees = true
let tree = parser.singletonTerm()