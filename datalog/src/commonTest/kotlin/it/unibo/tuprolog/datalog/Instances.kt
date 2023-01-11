package it.unibo.tuprolog.datalog

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.theory.Theory

object Instances {
    val theoryWithCompoundTerms: Theory = logicProgramming {
        theoryOf(
            rule { "f"(X) impliedBy "g"("h"(X)) }
        )
    }

    val theoryWithFreeVariablesInTheHead: Theory = logicProgramming {
        theoryOf(
            rule { "f"(X, Y).impliedBy("g"(X, Z), "h"(Z, X)) }
        )
    }

    val theoryWithFreeVariablesInNegatedBodyLiterals: Theory = logicProgramming {
        theoryOf(
            rule { "f"(X).impliedBy("g"(X, Z), "not"("h"(Y)), "i"(Z, X)) }
        )
    }

    val theoryWithRecursion: Theory = logicProgramming {
        theoryOf(
            rule { "a"(X).impliedBy("b"(X), "c"(X)) },
            rule { "b"(X).impliedBy("d"(X), "e"(X)) },
            rule { "c"(X).impliedBy("f"(X), "g"(X)) },
            fact { "d"(1) },
            fact { "e"(2) },
            rule { "f"(X) impliedBy "a"(X) },
            fact { "g"(4) },
        )
    }

    val datalogTheory: Theory = logicProgramming {
        theoryOf(
            rule { "a"(X).impliedBy("b"(X), "c"(X)) },
            rule { "b"(X).impliedBy("d"(X), "e"(X)) },
            rule { "c"(X).impliedBy("f"(X), "g"(X)) },
            fact { "d"(1) },
            fact { "e"(2) },
            fact { "f"(3) },
            fact { "g"(4) },
            rule { "f"(X, Y).impliedBy("g"(X, Z), "h"(Z, Y)) }
        )
    }
}
