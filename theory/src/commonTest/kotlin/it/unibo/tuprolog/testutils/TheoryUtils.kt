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
import it.unibo.tuprolog.testutils.ReteNodeUtils.ClauseCase
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.core.List as LogicList

/**
 * Utils singleton for testing [Theory]
 *
 * @author Enrico
 */
internal object TheoryUtils {
    /** Contains well formed clauses (the head is a [Struct] and the body doesn't contain [Numeric] values) */
    internal val wellFormedClauseCases get() = ReteNodeUtils.mixedClauseCases

    internal val wellFormedClauses get() = ReteNodeUtils.mixedClauses

    /** Contains the rules among [wellFormedClauseCases] */
    internal val wellFormedRuleCases get() = ReteNodeUtils.ruleCases

    /** Contains a pair which has in its first element half clauses from [wellFormedClauses] in the second element the other half */
    internal val wellFormedClausesHelves =
        Pair(
            wellFormedClauses.subList(0, wellFormedClauses.count() / 2),
            wellFormedClauses.subList(wellFormedClauses.count() / 2, wellFormedClauses.count()),
        )

    /** Contains well formed clauses queries based on [wellFormedClauses] and expected responses from the ClauseDatabase */
    internal val clauseQueryCases get() = ReteNodeUtils.mixedClauseQueryCases

    /** Contains rules queries that have Variable as body, to be used when testing the methods accepting only heads */
    internal val ruleQueryWithVarBodyCases = ReteNodeUtils.ruleQueryCases.filter { it.query.body.isVar }

    /** Contains rules queries that only specify the functor and arity of the head, leaving body and head functor arguments variables */
    internal val ruleQueryByFunctorAndArityCases
        get() =
            ruleQueryWithVarBodyCases.filter { case ->
                case.query.head.argsSequence
                    .all { it is Var }
            }

    /** Contains not well formed clauses (with [Numeric] values in body) */
    internal val notWellFormedClauseCases =
        listOf(
            ClauseCase(
                "rule-with-integer-in-body",
                Clause.of(Struct.of("test", Var.anonymous()), Struct.of("b", Var.anonymous()), Integer.of(1)),
            ),
            ClauseCase("directive-with-real", Directive.of(Atom.of("execute_this"), Real.of(1.5))),
            ClauseCase(
                "rule-with-real-in-body",
                Rule.of(Struct.of("f2", Atom.of("a")), Atom.of("do_something"), Numeric.of(1.5f)),
            ),
        )

    internal val notWellFormedClauses = notWellFormedClauseCases.map { it.clause }

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

    internal val positiveMemberQueryCases =
        listOf(
            ClauseCase(
                "member-of-nested-list-unifying",
                member(
                    LogicList.of(Struct.of("a", Var.of("X"))),
                    LogicList.of(LogicList.of(Struct.of("a", Integer.of(1)))),
                ),
            ),
            ClauseCase("member-of-atom-list-unifying", member(Atom.of("a"), LogicList.of(Atom.of("a")))),
        )

    internal val negativeMemberQueryCases =
        listOf(
            ClauseCase(
                "member-of-nested-list-not-unifying",
                member(
                    LogicList.of(Struct.of("a", Var.of("X"))),
                    LogicList.of(LogicList.of(Struct.of("b", Integer.of(1)))),
                ),
            ),
            ClauseCase("member-of-atom-list-not-unifying", member(Atom.of("a"), LogicList.of(Atom.of("b")))),
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

    internal val deepQueryCases =
        listOf(
            ClauseCase("depth-1", Fact.of(LogicList.of(Var.of("ABC"), Var.of("D")))),
            ClauseCase("depth-2", Fact.of(LogicList.of(LogicList.of(Var.of("AB"), Var.of("C")), Var.of("D")))),
            ClauseCase(
                "depth-3",
                Fact.of(LogicList.of(LogicList.of(LogicList.of(Var.of("A"), Var.of("B")), Var.of("C")), Var.of("D"))),
            ),
        )
}
