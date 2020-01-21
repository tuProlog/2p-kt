package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.unify.prolog

object ParsingExamples {
    val canonicalTerms: Sequence<Pair<String, Term>> = sequenceOf(
        "f(X)" to prolog { "f"("X") },
        "[]" to prolog { List.empty() },
        "[ ]" to prolog { List.empty() },
        "[1]" to prolog { listOf(1) },
        "[a]" to prolog { listOf("a") }
    )

    val expressions: Sequence<Pair<String, Term>> = sequenceOf(
        "1 + 2" to prolog {
            1.toTerm() + 2
        },
        "A; _" to prolog {
            varOf("A") or `_`
        },
        "a; B :- 1" to prolog{
            "a" or varOf("B") impliedBy
                    1
        },
        "a :- 1; '2'" to prolog{
            "a" impliedBy
                    (1 or("2"))
        },
        " a; B :- 1, 3.1; '2'" to prolog {
            "a" or varOf("B") impliedBy
                    ((1 and 3.1) or "2")
        },
        "a, c(D); B :- 1, 3.1; '2'" to prolog {
            ("a" and structOf("c", varOf("D"))) or varOf("B") impliedBy
                    ((1 and 3.1) or "2")
        },
        "a; B, c(D) :- 1, \"4\"; '2', 3.1" to prolog {
            "a" or (varOf("B") and structOf("c",varOf("D"))) impliedBy
                    ( ( 1 and "4") or ("2" and 3.1))
        },
        "a, c(D); B, e(_f, [g]) :- 1; '2', 3.1" to prolog {
            (("a" and structOf("c", varOf("D"))) or ( varOf("B") and structOf("e", "_f", listOf("g")))) impliedBy
                    ( 1 or ( "2" and 3.1 ))
        },
        "a, 3 -> 5; 5.3, 1 -> 6 :- a; b, c" to prolog {
            (("a" and 3) then 5 ) or (( 5.3 and  1) then 6 ) impliedBy
                    ( "a" or ( "b" and "c" ))
        },
        "first_step(X,[X])" to prolog {
            structOf("first_step",varOf("X"),listOf(varOf("X")))
        },
        "sec_step(X,[_|L]) :- first_step(X,L)" to prolog{
            structOf("sec_step", varOf("X"), consOf(varOf("_"),varOf("L"))) impliedBy
                    (structOf("first_step",varOf("X"),varOf("L")))
        },
        "last_but_one(X,[X,_])" to prolog {
            structOf("last_but_one",varOf("X"),listOf(varOf("X"),varOf("_")))
        },
        "last_but_one(X,[_,Y|Ys]) :- last_but_one(X,[Y|Ys])" to prolog {
            structOf("last_but_one",varOf("X"),consOf( varOf("_"), consOf(varOf("Y"),varOf("Ys"))) ) impliedBy
                    structOf("last_but_one",varOf("X"),consOf(varOf("Y"),varOf("Ys")))
        },
        "element_at(X,[_|L],K) :- K > 1, K1 is K - 1, element_at(X,L,K1)" to prolog {
            structOf("element_at",varOf("X"),consOf(varOf("_"),varOf("L")),varOf("K")) impliedBy
                    (  (varOf("K") greaterThan 1) and  ((varOf("K1") `is` (varOf("K").minus(1))) and  (structOf("element_at",varOf("X"),varOf("L"),varOf("K1")))))
        },
        "my_length([],0)" to prolog {
            structOf("my_length",List.empty(),0)
        },
        "my_length([_|L],N) :- my_length(L,N1), N is N1 + 1" to prolog {
            structOf("my_length",consOf(varOf("_"),varOf("L")),varOf("N")) impliedBy
                    ( structOf("my_length", varOf("L"), varOf("N1"))  and (( varOf("N") `is` (varOf("N1").plus(1)))))
        },
        "my_reverse(L1,L2) :- my_rev(L1,L2,[])" to prolog {
            structOf("my_reverse",varOf("L1"),varOf("L2")) impliedBy
                    structOf("my_rev",varOf("L1"), varOf("L2"),List.empty())
        },
        "my_rev([],L2,L2) :- !" to prolog {
            structOf("my_rev",List.empty(),varOf("L2"),varOf("L2")) impliedBy
                    atomOf("!")
        },
        "my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc])" to prolog {
            structOf("my_rev",consOf(varOf("X"),varOf("Xs")), varOf("L2"),varOf("Acc")) impliedBy
                    structOf("my_rev", varOf("Xs"),varOf("L2"),consOf(varOf("X"),varOf("Acc")))
        },
        "is_palindrome(L) :- reverse(L,L)" to prolog {
            structOf("is_palindrome",varOf("L")) impliedBy
                    structOf("reverse",varOf("L"),varOf("L"))
        },
        "my_flatten(X,[X]) :- \\+ is_list(X)" to prolog {
            structOf("my_flatten",varOf("X"),listOf(varOf("X"))) impliedBy
                    structOf("\\+",structOf("is_list",varOf("X")))
        },
        "my_flatten([],[])" to prolog {
            structOf("my_flatten",List.empty(),List.empty())
        },
        "my_flatten([X|Xs],Zs) :- my_flatten(X,Y), my_flatten(Xs,Ys), append(Y,Ys,Zs)" to prolog {
            structOf("my_flatten",consOf(varOf("X"),varOf("Xs")),varOf("Zs")) impliedBy
                    ( structOf("my_flatten",varOf("X"),varOf("Y")) and  ( structOf("my_flatten",varOf("Xs"),varOf("Ys")) and structOf("append",varOf("Y"),varOf("Ys"),varOf("Zs"))))
        },
        "compress([X,Y|Ys],[X|Zs]) :- X \\= Y, compress([Y|Ys],Zs)" to prolog {
            structOf("compress",consOf(varOf("X"),consOf(varOf("Y"),varOf("Ys"))),consOf(varOf("X"),varOf("Zs"))) impliedBy
                    (structOf("\\=",varOf("X"),varOf("Y")) and structOf("compress",consOf(varOf("Y"),varOf("Ys")),varOf("Zs")))
        },
        "count(X,[],[],N,[N,X]) :- N > 1" to prolog {
            structOf("count",varOf("X"),List.empty(),List.empty(),varOf("N"),consOf(varOf("N"),varOf("X"))) impliedBy
                    (varOf("N") greaterThan 1)
        },
        "count(X,[Y|Ys],[Y|Ys],1,X) :- X \\= Y" to prolog {
            structOf("count",varOf("X"),consOf(varOf("Y"),varOf("Ys")),consOf(varOf("Y"),varOf("Ys")),1,varOf("X")) impliedBy
                    ( structOf("\\=",varOf("X"),varOf("Y")))
        },
        "count(X,[Y|Ys],[Y|Ys],N,[N,X]) :- N > 1, X \\= Y" to prolog {
            structOf("count",varOf("X"), consOf(varOf("Y"),varOf("Ys")),consOf(varOf("Y"),varOf("Ys")),varOf("N"),consOf(varOf("N"),varOf("X"))) impliedBy
                    ( (varOf("N") greaterThan 1) and ( structOf("\\=",varOf("X"),varOf("Y")) ))
        },
        "count(X,[X|Xs],Ys,K,T) :- K1 is K + 1, count(X,Xs,Ys,K1,T)" to prolog {
            structOf("count",varOf("X"),consOf(varOf("X"),varOf("Xs")),varOf("Ys"),varOf("K"),varOf("T")) impliedBy
                    ( (varOf("K1") `is` (varOf("K").plus(1))) and ( structOf("count",varOf("X"),varOf("Xs"),varOf("Ys"),varOf("K1"),varOf("T"))))
        },
        "map_upper_bound(XMax, YMax) :- map_size(XSize, YSize), XMax is XSize - 1, YMax is YSize - 1" to prolog {
            structOf("map_upper_bound",varOf("XMax"),varOf("YMax")) impliedBy
                    ( structOf("map_size",varOf("XSize"),varOf("YSize")) and ((varOf("XMax") `is` (varOf("XSize").minus(1))) and (varOf("YMax") `is` (varOf("YSize").minus(1)))))
        },
        "in_map(X, Y) :- X >= 0, Y >= 0, map_size(XSize, YSize), X < XSize, Y < YSize" to prolog {
            structOf("in_map",varOf("X"),varOf("Y")) impliedBy
                    ( (varOf("X") greaterThanOrEqualsTo 0) and ((varOf("Y") greaterThanOrEqualsTo 0) and ( structOf("map_size",varOf("XSize"),varOf("YSize")) and ( (varOf("X") lowerThan (varOf("XSize"))) and (varOf("Y") lowerThan varOf("YSize"))))))
        },
        "tile(wall, X, Y) :- \\+ in_map(X, Y)" to prolog {
            structOf("tile","wall",varOf("X"),varOf("Y")) impliedBy
                    ( structOf("\\+", structOf("in_map",varOf("X"),varOf("Y"))) )
        },
        "draw_char(X, Y) :- tty_size(_, XSize), X >= XSize, NY is Y + 1, draw_char(0, NY)" to prolog {
            structOf("draw_char",varOf("X"),varOf("Y")) impliedBy
                    ( structOf("tty_size",varOf("_"),varOf("XSize")) and ( (varOf("X") greaterThanOrEqualsTo varOf("XSize")) and ( (varOf("NY") `is` ( varOf("Y").plus(1) ))  and (structOf("draw_char",0,varOf("NY")))) ))
        },
        "Y < YMsgs -> write(' ') ; display_offset(XOff, YOff), XMap is X + XOff, YMap is Y + YOff, get_character(XMap, YMap, C), format('~s', [C])" to prolog {
            ((varOf("Y") lowerThan varOf("YMsgs")) then structOf("write",atomOf(" "))) or
                    ( structOf("display_offset",varOf("XOff"),varOf("YOff")) and ( (varOf("XMap") `is` (varOf("X").plus(varOf("XOff")))) and ( (varOf("YMap") `is` (varOf("Y").plus(varOf("YOff")))) and
                    ( structOf("get_character",varOf("XMap"),varOf("YMap"),varOf("C"))  and ( structOf("format",atomOf("~s"),listOf(varOf("C"))) )))))
        },
        "display_offset(X, Y) :- player(XPos, YPos), tty_size(YSize, XSize), message_lines(YMsgs), X is XPos - floor(XSize / 2), Y is YPos - floor((YSize - YMsgs) / 2)" to prolog {
            structOf("display_offset",varOf("X"),varOf("Y")) impliedBy
                    ( structOf("player",varOf("XPos"),varOf("YPos")) and ( structOf("tty_size",varOf("YSize"),varOf("XSize"))and ( structOf("message_lines",varOf("YMsgs"))  and (
                            ( varOf("X") `is` (varOf("XPos").minus(structOf("floor",varOf("XSize").div(2)) )))  and ( (varOf("Y") `is` (varOf("YPos").minus(structOf("floor",(varOf("YSize").minus(varOf("YMsgs"))).div(2)) ))))))))
        }
    )

    val all = canonicalTerms + expressions
}