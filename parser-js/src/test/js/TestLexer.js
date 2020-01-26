const antlr4 = require("antlr4");
const Lexer = require("./PrologLexer").PrologLexer;
const assert = require('assert');
const StringType = require("./StringType").StringType

function testVariables(){
    let input = "_ + A + _B is _1 + _a + _+"
    let chars = new antlr4.InputStream(input);
    let lexer = new Lexer(chars);
    lexer.addOperators("+", "is")
    let tokens = new Array()
    let types = new Array()
    lexer.getAllTokens().forEach(function (it) {
        tokens.push(it.text)
        types.push(it.type)
    });

    assert(tokens[0] === "_")
    assert(tokens[1] === "+")
    assert(tokens[2] === "A")
    assert(tokens[3] === "+")
    assert(tokens[4] === "_B")
    assert(tokens[5] === "is")
    assert(tokens[6] === "_1")
    assert(tokens[7] === "+")
    assert(tokens[8] === "_a")
    assert(tokens[9] === "+")
    assert(tokens[10] === "_")
    assert(tokens[11] === "+")

    assert(types[0] === 1)
    assert(types[1] === 6)
    assert(types[2] === 1)
    assert(types[3] === 6)
    assert(types[4] === 1)
    assert(types[5] === 27)
    assert(types[6] === 1)
    assert(types[7] === 6)
    assert(types[8] === 1)
    assert(types[9] === 6)
    assert(types[10] === 1)
    assert(types[11] === 6)

}

function testOperators(){
    let input = "1 + a :- b + c oo d"
    let chars = new antlr4.InputStream(input);
    let lexer = new Lexer(chars);
    //Must be empty
    console.log("Operators: " + lexer.getOperators())
    lexer.addOperators("+",":-","oo")
    //Must be +, :- and 'oo'
    console.log("Operators: " + lexer.getOperators())
    assert("String"===lexer.unquote("'String'"))
    assert(!lexer.isOperator("violet"))
    assert(lexer.isOperator(":-"))
    assert(!lexer.isOperator("red"))
    assert(lexer.isOperator("+"))
    let escaped = "test\\nsec\\rvisible"
    assert(JSON.stringify("test\nsec\rvisible") === lexer.escape(JSON.stringify(escaped),StringType.SINGLE_QUOTED))
    let tokens = new Array()
    let types = new Array()
    lexer.getAllTokens().forEach(function (it) {
        tokens.push(it.text)
        types.push(it.type)
    });

    assert(tokens[0] === "1")
    assert(tokens[1] === "+")
    assert(tokens[2] === "a")
    assert(tokens[3] === ":-")
    assert(tokens[4] === "b")
    assert(tokens[5] === "+")
    assert(tokens[6] === "c")
    assert(tokens[7] === "oo")
    assert(tokens[8] === "d")

    assert(types[0] === 2)
    assert(types[1] === 6)
    assert(types[2] === 28)
    assert(types[3] === 27)
    assert(types[4] === 28)
    assert(types[5] === 6)
    assert(types[6] === 28)
    assert(types[7] === 27)
    assert(types[8] === 28)
}

function testAtoms(){
    let input = "1 + a + \"b\" + 'c'"
    let chars = new antlr4.InputStream(input);
    let lexer = new Lexer(chars);
    let tokens = new Array()
    let types = new Array()
    lexer.getAllTokens().forEach(function (it) {
        tokens.push(it.text)
        types.push(it.type)
    });

    assert(tokens[0] === "1")
    assert(tokens[1] === "+")
    assert(tokens[2] === "a")
    assert(tokens[3] === "+")
    assert(tokens[4] === "b")
    assert(tokens[5] === "+")
    assert(tokens[6] === "c")

    assert(types[0] === 2)
    assert(types[1] === 6)
    assert(types[2] === 28)
    assert(types[3] === 6)
    assert(types[4] === 19)
    assert(types[5] === 6)
    assert(types[6] === 18)

}


testOperators()
testVariables()
testAtoms()




