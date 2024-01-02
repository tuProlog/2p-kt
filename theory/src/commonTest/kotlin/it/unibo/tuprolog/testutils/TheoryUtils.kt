package it.unibo.tuprolog.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.core.List as LogicList

/**
 * Utils singleton for testing [Theory]
 *
 * @author Enrico
 */
internal object TheoryUtils {
    /** Contains well formed clauses (the head is a [Struct] and the body doesn't contain [Numeric] values) */
    internal val wellFormedClauses get() = ReteNodeUtils.mixedClauses

    /** Contains a pair which has in its first element half clauses from [wellFormedClauses] in the second element the other half */
    internal val wellFormedClausesHelves =
        Pair(
            wellFormedClauses.subList(0, wellFormedClauses.count() / 2),
            wellFormedClauses.subList(wellFormedClauses.count() / 2, wellFormedClauses.count()),
        )

    /** Contains well formed clauses queries based on [wellFormedClauses] and expected responses from the ClauseDatabase */
    internal val clausesQueryResultsMap get() = ReteNodeUtils.mixedClausesQueryResultsMap

    /** Contains rules queries that have Variable as body, to be used when testing the methods accepting only heads */
    internal val rulesQueryWithVarBodyResultsMap =
        clausesQueryResultsMap
            .filterKeys { it is Rule && it.body.isVar }
            .mapKeys { it.key as Rule }

    /** Contains rules queries that only specify the functor and arity of the head, leaving body and head functor arguments variables */
    internal val rulesQueryResultByFunctorAndArity
        get() = rulesQueryWithVarBodyResultsMap.filterKeys { rule -> rule.head.argsSequence.all { it is Var } }

    /** Contains not well formed clauses (with [Numeric] values in body) */
    internal val notWellFormedClauses =
        listOf(
            Clause.of(Struct.of("test", Var.anonymous()), Struct.of("b", Var.anonymous()), Integer.of(1)),
            Directive.of(Atom.of("execute_this"), Real.of(1.5)),
            Rule.of(Struct.of("f2", Atom.of("a")), Atom.of("do_something"), Numeric.of(1.5f)),
        )

    internal val memberClause =
        listOf(
            Scope.empty {
                factOf(structOf("member", varOf("H"), consOf(varOf("H"), anonymous())))
            },
        )

    internal fun member(
        first: Term,
        second: Term,
    ): Fact = Fact.of(Struct.of("member", first, second))

    internal val positiveMemberQueries =
        listOf(
            member(
                LogicList.of(Struct.of("a", Var.of("X"))),
                LogicList.of(LogicList.of(Struct.of("a", Integer.of(1)))),
            ),
            member(Atom.of("a"), LogicList.of(Atom.of("a"))),
        )

    internal val negativeMemberQueries =
        listOf(
            member(
                LogicList.of(Struct.of("a", Var.of("X"))),
                LogicList.of(LogicList.of(Struct.of("b", Integer.of(1)))),
            ),
            member(Atom.of("a"), LogicList.of(Atom.of("b"))),
        )

    internal val deepClause =
        listOf(
            Fact.of(
                LogicList.of(
                    LogicList.of(
                        LogicList.of(
                            Atom.of("a"),
                            Atom.of("b"),
                        ),
                        Atom.of("c"),
                    ),
                    Atom.of("d"),
                ),
            ),
        )

    internal val deepQueries =
        sequenceOf(
            LogicList.of(Var.of("ABC"), Var.of("D")),
            LogicList.of(LogicList.of(Var.of("AB"), Var.of("C")), Var.of("D")),
            LogicList.of(LogicList.of(LogicList.of(Var.of("A"), Var.of("B")), Var.of("C")), Var.of("D")),
        ).map { Fact.of(it) }.toList()
}
