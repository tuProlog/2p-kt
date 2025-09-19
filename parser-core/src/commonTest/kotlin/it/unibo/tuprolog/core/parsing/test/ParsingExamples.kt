package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.unify.logicProgramming
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

object ParsingExamples {
    @Suppress("ktlint:standard:multiline-expression-wrapping")
    val canonicalTerms: Sequence<Pair<String, Term>> =
        sequenceOf(
            "f(X)" to logicProgramming { "f"("X") },
            "f(X, y)" to logicProgramming { "f"("X", "y") },
            "g(X, y, 3)" to logicProgramming { "g"("X", "y", 3) },
            "[]" to logicProgramming { emptyLogicList },
            "[ ]" to logicProgramming { emptyLogicList },
            "[1]" to logicProgramming { logicListOf(1) },
            "[1 | X]" to logicProgramming { consOf(1, "X") },
            "[1, a | X]" to logicProgramming { consOf(1, consOf("a", "X")) },
            "[a, 2, X]" to logicProgramming { logicListOf("a", 2, "X") },
            "{}" to logicProgramming { emptyBlock },
            "{ }" to logicProgramming { emptyBlock },
            "{ 1 }" to logicProgramming { blockOf(1) },
            "{ 1, a }" to logicProgramming { blockOf(1, "a") },
            "{ 1, a, X }" to logicProgramming { blockOf(1, "a", "X") },
            "abc" to logicProgramming { atomOf("abc") },
            "Abc" to logicProgramming { varOf("Abc") },
            "'abc'" to logicProgramming { atomOf("abc") },
            "'Abc'" to logicProgramming { atomOf("Abc") },
            "'Abc d'" to logicProgramming { atomOf("Abc d") },
            "'1'" to logicProgramming { atomOf("1") },
            "'1.2'" to logicProgramming { atomOf("1.2") },
            "\"abc\"" to logicProgramming { atomOf("abc") },
            "\"Abc\"" to logicProgramming { atomOf("Abc") },
            "\"Abc d\"" to logicProgramming { atomOf("Abc d") },
            "\"1\"" to logicProgramming { atomOf("1") },
            "\"1.2\"" to logicProgramming { atomOf("1.2") },
            "1" to logicProgramming { numOf(1) },
            "1.2" to logicProgramming { numOf("1.2") },
            "-1" to logicProgramming { numOf(-1) },
            "-1.2" to logicProgramming { numOf("-1.2") },
            "${BigInteger.of("".padEnd(17, 'F'), 16)}" to logicProgramming {
                numOf(BigInteger.of("295147905179352825855"))
            },
            "0xFF" to logicProgramming { numOf(255) },
            "0XFF" to logicProgramming { numOf(255) },
            "-0xFF" to logicProgramming { numOf(-255) },
            "-0XFF" to logicProgramming { numOf(-255) },
            "0b111" to logicProgramming { numOf(7) },
            "0B111" to logicProgramming { numOf(7) },
            "-0b111" to logicProgramming { numOf(-7) },
            "-0B111" to logicProgramming { numOf(-7) },
            "0o11" to logicProgramming { numOf(9) },
            "0O11" to logicProgramming { numOf(9) },
            "-0o11" to logicProgramming { numOf(-9) },
            "-0O11" to logicProgramming { numOf(-9) },
            "0O11" to logicProgramming { numOf(9) },
            "0'a" to logicProgramming { numOf(97) },
            "-0'a" to logicProgramming { numOf(-97) },
            "${BigDecimal.PI}" to logicProgramming { numOf(BigDecimal.PI) },
            "(+)" to logicProgramming { atomOf("+") },
            "(-)" to logicProgramming { atomOf("-") },
            "$" to logicProgramming { atomOf("$") },
            "+(1)" to logicProgramming { structOf("+", 1) },
            "-(1)" to logicProgramming { structOf("-", 1) },
            "$(1)" to logicProgramming { structOf("$", 1) },
            "+(1, 2)" to logicProgramming { structOf("+", 1, 2) },
            "-(1, 2)" to logicProgramming { structOf("-", 1, 2) },
            "$(1, 2)" to logicProgramming { structOf("$", 1, 2) },
            "'+'(1)" to logicProgramming { structOf("+", 1) },
            "'-'(1)" to logicProgramming { structOf("-", 1) },
            "'$'(1)" to logicProgramming { structOf("$", 1) },
            "'+'(1, 2)" to logicProgramming { structOf("+", 1, 2) },
            "'-'(1, 2)" to logicProgramming { structOf("-", 1, 2) },
            "'$'(1, 2)" to logicProgramming { structOf("$", 1, 2) },
            "'F'(1, 2)" to logicProgramming { structOf("F", 1, 2) },
            "'true'(3)" to logicProgramming { structOf("true", 3) },
            "'false'(4)" to logicProgramming { structOf("false", 4) },
            "'fail'(5)" to logicProgramming { structOf("fail", 5) },
            "true(0)" to logicProgramming { structOf("true", 0) },
            "false(1)" to logicProgramming { structOf("false", 1) },
            "fail(2)" to logicProgramming { structOf("fail", 2) },
        )

