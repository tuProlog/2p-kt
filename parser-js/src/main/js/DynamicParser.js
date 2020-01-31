const Associativity = require("./Associativity").Associativity;
const antlr4 = require("antlr4");

const OP = 0;
const ASSOCIATIVITY = 1;
const PRIORITY = 2;


const enableLogging = true;

function log(...args) {
    if (enableLogging) {
        console.log("Parser: ", ...args)
    }
}

function DynamicParser(input) {
    const _operators = [];
    let _input = input;
    let _lexer = input.tokenSource;

    this.isAnonymous = function (token) {
        return ((token.length === 1) && (token[0] === '_')) || ((token.text.length === 1) && (token.text[0] === '_'))
    };

    this.getOperatorPriority = function (operator, associativity) {
        log("getOperatorPriority with parameters operator=" + operator + " associativity="+ associativity);
        for (const op of _operators) {
            log((op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY] === associativity));
            if ((op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY] === associativity))
                return op[PRIORITY];
        }
    };

    this.isOperator = function (operator) {
        log("check if operator " + operator + " is in " + JSON.stringify(_operators));
        //_operators.forEach(
        //    op => {
        //        log("checking if operator " + operator + " is equal to " + op[OP]);
        //        if(op[OP] === operator || op[OP].toString() === operator || op[OP] == operator)
        //            return true;
        //    }
   // );
        return _lexer.isOperator(operator)
        //log("FALSE, operator " + operator + " is NOT in" + JSON.stringify(_operators));
        //return false;
    };

    this.addOperator = function (functor, associativity, priority) {
        _lexer.addOperators(functor);
        var ops = new Array(3);
        log("add operator: " + functor);
        ops[OP] = functor; ops[ASSOCIATIVITY] = associativity; ops[PRIORITY] = priority;
        log("added operator: " + ops[OP] + " " + ops[ASSOCIATIVITY] + " " + ops[PRIORITY]);
        _operators.push(ops);
        log("new operator set: " + JSON.stringify(_operators))
    };

    this.isOperatorAssociativity = function (operator, associativity) {
        log("check if operator " + operator + " (or operator " + operator.text + ") and associativity: " + associativity + " are in: " + JSON.stringify(_operators));
        for (const op of _operators) {
            log(op[OP]===operator.text);
            if ((op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY] === associativity))
                return true;
        }
        log("check operator associativity failed");
        return false
    };

    this.lookaheadFunc = function (f, associativity, priority, ...except) {
        log("lookaheadFunc with parameters: f=" + f + " ass=" + associativity + " priority="+ priority+ " exc=" + except);
        const lookahead = _input.LT(1);
        log("lookahead: " + lookahead);
        for (const exc in except) {
            if (this.isOperator(exc) && lookahead.text === exc)
                return null;
        }
        if (!this.isOperator(lookahead.text))
            return null;
        if (!this.isOperatorAssociativity(lookahead, associativity))
            return null;
        return f(this.getOperatorPriority(lookahead, associativity), priority)
    };

    this.lookaheadIs = function (associativities, ...except) {
        const lookahead = _input.LT(1);
        for (const e of except) {
            if (this.isOperator(e) && e === lookahead.text)
                return false;
        }
        for (const a of associativities) {
            if (this.isOperatorAssociativity(lookahead, a))
                return true;
        }
        return false;
    };

    const _compare = function (x, y) {
        return (x < y) ? -1 : ((x === y) ? 0 : 1)
    };

    this.lookaheadGt = function (associativity, priority, ...except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = -1;
        return res > 0
    };

    this.lookaheadEq = function (associativity, priority, ...except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = -1;
        return res === 0
    };

    this.lookaheadNeq = function (associativity, priority, ...except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = 0;
        return res !== 0
    };

    this.lookaheadGeq = function (associativity, priority, ...except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = -1;
        return res >= 0
    };

    this.lookaheadLeq = function (associativity, priority, ...except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = 1;
        return res <= 0
    };

    this.lookaheadLt = function (associativity, priority, ...except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = 1;
        return res < 0
    };

    this.lookahead = function (associativity, top, bottom, ...except) {
        let associativities = Array();
        if (associativity.__proto__ != Array.prototype)
            associativities.push(associativity);
        else
            associativities = associativity;
        const lookahead = _input.LT(1);
        for (const e of except) {
            if (this.isOperator(e) && lookahead.text === e)
                return false;
        }
        for (const a of associativities) {
            if (this.isOperatorAssociativity(lookahead, a)) {
                const priority = this.getOperatorPriority(lookahead, a);
                if (priority <= top && priority >= bottom) {
                    return true;
                }
            }
        }
        return false;
    }

}

exports.DynamicParser = DynamicParser;