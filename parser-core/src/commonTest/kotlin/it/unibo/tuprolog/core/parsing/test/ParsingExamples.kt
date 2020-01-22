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
            "A" or `_`
        },
        "a; B :- 1" to prolog{
            "a" or "B" impliedBy
                    1
        },
        "a :- 1; '2'" to prolog{
            "a" impliedBy
                    (1 or("2"))
        },
        " a; B :- 1, 3.1; '2'" to prolog {
            "a" or "B" impliedBy
                    ((1 and 3.1) or "2")
        },
        "a, c(D); B :- 1, 3.1; '2'" to prolog {
            ("a" and structOf("c", "D")) or "B" impliedBy
                    ((1 and 3.1) or "2")
        },
        "a; B, c(D) :- 1, \"4\"; '2', 3.1" to prolog {
            "a" or ("B" and structOf("c","D")) impliedBy
                    ( ( 1 and "4") or ("2" and 3.1))
        },
        "a, c(D); B, e(_f, [g]) :- 1; '2', 3.1" to prolog {
            (("a" and structOf("c", "D")) or ( "B" and structOf("e", "_f", listOf("g")))) impliedBy
                    ( 1 or ( "2" and 3.1 ))
        },
        "a, 3 -> 5; 5.3, 1 -> 6 :- a; b, c" to prolog {
            (("a" and 3) then 5 ) or (( 5.3 and  1) then 6 ) impliedBy
                    ( "a" or ( "b" and "c" ))
        },
        "first_step(X,[X])" to prolog {
            structOf("first_step","X",listOf("X"))
        },
        "sec_step(X,[_|L]) :- first_step(X,L)" to prolog{
            structOf("sec_step", "X", consOf(`_`,"L")) impliedBy
                    (structOf("first_step","X","L"))
        },
        "last_but_one(X,[X,_])" to prolog {
            structOf("last_but_one","X",listOf("X",`_`))
        },
        "last_but_one(X,[_,Y|Ys]) :- last_but_one(X,[Y|Ys])" to prolog {
            structOf("last_but_one","X",consOf( `_`, consOf("Y","Ys")) ) impliedBy
                    structOf("last_but_one","X",consOf("Y","Ys"))
        },
        "element_at(X,[_|L],K) :- K > 1, K1 is K - 1, element_at(X,L,K1)" to prolog {
            structOf("element_at","X",consOf(`_`,"L"),"K") impliedBy
                    (  ("K" greaterThan 1) and  (("K1" `is` ("K".minus(1))) and  (structOf("element_at","X","L","K1"))))
        },
        "my_length([],0)" to prolog {
            structOf("my_length",List.empty(),0)
        },
        "my_length([_|L],N) :- my_length(L,N1), N is N1 + 1" to prolog {
            structOf("my_length",consOf(`_`,"L"),"N") impliedBy
                    ( structOf("my_length", "L", "N1")  and (( "N" `is` (varOf("N1").plus(1)))))
        },
        "my_reverse(L1,L2) :- my_rev(L1,L2,[])" to prolog {
            structOf("my_reverse","L1","L2") impliedBy
                    structOf("my_rev","L1", "L2",List.empty())
        },
        "my_rev([],L2,L2) :- !" to prolog {
            structOf("my_rev",List.empty(),"L2","L2") impliedBy
                    atomOf("!")
        },
        "my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc])" to prolog {
            structOf("my_rev",consOf("X","Xs"), "L2","Acc") impliedBy
                    structOf("my_rev", "Xs","L2",consOf("X","Acc"))
        },
        "is_palindrome(L) :- reverse(L,L)" to prolog {
            structOf("is_palindrome","L") impliedBy
                    structOf("reverse","L","L")
        },
        "my_flatten(X,[X]) :- \\+ is_list(X)" to prolog {
            structOf("my_flatten","X",listOf("X")) impliedBy
                    structOf("\\+",structOf("is_list","X"))
        },
        "my_flatten([],[])" to prolog {
            structOf("my_flatten",List.empty(),List.empty())
        },
        "my_flatten([X|Xs],Zs) :- my_flatten(X,Y), my_flatten(Xs,Ys), append(Y,Ys,Zs)" to prolog {
            structOf("my_flatten",consOf("X","Xs"),varOf("Zs")) impliedBy
                    ( structOf("my_flatten","X","Y") and  ( structOf("my_flatten","Xs","Ys") and structOf("append","Y","Ys",varOf("Zs"))))
        },
        "compress([X,Y|Ys],[X|Zs]) :- X \\= Y, compress([Y|Ys],Zs)" to prolog {
            structOf("compress",consOf("X",consOf("Y","Ys")),consOf("X",varOf("Zs"))) impliedBy
                    (structOf("\\=","X","Y") and structOf("compress",consOf("Y","Ys"),varOf("Zs")))
        },
        "count(X,[],[],N,[N,X]) :- N > 1" to prolog {
            structOf("count","X",List.empty(),List.empty(),"N",consOf("N","X")) impliedBy
                    ("N" greaterThan 1)
        },
        "count(X,[Y|Ys],[Y|Ys],1,X) :- X \\= Y" to prolog {
            structOf("count","X",consOf("Y","Ys"),consOf("Y","Ys"),1,"X") impliedBy
                    ( structOf("\\=","X","Y"))
        },
        "count(X,[Y|Ys],[Y|Ys],N,[N,X]) :- N > 1, X \\= Y" to prolog {
            structOf("count","X", consOf("Y","Ys"),consOf("Y","Ys"),"N",consOf("N","X")) impliedBy
                    ( ("N" greaterThan 1) and ( structOf("\\=","X","Y") ))
        },
        "count(X,[X|Xs],Ys,K,T) :- K1 is K + 1, count(X,Xs,Ys,K1,T)" to prolog {
            structOf("count","X",consOf("X","Xs"),"Ys","K","T") impliedBy
                    ( ("K1" `is` (varOf("K").plus(1))) and ( structOf("count","X","Xs","Ys","K1","T")))
        },
        "map_upper_bound(XMax, YMax) :- map_size(XSize, YSize), XMax is XSize - 1, YMax is YSize - 1" to prolog {
            structOf("map_upper_bound","XMax","YMax") impliedBy
                    ( structOf("map_size","XSize","YSize") and (("XMax" `is` ("XSize".minus(1))) and ("YMax" `is` ("YSize".minus(1)))))
        },
        "in_map(X, Y) :- X >= 0, Y >= 0, map_size(XSize, YSize), X < XSize, Y < YSize" to prolog {
            structOf("in_map","X","Y") impliedBy
                    ( ("X" greaterThanOrEqualsTo 0) and (("Y" greaterThanOrEqualsTo 0) and ( structOf("map_size","XSize","YSize") and ( ("X" lowerThan ("XSize")) and ("Y" lowerThan "YSize")))))
        },
        "tile(wall, X, Y) :- \\+ in_map(X, Y)" to prolog {
            structOf("tile","wall","X","Y") impliedBy
                    ( structOf("\\+", structOf("in_map","X","Y")) )
        },
        "draw_char(X, Y) :- tty_size(_, XSize), X >= XSize, NY is Y + 1, draw_char(0, NY)" to prolog {
            structOf("draw_char","X","Y") impliedBy
                    ( structOf("tty_size",`_`,"XSize") and ( ("X" greaterThanOrEqualsTo "XSize") and ( ("NY" `is` ( varOf("Y").plus(1) ))  and (structOf("draw_char",0,"NY"))) ))
        },
        "Y < YMsgs -> write(' ') ; display_offset(XOff, YOff), XMap is X + XOff, YMap is Y + YOff, get_character(XMap, YMap, C), format('~s', [C])" to prolog {
            (("Y" lowerThan "YMsgs") then structOf("write",atomOf(" "))) or
                    ( structOf("display_offset","XOff","YOff") and ( (varOf("XMap") `is` (varOf("X").plus("XOff"))) and ( (varOf("YMap") `is` (varOf("Y").plus("YOff"))) and
                    ( structOf("get_character",varOf("XMap"),varOf("YMap"),varOf("C"))  and ( structOf("format",atomOf("~s"),listOf(varOf("C"))) )))))
        },
        "display_offset(X, Y) :- player(XPos, YPos), tty_size(YSize, XSize), message_lines(YMsgs), X is XPos - floor(XSize / 2), Y is YPos - floor((YSize - YMsgs) / 2)" to prolog {
            structOf("display_offset","X","Y") impliedBy
                    ( structOf("player","XPos","YPos") and ( structOf("tty_size","YSize","XSize")and ( structOf("message_lines","YMsgs")  and (
                            ( "X" `is` ("XPos".minus(structOf("floor","XSize".div(2)) )))  and ( ("Y" `is` ("YPos".minus(structOf("floor",("YSize".minus("YMsgs")).div(2)) ))))))))
        }
    )

    val all = canonicalTerms + expressions
}