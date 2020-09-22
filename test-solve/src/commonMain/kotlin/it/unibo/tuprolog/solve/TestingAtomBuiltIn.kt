package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

object TestingAtomBuiltIn {

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
                /*atom_length(
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
                }),*/
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
                /*   char_code("X", "a").hasSolutions({halt(
                           TypeError.forArgument(
                               DummyInstances.executionContext,
                               Signature("char_code", 2),
                               TypeError.Expected.INTEGER,
                               args.get(1),
                               index = 1
                           ))}),*/
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
}
