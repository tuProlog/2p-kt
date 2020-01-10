package it.unibo.tuprolog.parser.dynamic;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DynamicParser extends Parser {

    private static final boolean DEBUG = false;

    private final Map<String, Map<Associativity, Integer>> operators = new HashMap<>();
    private DynamicLexer lexer;

    public DynamicParser(TokenStream input) {
        super(input);
        if (input.getTokenSource() instanceof DynamicLexer) {
            this.lexer = (DynamicLexer) input.getTokenSource();
        } else {
            throw new IllegalStateException("No lexer was provided");
        }
    }

    public DynamicParser(TokenStream input, DynamicLexer lexer) {
        super(input);
        this.lexer = lexer;
    }

    public void setLexer(DynamicLexer lexer) {
        this.lexer = lexer;
    }

    public DynamicLexer getLexer() {
        return lexer;
    }

    public int getOperatorPriority(String operator, Associativity associativity) {
        return Optional.ofNullable(operators.get(operator))
                       .map(m -> m.get(associativity))
                       .orElse(Integer.MAX_VALUE);
    }

    public int getOperatorPriority(Token operator, Associativity associativity) {
        return getOperatorPriority(operator.getText(), associativity);
    }

    public boolean isOperator(Token token) {
        return lexer.isOperator(token);
    }

    public boolean isOperator(String functor) {
        return lexer.isOperator(functor);
    }

    public void addOperator(String functor, Associativity associativity, int priority) {
        lexer.addOperators(functor);
        final Map<Associativity, Integer> item = Collections.singletonMap(associativity, priority);

        operators.merge(functor, item, (m1, m2) -> {
            final Map<Associativity, Integer> m3 = new HashMap<>(m1);
            m3.putAll(m2);
            return m3;
        });
    }

    public void removeOperator(String functor) {
        lexer.removeOperators(functor);
        operators.remove(functor);
    }

    public boolean isOperatorAssociativity(Token token, Associativity associativity) {
        return isOperatorAssociativity(token.getText(), associativity);
    }

    public boolean isOperatorAssociativity(String functor, Associativity a) {
        return operators.containsKey(functor)
               && operators.get(functor).containsKey(a);
    }

    protected static void log(String format, Object... args) {
        if (DEBUG) {
            System.err.printf(format, args);
        }
    }

    protected OptionalInt lookahead(IntBinaryOperator f, Associativity associativity, int priority, String... except) {
        final Token lookahead = getTokenStream().LT(1);

        log("r=%d, c=%d, l='%s' a=%s e=[%s] p(%s)=%d",
            lookahead.getLine(),
            lookahead.getCharPositionInLine(),
            lookahead.getText(),
            associativity,
            except.length > 0 ? "'" + String.join("','", except) + "'" : "",
            lookahead.getText(),
            getOperatorPriority(lookahead, associativity)
        );

        if (Stream.of(except).filter(this::isOperator).anyMatch(o -> lookahead.getText().equals(o))) {
            return OptionalInt.empty();
        }
        if (!isOperator(lookahead)) {
            return OptionalInt.empty();
        }
        if (!isOperatorAssociativity(lookahead, associativity)) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(f.applyAsInt(getOperatorPriority(lookahead, associativity), priority));
    }

    protected boolean lookaheadIs(EnumSet<Associativity> associativities, String... except) {
        final Token lookahead = getTokenStream().LT(1);

        log("r=%d, c=%d, l='%s' e=[%s]",
            lookahead.getLine(),
            lookahead.getCharPositionInLine(),
            lookahead.getText(),
            except.length > 0 ? "'" + String.join("','", except) + "'" : ""
        );

        boolean result = Stream.of(except).filter(this::isOperator).noneMatch(o -> lookahead.getText().equals(o))
                         && associativities.stream().anyMatch(a -> isOperatorAssociativity(lookahead, a));

        log(" %s %s\n", result ? "is" : "is not", associativities.size() == 1
                                                  ? associativities.iterator().next()
                                                  : associativities.stream().map(Objects::toString).collect(Collectors.joining(",", "oneOf(", ")")));

        return result;
    }

    protected boolean lookaheadGt(Associativity associativity, int priority, String... except) {
        boolean res = lookahead(Integer::compare, associativity, priority, except).orElse(-1) > 0;
        log(" > %d? %s\n", priority, res ? "yes" : "no");
        return res;
    }

    protected boolean lookaheadEq(Associativity associativity, int priority, String... except) {
        boolean res = lookahead(Integer::compare, associativity, priority, except).orElse(-1) == 0;
        log(" == %d? %s\n", priority, res ? "yes" : "no");
        return res;
    }

    protected boolean lookaheadNeq(Associativity associativity, int priority, String... except) {
        boolean res = lookahead(Integer::compare, associativity, priority, except).orElse(0) != 0;
        log(" != %d? %s\n", priority, res ? "yes" : "no");
        return res;
    }

    protected boolean lookaheadGeq(Associativity associativity, int priority, String... except) {
        boolean res = lookahead(Integer::compare, associativity, priority, except).orElse(-1) >= 0;
        log(" >= %d? %s\n", priority, res ? "yes" : "no");
        return res;
    }

    protected boolean lookaheadLeq(Associativity associativity, int priority, String... except) {
        boolean res = lookahead(Integer::compare, associativity, priority, except).orElse(1) <= 0;
        log(" =< %d? %s\n", priority, res ? "yes" : "no");
        return res;
    }

    protected boolean lookaheadLt(Associativity associativity, int priority, String... except) {
        boolean res = lookahead(Integer::compare, associativity, priority, except).orElse(1) < 0;
        log(" < %d? %s\n", priority, res ? "yes" : "no");
        return res;
    }

    protected boolean lookahead(Associativity associativity, int top, int bottom, String... except) {
        return lookahead(EnumSet.of(associativity), top, bottom, except);
    }

    protected boolean lookahead(EnumSet<Associativity> associativities, int top, int bottom, String... except) {
        final Token lookahead = getTokenStream().LT(1);

        log("r=%d, c=%d, l='%s' a=%s e=[%s] p(%s)=%s",
            lookahead.getLine(),
            lookahead.getCharPositionInLine(),
            lookahead.getText(),
            associativities.size() == 1
                ? associativities.iterator().next()
                : associativities.stream().map(Objects::toString).collect(Collectors.joining(",", "oneOf(", ")")),
            except.length > 0 ? "'" + String.join("','", except) + "'" : "",
            lookahead.getText(),
            associativities.size() == 1
                ? associativities.stream().map(a -> getOperatorPriority(lookahead,a)).map(String::valueOf).findAny().get()
                : associativities.stream().map(a -> getOperatorPriority(lookahead,a)).map(String::valueOf).collect(Collectors.joining(",", "oneOf(", ")"))
        );


        final boolean result = Stream.of(except).filter(this::isOperator).noneMatch(o -> lookahead.getText().equals(o))
                               && associativities.stream().anyMatch(associativity -> {
                                    if (!isOperatorAssociativity(lookahead, associativity)) {
                                        return false;
                                    }

                                    final int priority = getOperatorPriority(lookahead, associativity);

                                    return priority <= top && priority >= bottom;
                                });

        log(" in [%d, %d]? %s\n", top, bottom, result ? "yes" : "no");

        return result;
    }
}
