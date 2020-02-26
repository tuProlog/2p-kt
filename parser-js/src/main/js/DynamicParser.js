const Associativity = require("./Associativity").Associativity;

const OP = 0;
const ASSOCIATIVITY = 1;
const PRIORITY = 2;


const enableLogging = false;

function log(...args) {
    if (enableLogging) {
        console.log("Parser: ", ...args)
    }
}

function DynamicParser(input) {
    const _operators = {}; // map<string, map<assoc, int>>
    let _input = input;
    let _lexer = input.tokenSource;

    this.isAnonymous = function (token) {
        return ((token.length === 1) && (token[0] === '_')) || ((token.text.length === 1) && (token.text[0] === '_'))
    };

    this.getOperatorPriority = function (operator, associativity) {
        const functor = operator.text || operator;

        if (functor in _operators) {
            const assocPriority = _operators[functor];
            if (associativity in assocPriority) {
                return assocPriority[associativity];
            }
        }
        return 1201;
    };

    this.isOperator = function (operator) {
        return _lexer.isOperator(operator);
    };

    this.addOperator = function (functor, associativity, priority) {
        _lexer.addOperators(functor);
        if (!(functor in _operators)) {
            _operators[functor] = {}
        }
        const assocPriority = _operators[functor];
        assocPriority[associativity] = priority;
    };

    this.isOperatorAssociativity = function (operator, associativity) {
        const functor = operator.text || operator;

        if (functor in _operators) {
            return associativity in _operators[functor];
        }
        return false
    };

    this.mustBeExcluded = function (lookahead, except) {
        for (const e of except) {
            if (this.isOperator(e) && lookahead.text === e) {
                return true;
            }
        }
        return false;
    };

    this.lookaheadFunc = function (f, associativity, priority, except) {
        const lookahead = _input.LT(1);
        log("lookahead: " + lookahead);
        if (!this.isOperator(lookahead.text)) {
            return null;
        }
        if (this.mustBeExcluded(lookahead, except)) {
            return null;
        }
        if (!this.isOperatorAssociativity(lookahead, associativity)) {
            return null;
        }
        return f(this.getOperatorPriority(lookahead, associativity), priority);
    };

    this.lookaheadIs = function (associativity, except) {
        let associativities;
        if (associativity.__proto__ !== Array.prototype) {
            associativities = [associativity];
        } else {
            associativities = associativity;
        }
        const lookahead = _input.LT(1);
        if (this.mustBeExcluded(lookahead, except || [])) {
            return false;
        }
        for (const a of associativities) {
            if (this.isOperatorAssociativity(lookahead, a)) {
                return true;
            }
        }
        return false;
    };

    const _compare = function (x, y) {
        return (x < y) ? -1 : ((x === y) ? 0 : 1);
    };

    this.lookaheadGt = function (associativity, priority, except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = -1;
        return res > 0;
    };

    this.lookaheadEq = function (associativity, priority, except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = -1;
        return res === 0;
    };

    this.lookaheadNeq = function (associativity, priority, except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = 0;
        return res !== 0;
    };

    this.lookaheadGeq = function (associativity, priority, except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = -1;
        return res >= 0;
    };

    this.lookaheadLeq = function (associativity, priority, except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = 1;
        return res <= 0;
    };

    this.lookaheadLt = function (associativity, priority, except) {
        let res = this.lookaheadFunc(_compare, associativity, priority, except);
        if (res === null)
            res = 1;
        return res < 0;
    };

    this.lookahead = function (associativity, top, bottom, except) {
        let associativities;
        if (associativity.__proto__ !== Array.prototype) {
            associativities = [associativity];
        } else {
            associativities = associativity;
        }
        const lookahead = _input.LT(1);
        if (this.mustBeExcluded(lookahead, except)) {
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