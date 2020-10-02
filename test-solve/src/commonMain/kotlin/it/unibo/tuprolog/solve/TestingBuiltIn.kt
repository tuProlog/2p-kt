package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import kotlin.collections.listOf as ktListOf

object TestingBuiltIn {
/*
    /**
     * atom_chars Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_chars(X,[t,e,s,t]).
     * ?- atom_chars(test,[t,e,s,t]).
     * ?- atom_chars(test,[t,e,s,T]).
     * ?- atom_chars(test1,[t,e,s,T]).
     * ```
     */
    val atomCharsTesting by lazy {
        prolog {
            ktListOf(
                atom_chars("X", listOf("t", "e", "s", "t")).hasSolutions({ yes("X" to "test") }),
                atom_chars("test", listOf("t", "e", "s", "t")).hasSolutions({ yes() }),
                atom_chars("test", listOf("t", "e", "s", "T")).hasSolutions({ yes("T" to "t") }),
                atom_chars("test1", listOf("t", "e", "s", "T")).hasSolutions({ no() })
            )
        }
    }

    /**
     * atom_length Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_length(test,4).
     * ?- atom_length(test,X).
     * ?- atom_length(X,4).
     * ?- atom_length(42,X).
     * ?- atom_chars(test,5).
     * ```
     */
    val atomLengthTesting by lazy {
        prolog {
            ktListOf(
                atom_length("test", intOf(4)).hasSolutions({ yes() }),
                atom_length("test", "X").hasSolutions({ yes("X" to 4) }),
                atom_length(
                    "X",
                    intOf(5)
                ).hasSolutions({
                    halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("atom_length", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                }),
                atom_length("testLength", "X").hasSolutions({ yes("X" to 10) }),
                atom_length("test", intOf(5)).hasSolutions({ no() })
            )
        }
    }

    /**
     * char_code Testing
     *
     * Contained requests:
     * ```prolog
     * ?- char_code(a,X).
     * ?- char_code(X,97).
     * ?- char_code(X,a).
     * ?- char_code(g,103).
     * ```
     */
    val charCodeTesting by lazy {
        prolog {
            ktListOf(
                char_code("a", "X").hasSolutions({ yes("X" to 97) }),
                char_code("X", intOf(97)).hasSolutions({ yes("X" to "a") }),
                char_code("g", intOf(104)).hasSolutions({ no() }),
                char_code("X", "a").hasSolutions({
                    halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("char_code", 2),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 1
                        )
                    )
                }),
                char_code("g", intOf(103)).hasSolutions({ yes() })
            )
        }
    }

    /**
     * atom_codes Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_codes(abc,X).
     * ?- atom_codes(test,X).
     * ?- atom_codes(test,[116,101,115,116]).
     * ?- atom_codes(test,[112,101,115,116]).
     * ```
     */
    val atomCodesTesting by lazy {
        prolog {
            ktListOf(
                atom_codes("abc", "X")
                    .hasSolutions({ yes("X" to listOf(97, 98, 99)) }),
                atom_codes("test", "X")
                    .hasSolutions({ yes("X" to listOf(116, 101, 115, 116)) }),
                atom_codes("X", listOf(97, 98, 99))
                    .hasSolutions({ yes("X" to "abc") }),
                atom_codes("test", listOf(116, 101, 115, 116))
                    .hasSolutions({ yes() }),
                atom_codes("test", listOf(112, 101, 115, 116))
                    .hasSolutions({ no() })
            )
        }
    }

    /**
     * atom_concat Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_concat(test,concat,X).
     * ?- atom_concat(test,concat,test).
     * ?- atom_concat(test,X,testTest).
     * ```
     */
    val atomConcatTesting by lazy {
        prolog {
            ktListOf(
                atom_concat("test", "concat", "X")
                    .hasSolutions({ yes("X" to atomOf("testconcat")) }),
                atom_concat("test", "concat", "test")
                    .hasSolutions({ no() }),
                atom_concat("test", "X", "testTest")
                    .hasSolutions({ yes("X" to atomOf("Test")) }),
                atom_concat("X", "query", "testquery")
                    .hasSolutions({ yes("X" to atomOf("test")) })
            )
        }
    }

    */

    /*
    [sub_atom('ab', Before, Length, After, Sub_atom),
    [[Before <-- 1, Length <-- 0, Sub_atom <-- ''],
    [Before <-- 1, Length <-- 1, Sub_atom <-- 'a'],
    [Before <-- 1, Length <-- 2, Sub_atom <-- 'ab'],
    [Before <-- 2, Length <-- 0, Sub_atom <-- ''],
    [Before <-- 2, Length <-- 1, Sub_atom <-- 'b'],
    [Before <-- 3, Length <-- 0, Sub_atom <-- '']]].

    [sub_atom(Banana, 3, 2, _, S2), instantiation_error].
    [sub_atom(f(a), 2, 2, _, S2), type_error(atom,f(a))].
    [sub_atom('Banana', 4, 2, _, 2), type_error(atom,2)].
    [sub_atom('Banana', a, 2, _, S2), type_error(integer,a)].
    [sub_atom('Banana', 4, n, _, S2), type_error(integer,n)].
    [sub_atom('Banana', 4, _, m, S2), type_error(integer,m)].



    val subAtomTesting by lazy {
        prolog {
            ktListOf(
                sub_atom("abracadabra", intOf(0), intOf(5), "_", "S")
                    .hasSolutions({ yes("X" to atomOf("abrac")) }),
                sub_atom("abracadabra", "_", intOf(5), intOf(0), "S")
                    .hasSolutions({ yes("X" to atomOf("dabra")) }),
                sub_atom("abracadabra", intOf(3), "L", intOf(3), "S")
                    .hasSolutions({ yes("L" to 5); yes("X" to atomOf("acada")) }),
                sub_atom("abracadabra", "B", 2, "A", "ab")
                    .hasSolutions({ yes("B" to 0); yes("A" to 9) }).also { ("B" to 0);("A" to 9) },
                sub_atom("banana", intOf(3), intOf(2), "_", "S")
                    .hasSolutions({ yes("S" to atomOf("an")) }),
            )
        }
    } */

    /**
     * [number_chars(33,L), [[L <-- ['3','3']]]].
     * [number_chars(33,['3','3']), success].
     * [number_chars(X,['3','3']), [[X <-- 33]].
     * [number_chars(A,['-','2','5']), [[A <-- (-25)]]].
     * [number_chars(3.3,['3','.','3']), success].
     * [number_chars(A,['\n',' ','3']), [[A <-- 3]]].
     * number_chars(A,['4','.','2']), [[A <-- 4.2]]].
     *[number_chars(X,['3','.','3','E','+','0']), [[X <-- 3.3]]].
     * [number_chars(A,L), instantiation_error].
     *
     */

    val numbCharsTesting by lazy {
        prolog {
            ktListOf(
                number_chars(33, "L").hasSolutions({ yes("L" to listOf("3", "3")) }),
                number_chars(33, listOf("3", "3")).hasSolutions({ yes() }),
                number_chars("X", listOf("3", "3")).hasSolutions({ yes("X" to 33) }),
                number_chars("X", listOf("-", "2", "5")).hasSolutions({ yes("X" to intOf(-25)) }),
                number_chars(3.3, listOf("3", ".", "3")).hasSolutions({ yes() }),
                number_chars("X", listOf("\n", "3")).hasSolutions({ yes("X" to 3) }),
                number_chars("X", listOf("4", ".", "2")).hasSolutions({ yes("X" to 4.2) }),
                // number_chars("X",listOf("3",".","3","E","+","0")).hasSolutions({yes("X" to 3.3)}),
                number_chars("X", "L").hasSolutions({
                    halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("number_chars", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                }),
            )
        }
    }

    /**
     * [number_codes(33,L), [[L <-- [0'3,0'3]]]].
     * [number_codes(33.0,L), [[L <-- [51,51,46,48]]]].
     * [number_codes(33,L), [[L <-- [0'3,0'3]]]].
     * [number_codes(33,[0'3,0'3]), success].
     * [number_codes(33.0,[0'3,0'.,0'3,0'E,0'+,0'0,0'1]), success].
     * [number_codes(A,[0'-,0'2,0'5]), [[A <-- (-25)]]].
     * [number_codes(A,[0'0,39,0'a]), [[A <-- 97]]].
     */

    val numbCodesTesting by lazy {
        prolog {
            ktListOf(
                number_codes(33, "L").hasSolutions({ yes("L" to listOf(51, 51)) }),
                number_codes("X", listOf(51, 51)).hasSolutions({ yes("X" to 33) }),
                number_codes(33.0, "L").hasSolutions({ yes("L" to listOf("51", "51", "46", "48")) }),
                number_codes(33.0, "L").hasSolutions({ yes("L" to listOf("0'3", "0'3")) }),
                number_codes(33, listOf("0'3", "0'3")).hasSolutions({ yes() }),
                number_codes(33.0, listOf("0'3", "0'.", "0'3", "0'E", "0'+", "0'0", "0'1")).hasSolutions({ yes() }),
                number_codes("X", listOf("0'2", "0'5")).hasSolutions({ yes("X" to 25) }),
                number_codes("X", listOf("0'0", "39", "0'a")).hasSolutions({ yes("X" to 97) }),

            )
        }
    }
}
