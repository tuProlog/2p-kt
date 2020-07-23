package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

object TestingStandardOperator {

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
                "@>"(1,1.0).hasSolutions({yes()}),
                "@>"(1.0,1).hasSolutions({no()}),
                "@>"("stringTestA","stringTestZ").hasSolutions({no()}),
                "@>"("stringTest",1).hasSolutions({yes()})
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
                "@>="(1,1).hasSolutions({yes()}),
                "@>="("stringTest","stringTest").hasSolutions({yes()}),
                "@>="("stringTest","stringTest1").hasSolutions({no()}),
                "@>="("stringTest",1).hasSolutions({yes()}
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
                "=@="(1.0,1.0).hasSolutions(
                    { yes() }
                ),
                "=@="("stringTest","stringTest").hasSolutions(
                    { no() }
                ),
                "=@="("stringTest",1.0).hasSolutions(
                    { no() }
                )
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
                "@<"(1.0,1).hasSolutions({yes()}),
                "@<"(1,1.0).hasSolutions({no()}),
                "@<"("stringTestA","stringTestZ").hasSolutions({yes()}),
                "@<"(1,"stringTest").hasSolutions({yes()})
            )
        }
    }

    /**
     * Standard operator Lower Than or Equal
     *
     * Contained requests:
     * ```prolog
     * ?- @<=(1,1.0).
     * ?- @<=(1.0,1.0).
     * ?- @<=(stringTest,stringTest).
     * ```
     */

    val lowerThanOrEqualTesting by lazy {
        prolog {
            ktListOf(
                "@<="(1,1.0).hasSolutions({no()}
                ),
                "@<="(1.0,1.0).hasSolutions({yes()}
                ),
                "@<="("stringTest","stringTest").hasSolutions({yes()}
                )
            )
        }
    }

    val notEqual by lazy{

    }

}