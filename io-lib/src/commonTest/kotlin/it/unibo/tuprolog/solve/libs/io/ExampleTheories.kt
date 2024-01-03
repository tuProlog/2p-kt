package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming

object ExampleTheories {
    val PARENTS =
        logicProgramming {
            theoryOf(
                fact { "male"("james1") },
                fact { "male"("charles1") },
                fact { "male"("charles2") },
                fact { "male"("james2") },
                fact { "male"("george1") },
                fact { "female"("catherine") },
                fact { "female"("elizabeth") },
                fact { "female"("sophia") },
                fact { "parent"("charles1", "james1") },
                fact { "parent"("elizabeth", "james1") },
                fact { "parent"("charles2", "charles1") },
                fact { "parent"("catherine", "charles1") },
                fact { "parent"("james2", "charles1") },
                fact { "parent"("sophia", "elizabeth") },
                fact { "parent"("george1", "sophia") },
                rule { "mother"(X, Y) `if` ("female"(X) and "parent"(X, Y)) },
                rule { "father"(X, Y) `if` ("male"(X) and "parent"(X, Y)) },
            )
        }
}
