package it.unibo.tuprolog.parser.dynamic;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class DynamicLexer extends org.antlr.v4.runtime.Lexer {

    private final Set<String> operators = new HashSet<>();

    public DynamicLexer() {
    }

    public DynamicLexer(CharStream input) {
        super(input);
    }

    public boolean isOperator(Token token) {
        return this.isOperator(token.getText());
    }

    public boolean isOperator(String functor) {
        return operators.contains(functor);
    }

    public void addOperators(String operator, String... operators) {
        this.operators.add(operator);
        this.operators.addAll(Arrays.asList(operators));
    }

    public void removeOperators(String operator, String... operators) {
        this.operators.remove(operator);
        this.operators.removeAll(Arrays.asList(operators));
    }

    public void clearOperators() {
        operators.clear();
    }

    public Set<String> getOperators() {
        return new HashSet<>(operators);
    }

    public final String substring(String string, int start, int end) {
        if (end >= 0) {
            return string.substring(start, end);
        } else {
            return string.substring(start, string.length() + end);
        }
    }

    public final String unquote(String string) {
        return string.substring(1, string.length() - 1);
    }

    public String escapeChar(int repr) {
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
                return "\\" + ((char) repr);
        }
    }

    public String escape(String string) {
        return escape(string, StringType.SINGLE_QUOTED);
    }

    public String escape(String string, StringType stringType) {
        Objects.requireNonNull(stringType);

        final StringBuilder sb = new StringBuilder(string.length());
        final int last = string.length() - 1;

        for (int i = 0; i <= last; i++) {
            final int currentChar = string.charAt(i);
            final int lookahead = i < last ? string.charAt(i + 1) : -1;

            if (currentChar == '\\') {
                if (i == last) {
                    sb.append('\\');
                } else if (lookahead == '\n') {
                    i += 1;
                } else if (lookahead == 'x' || lookahead == 'X') {
                    final int nextSlashPos = string.indexOf('\\', i + 2);
                    if (nextSlashPos > i && nextSlashPos <= last) {
                        final String hexStr = string.substring(i + 2, nextSlashPos);
                        try {
                            final int hex = Integer.parseInt(hexStr, 16);
                            sb.append((char) hex);
                            i += hexStr.length() + 2;
                        } catch (NumberFormatException e) {
                            sb.append((char) currentChar);
                        }
                    } else {
                        sb.append((char) currentChar);
                    }
                } else if (Character.isDigit(lookahead)) {
                    final int nextSlashPos = string.indexOf('\\', i + 1);
                    if (nextSlashPos > i && nextSlashPos <= last) {
                        final String octStr = string.substring(i + 1, nextSlashPos);
                        try {
                            final int oct = Integer.parseInt(octStr, 8);
                            sb.append((char) oct);
                            i += octStr.length() + 1;
                        } catch (NumberFormatException e) {
                            sb.append((char) currentChar);
                        }
                    } else {
                        sb.append((char) currentChar);
                    }
                } else if (i < last - 1 && lookahead == '\r' && string.charAt(i + 2) == '\n') {
                    i += 2;
                } else {
                    final String escaped = escapeChar(lookahead);
                    sb.append(escaped);
                    i += 1;
                }
            } else if ((stringType == StringType.DOUBLE_QUOTED && currentChar == '"' && lookahead == '"')
                        || (stringType == StringType.SINGLE_QUOTED && currentChar == '\'' && lookahead == '\'')) {
                sb.append((char) currentChar);
                i += 1;
            } else {
                sb.append((char) currentChar);
            }
        }
        return sb.toString();
    }
}