    @Suppress("ktlint:standard:max-line-length")
    val expressions: Sequence<Pair<String, Term>> =
        sequenceOf(
            "g(X, y, f(3, a))" to logicProgramming { "g"("X", "y", "f"(3, "a")) },
            "g(X, y, (3, a))" to logicProgramming { "g"("X", "y", 3 and "a") },
            "1 + 2" to
                logicProgramming {
                    1.toTerm() + 2
                },
            "3 + 4 * 5 - 1" to
                logicProgramming {
                    (3.toTerm() + (4.toTerm() * 5)).toTerm() - 1
                },
            "A; _" to
                logicProgramming {
                    "A" or `_`
                },
            "a; B :- 1" to
                logicProgramming {
                    "a" or "B" impliedBy 1
                },
            "a :- 1; '2'" to
                logicProgramming {
                    "a" impliedBy (1 or "2")
                },
            "a; B :- 1, 3.1; '2'" to
                logicProgramming {
                    "a" or "B" impliedBy
                        (1 and 3.1 or "2")
                },
            "a, c(D); B :- 1, 3.1; '2'" to
                logicProgramming {
                    "a" and "c"("D") or "B" impliedBy
                        (1 and 3.1 or "2")
                },
            "a; B, c(D) :- 1, \"4\"; '2', 3.1" to
                logicProgramming {
                    "a" or ("B" and "c"("D")) impliedBy
                        (1 and "4" or ("2" and 3.1))
                },
            "a, c(D); B, e(_f, [g]) :- 1; '2', 3.1" to
                logicProgramming {
                    ("a" and "c"("D") or ("B" and "e"("_f", logicListOf("g")))) impliedBy
                        (1 or ("2" and 3.1))
                },
            "a, 3 -> 5; 5.3, 1 -> 6 :- a; b, c" to
                logicProgramming {
                    (("a" and 3) then 5) or ((5.3 and 1) then 6) impliedBy
                        ("a" or ("b" and "c"))
                },
            "first_step(X, [X])" to
                logicProgramming {
                    "first_step"("X", logicListOf("X"))
                },
            "sec_step(X,[_|L]) :- first_step(X,L)" to
                logicProgramming {
                    "sec_step"("X", consOf(`_`, "L")) impliedBy
                        ("first_step"("X", "L"))
                },
            "last_but_one(X,[X,_])" to
                logicProgramming {
                    "last_but_one"("X", logicListOf("X", `_`))
                },
            "last_but_one(X,[_,Y|Ys]) :- last_but_one(X,[Y|Ys])" to
                logicProgramming {
                    "last_but_one"("X", consOf(`_`, consOf("Y", "Ys"))) impliedBy
                        "last_but_one"("X", consOf("Y", "Ys"))
                },
            "element_at(X,[_|L],K) :- K > 1, K1 is K - 1, element_at(X,L,K1)" to
                logicProgramming {
                    "element_at"("X", consOf(`_`, "L"), "K") impliedBy
                        (("K" greaterThan 1) and (("K1" `is` ("K" - 1)) and ("element_at"("X", "L", "K1"))))
                },
            "my_length([],0)" to
                logicProgramming {
                    "my_length"(emptyLogicList, 0)
                },
            "my_length([_|L],N) :- my_length(L,N1), N is N1 + 1" to
                logicProgramming {
                    "my_length"(consOf(`_`, "L"), "N") impliedBy
                        ("my_length"("L", "N1") and (("N" `is` ("N1".toTerm() + 1))))
                },
            "my_reverse(L1,L2) :- my_rev(L1,L2,[])" to
                logicProgramming {
                    "my_reverse"("L1", "L2") impliedBy
                        "my_rev"("L1", "L2", emptyLogicList)
                },
            "my_rev([],L2,L2) :- !" to
                logicProgramming {
                    "my_rev"(emptyLogicList, "L2", "L2") impliedBy "!"
                },
            "my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc])" to
                logicProgramming {
                    "my_rev"(consOf("X", "Xs"), "L2", "Acc") impliedBy
                        "my_rev"("Xs", "L2", consOf("X", "Acc"))
                },
            "is_palindrome(L) :- reverse(L,L)" to
                logicProgramming {
                    "is_palindrome"("L") impliedBy
                        "reverse"("L", "L")
                },
            "my_flatten(X,[X]) :- \\+ is_list(X)" to
                logicProgramming {
                    "my_flatten"("X", logicListOf("X")) impliedBy
                        "\\+"("is_list"("X"))
                },
            "my_flatten([],[])" to
                logicProgramming {
                    "my_flatten"(emptyLogicList, emptyLogicList)
                },
            "my_flatten([X|Xs],Zs) :- my_flatten(X,Y), my_flatten(Xs,Ys), append(Y,Ys,Zs)" to
                logicProgramming {
                    "my_flatten"(consOf("X", "Xs"), "Zs") impliedBy
                        ("my_flatten"("X", "Y") and ("my_flatten"("Xs", "Ys") and "append"("Y", "Ys", "Zs")))
                },
            "compress([X,Y|Ys],[X|Zs]) :- X \\= Y, compress([Y|Ys],Zs)" to
                logicProgramming {
                    "compress"(consOf("X", consOf("Y", "Ys")), consOf("X", "Zs")) impliedBy
                        ("\\="("X", "Y") and "compress"(consOf("Y", "Ys"), "Zs"))
                },
            "count(X,[],[],N,[N,X]) :- N > 1" to
                logicProgramming {
                    "count"("X", emptyLogicList, emptyLogicList, "N", consOf("N", "X")) impliedBy
                        ("N" greaterThan 1)
                },
            "count(X,[Y|Ys],[Y|Ys],1,X) :- X \\= Y" to
                logicProgramming {
                    "count"("X", consOf("Y", "Ys"), consOf("Y", "Ys"), 1, "X") impliedBy
                        ("\\="("X", "Y"))
                },
            "count(X,[Y|Ys],[Y|Ys],N,[N,X]) :- N > 1, X \\= Y" to
                logicProgramming {
                    "count"("X", consOf("Y", "Ys"), consOf("Y", "Ys"), "N", consOf("N", "X")) impliedBy
                        (("N" greaterThan 1) and ("\\="("X", "Y")))
                },
            "count(X,[X|Xs],Ys,K,T) :- K1 is K + 1, count(X,Xs,Ys,K1,T)" to
                logicProgramming {
                    "count"("X", consOf("X", "Xs"), "Ys", "K", "T") impliedBy
                        (("K1" `is` ("K".toTerm() + 1)) and ("count"("X", "Xs", "Ys", "K1", "T")))
                },
            "map_upper_bound(XMax, YMax) :- map_size(XSize, YSize), XMax is XSize - 1, YMax is YSize - 1" to
                logicProgramming {
                    "map_upper_bound"("XMax", "YMax") impliedBy
                        ("map_size"("XSize", "YSize") and (("XMax" `is` ("XSize" - 1)) and ("YMax" `is` ("YSize" - 1))))
                },
            "in_map(X, Y) :- X >= 0, Y >= 0, map_size(XSize, YSize), X < XSize, Y < YSize" to
                logicProgramming {
                    "in_map"("X", "Y") impliedBy
                        (
                            ("X" greaterThanOrEqualsTo 0) and
                                (
                                    ("Y" greaterThanOrEqualsTo 0) and
                                        (
                                            "map_size"("XSize", "YSize") and
                                                (("X" lowerThan ("XSize")) and ("Y" lowerThan "YSize"))
                                        )
                                )
                        )
                },
            "tile(wall, X, Y) :- \\+ in_map(X, Y)" to
                logicProgramming {
                    "tile"("wall", "X", "Y") impliedBy
                        ("\\+"("in_map"("X", "Y")))
                },
            "draw_char(X, Y) :- tty_size(_, XSize), X >= XSize, NY is Y + 1, draw_char(0, NY)" to
                logicProgramming {
                    "draw_char"("X", "Y") impliedBy
                        (
                            "tty_size"(`_`, "XSize") and
                                (
                                    ("X" greaterThanOrEqualsTo "XSize") and
                                        (("NY" `is` ("Y".toTerm() + 1)) and ("draw_char"(0, "NY")))
                                )
                        )
                },
            "Y < YMsgs -> write(' ') ; display_offset(XOff, YOff), XMap is X + XOff, YMap is Y + YOff, get_character(XMap, YMap, C), format('~s', [C])" to
                logicProgramming {
                    (("Y" lowerThan "YMsgs") then "write"(" ")) or
                        (
                            "display_offset"("XOff", "YOff") and (
                                ("XMap" `is` ("X".toTerm() + "XOff")) and (
                                    ("YMap" `is` ("Y".toTerm() + "YOff")) and
                                        (
                                            "get_character"("XMap", "YMap", "C") and (
                                                "format"(
                                                    "~s",
                                                    logicListOf("C"),
                                                )
                                            )
                                        )
                                )
                            )
                        )
                },
            "display_offset(X, Y) :- player(XPos, YPos), tty_size(YSize, XSize), message_lines(YMsgs), X is XPos - floor(XSize / 2), Y is YPos - floor((YSize - YMsgs) / 2)" to
                logicProgramming {
                    "display_offset"("X", "Y") impliedBy
                        (
                            "player"("XPos", "YPos") and (
                                "tty_size"("YSize", "XSize") and (
                                    "message_lines"("YMsgs") and (
                                        ("X" `is` ("XPos" - "floor"("XSize" / 2))) and
                                            (("Y" `is` ("YPos" - "floor"(("YSize" - "YMsgs") / 2))))
                                    )
                                )
                            )
                        )
                },
        )

    @Suppress("unused")
    val all = canonicalTerms + expressions
}
