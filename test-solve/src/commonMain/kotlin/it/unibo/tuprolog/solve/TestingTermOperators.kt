package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

object TestingTermOperators {

    /**
     * Standard operator greater than
     *
     * Contained requests:
     * ```prolog
     * ?- @>(1,1.0).
     * ?- @>(1.0,1).
     * ?- @>(stringTestA,stringTestZ).
     * ?- @>(stringTest,1).
     * ```
     */
    val greaterThanTesting by lazy {
        prolog {
            ktListOf(
                "@>"(intOf(1), realOf(1.0)).hasSolutions({ yes() }),
                "@>"(realOf(1.0), intOf(1)).hasSolutions({ no() }),
                "@>"("stringTestA", "stringTestZ").hasSolutions({ no() }),
                "@>"("stringTest", intOf(1)).hasSolutions({ yes() })
            )
        }
    }

    /**
     * Standard operator greater than or Equal
     *
     * Contained requests:
     * ```prolog
     * ?- @>=(1,1).
     * ?- @>=(stringTest,stringTest).
     * ?- @>=(stringTest,stringTest1).
     * ?- @>=(stringTest,1).
     * ```
     */
    val greaterThanOrEqualTesting by lazy {
        prolog {
            ktListOf(
                "@>="(intOf(1), intOf(1)).hasSolutions({ yes() }),
                "@>="("stringTest", "stringTest").hasSolutions({ yes() }),
                "@>="("stringTest", "stringTest1").hasSolutions({ no() }),
                "@>="("stringTest", intOf(1)).hasSolutions({ yes() }
                )
            )
        }
    }

    /**
     * Standard operator Equal
     *
     * Contained requests:
     * ```prolog
     * ?- =@=(1.0,1.0).
     * ?- =@=(stringTest,stringTest).
     * ?- =@=(stringTest,1.0).
     * ```
     */
    val equalTesting by lazy {
        prolog {
            ktListOf(
                "=@="(realOf(1.0), realOf(1.0)).hasSolutions({ yes() }),
                "=@="("stringTest", "stringTest").hasSolutions({ yes() }),
                "=@="("stringTest", realOf(1.0)).hasSolutions({ no() })
            )
        }
    }

    /**
     * Standard operator Lower Than
     *
     * Contained requests:
     * ```prolog
     * ?- @<(1.0,1).
     * ?- @<(1,1.0).
     * ?- @<(stringTestA,stringTestZ).
     * ?- @<(1,stringTest).
     * ```
     */

    val lowerThanTesting by lazy {
        prolog {
            ktListOf(
                "@<"(realOf(1.0), intOf(1)).hasSolutions({ yes() }),
                "@<"(intOf(1), realOf(1.0)).hasSolutions({ no() }),
                "@<"("stringTestA", "stringTestZ").hasSolutions({ yes() }),
                "@<"(intOf(1), "stringTest").hasSolutions({ yes() })
            )
        }
    }

    /**
     * Standard operator Lower Than or Equal
     *
     * Contained requests:
     * ```prolog
     * ?- @=<(1,1.0).
     * ?- @=<(1.0,1.0).
     * ?- @=<(stringTest,stringTest).
     * ```
     */
    val lowerThanOrEqualTesting by lazy {
        prolog {
            ktListOf(
                "@=<"(intOf(1), realOf(1.0)).hasSolutions({ no() }),
                "@=<"(realOf(1.0), realOf(1.0)).hasSolutions({ yes() }),
                "@=<"("stringTest", "stringTest").hasSolutions({ yes() })
            )
        }
    }

    /**
     * Standard operator Not Equal
     *
     * Contained requests:
     * ```prolog
     * ?- \=@=(1.0,1.0).
     * ?- \=@=(stringTest,stringTest).
     * ?- \=@=(stringTest,1.0).
     * ```
     */
    val notEqualTesting by lazy {
        prolog {
            ktListOf(
                "\\=@="(realOf(1.0), realOf(1.0)).hasSolutions({ no() }),
                "\\=@="("stringTest", "stringTest").hasSolutions({ no() }),
                "\\=@="("stringTest", realOf(1.0)).hasSolutions({ yes() })
            )
        }
    }

}