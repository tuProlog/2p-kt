package it.unibo.tuprolog.solve.libs.io

object ExampleFiles {
    val PARENTS =
        """
            |male(james1).
            |male(charles1).
            |male(charles2).
            |male(james2).
            |male(george1).
            |
            |female(catherine).
            |female(elizabeth).
            |female(sophia).
            |
            |parent(charles1, james1).
            |parent(elizabeth, james1).
            |parent(charles2, charles1).
            |parent(catherine, charles1).
            |parent(james2, charles1).
            |parent(sophia, elizabeth).
            |parent(george1, sophia).
            |
            |mother(X, Y) :- female(X), parent(X, Y).
            |father(X, Y) :- male(X), parent(X, Y).
        """.trimMargin()

    val WRONG_PARENTS =
        """
            |male(james1).
            |male(charles1).
            |male(charles2).
            |male(james2).
            |male(george1) % missing dot here
            |
            |female(catherine).
            |female(elizabeth).
            |female(sophia).
            |
            |parent(charles1, james1).
            |parent(elizabeth, james1).
            |parent(charles2, charles1).
            |parent(catherine, charles1).
            |parent(james2, charles1).
            |parent(sophia, elizabeth).
            |parent(george1, sophia).
            |
            |mother(X, Y) :- female(X), parent(X, Y).
            |father(X, Y) :- male(X), parent(X, Y).
        """.trimMargin()
}
