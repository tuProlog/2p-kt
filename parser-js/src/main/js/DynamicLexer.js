const StringType = require("./StringType").StringType;

const enableLogging = false;

function log(...args) {
    if (enableLogging) {
        console.log("Lexer: ", ...args)
    }
}

function DynamicLexer() {

    this.operators = [];

    this.isOperator = function isOperator(string) {
        return this.operators.includes(string);
    };

    this.getOperators = function getOperators() {
        return [...this.operators];
    };

    this.unquote = function (string) {
        return string.substring(1, string.length - 1);
    };

    this.escapeChar = function (repr) {
        switch (repr) {
            case 'a':
                return "\u0007";
            case 'b':
                return "\b";
            case 'f':
                return "\f";
            case 'n':
                return "\n";
            case 'r':
                return "\r";
            case 't':
                return "\t";
            case 'v':
                return "\u000b";
            case '\\':
                return "\\";
            case '"':
                return "\"";
            case '`':
                return "`";
            case '\'':
                return "\'";
            default:
                return "\\" + repr;
        }
    };

    this.escape = function (string, stringType) {
        let res = "";
        const last = string.length - 1;
        for (let i = 0; i <= last; i++) {
            let curChar = string[i];
            let lookahead = i < last ? string[i + 1] : -1;
            if (curChar === '\\') {
                if (i === last) {
                    res += '\\';
                } else if (lookahead === '\n') {
                    i += 1;
                } else if (lookahead === 'x' || lookahead === 'X') {
                    let nextSlashPos = string.indexOf('\\', i + 2);
                    if (nextSlashPos > i && nextSlashPos <= last) {
                        const hexStr = string.substring(i + 2, nextSlashPos);
                        const hex = parseInt(hexStr, 16);
                        if (isNaN(hex)) {
                            res += curChar;
                        } else {
                            res += hex;
                            i += hexStr.length + 2;
                        }
                    } else {
                        res += curChar
                    }
                } else if (!isNaN(parseInt(lookahead)) || !isNaN(parseFloat(lookahead))) {
                    let nextSlashPos = string.indexOf('\\', i + 1);
                    if (nextSlashPos > i && nextSlashPos <= last) {
                        const octStr = string.substring(i + 1, nextSlashPos);
                        const oct = parseInt(octStr, 8);
                        if (isNaN(oct)) {
                            res += curChar;
                        } else {
                            res += oct;
                            i += octStr.length + 1;
                        }
                    } else {
                        res += curChar
                    }
                } else if (i < last - 1 && lookahead === '\r' && string[i + 2] === '\n') {
                    i += 2;
                } else {
                    const escaped = this.escapeChar(lookahead);
                    res += escaped;
                    i += 1;
                }
            } else if ((stringType === StringType.DOUBLE_QUOTED && curChar === '"' && lookahead === '"')
                || (stringType === StringType.SINGLE_QUOTED && curChar === '\'' && lookahead === '\'')) {
                res += curChar;
                i += 1;
            } else {
                res += curChar;
            }
            log("Escape: String=" + string + " StringType=" + stringType + " Iteration: " + i + " curChar=" + curChar + " lookahead: " + lookahead + " actual res=" + res)
        }
        return res
    };

    this.addOperators = function addOperators(...args) {
        for (const op of args) {
            this.operators.push(op)
        }
    }

}

exports.DynamicLexer = DynamicLexer;