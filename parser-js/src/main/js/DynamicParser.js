const Associativity = require("./Associativity").Associativity
const antlr4 = require("antlr4");

let OP = 0
let ASSOCIATIVITY = 1
let PRIORITY = 1

function DynamicParser(input, lexer){
    var _operators = Array(Array())
    antlr4.Parser.call(this,input)
    if(lexer === 'undefined')
        var _lexer = input.getTokenSource()
    else
        var _lexer=lexer

    Object.defineProperty(this,"lexer",{
        get: function(){return _lexer},
        set: function(value){_lexer = value}
    })

    this.getOperatorPriority = function(operator,associativity){
        _operators.forEach(
            op => {
                if((op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY]))
                    return op[PRIORITY]
            }
        )
    }

    this.isOperator = function(operator){
        return _lexer.isOperator(operator)
    }

    this.addOperator = function(functor,associativity,priority){
        lexer.addOperators(functor)
        _operators.push([functor,associativity,priority])
    }

    this.isOperatorAssociativity = function(operator,associativity){
        _operators.forEach(
            op => {
                if ( (op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY] === associativity))
                    return true
            }
        )
        return false
    }

    this.lookaheadFunc = function(f,associativity,priority, ...except){
        var lookahead = this.getTokenStream().LT(1)
        for(var exc in except){
            if(this.isOperator(exc) && lookahead.text === exc)
                return null
            if(!this.isOperator(lookahead))
                return null
            if(!this.isOperatorAssociativity(lookahead,associativity))
                return null
            return f(this.getOperatorPriority(lookahead,associativity),priority)
        }
    }

    this.lookaheadIs = function(associativities,...except){
        var lookahead = this.getTokenStream().LT(1)
        var exc = true
        except.forEach(
            e => {
                if(this.isOperator(e) && e === lookahead.text)
                    exc = false
            }
        )
        var ass = false
        associativities.forEach(
            a => {
                if(this.isOperatorAssociativity(lookahead,a))
                    ass = true
            }
        )
        return exc && ass
    }

    var _compare = function(x,y){
        return (x<y) ? -1 : ((x===y) ? 0 : 1)
    }

    this.lookaheadGt = function(associativity,priority,...except){
        var res = this.lookaheadFunc(_compare,associativity,priority,except)
        if(res===null)
            res = -1
        return res>0
    }

    this.lookaheadEq = function(associativity,priority,...except){
        var res = this.lookaheadFunc(_compare,associativity,priority,except)
        if(res===null)
            res = -1
        return res===0
    }

    this.lookaheadNeq = function(associativity,priority,...except){
        var res = this.lookaheadFunc(_compare,associativity,priority,except)
        if(res===null)
            res = 0
        return res!=0
    }

    this.lookaheadGeq = function(associativity,priority,...except){
        var res = this.lookaheadFunc(_compare,associativity,priority,except)
        if(res===null)
            res = -1
        return res>=0
    }

    this.lookaheadLeq = function(associativity,priority,...except){
        var res = this.lookaheadFunc(_compare,associativity,priority,except)
        if(res===null)
            res = 1
        return res<=0
    }

    this.lookaheadLt = function(associativity,priority,...except){
        var res = this.lookaheadFunc(_compare,associativity,priority,except)
        if(res===null)
            res = 1
        return res<0
    }

    this.lookahead = function(associativity,top,bottom,...except){
        var associativities = Array()
        if(associativity.__proto__ != Array.prototype)
            associativities.push(associativity)
        else
            associativities = associativity
        var lookahead = this.getTokenStream().LT(1)
        var exc = true
        except.forEach(
            e => {
                if(this.isOperator(e) && lookahead.text === e)
                    exc = false
            }
        )
        var ass = false
        associativities.forEach(
            a => {
                if(this.isOperatorAssociativity(lookahead,a)){
                    priority = this.getOperatorPriority(lookahead,a)
                    ass = (priority <= top) && (priority >= bottom)
                }
            }
        )
        return exc && ass
    }

}
