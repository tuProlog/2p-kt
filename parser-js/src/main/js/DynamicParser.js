const Associativity = require("./Associativity").Associativity;
const antlr4 = require("antlr4");

const OP = 0;
const ASSOCIATIVITY = 1;
const PRIORITY = 1;

function DynamicParser(input, lexer) {
    const _operators = Array(Array());
    let _lexer = lexer === 'undefined' ? input.getTokenSource() : lexer;

    Object.defineProperty(this, "lexer", {
        get: function () {
            return _lexer
        },
        set: function (value) {
            _lexer = value
        }
    });

    this.isAnonymous = function (token) {
        return ((token.length == 1) && (token[0] == '_')) || ((token.text.length == 1) && (token.text[0] == '_'))
    };

    this.getOperatorPriority = function (operator, associativity) {
        for (const op of _operators) {
            if ((op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY]))
                return op[PRIORITY];
        }
    };

    this.isOperator = function (operator) {
        return _lexer.isOperator(operator)
    };

    this.addOperator = function (functor, associativity, priority) {
        lexer.addOperators(functor);
        _operators.push([functor, associativity, priority])
    };

    this.isOperatorAssociativity = function (operator, associativity) {
        for (const op of _operators) {
            if ((op[OP] === operator || op[OP] === operator.text) && (op[ASSOCIATIVITY] === associativity))
                return true;
        }
        return false
    };

    this.lookaheadFunc = function (f, associativity, priority, ...except) {
        const lookahead = this.getTokenStream().LT(1);
        for (const exc in except) {
            if (this.isOperator(exc) && lookahead.text === exc)
                return null;
            if (!this.isOperator(lookahead))
                return null;
            if (!this.isOperatorAssociativity(lookahead, associativity))
                return null;
            return f(this.getOperatorPriority(lookahead, associativity), priority)
        }
    };

    this.lookaheadIs = function (associativities, ...except) {
        const lookahead = this.getTokenStream().LT(1);
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
        const lookahead = this.getTokenStream().LT(1);
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