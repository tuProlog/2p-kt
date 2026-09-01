@file:JvmName("Utils")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.PrologTermParserVisitor
import it.unibo.tuprolog.core.parsing.toClause
import it.unibo.tuprolog.core.parsing.toDefinition
import it.unibo.tuprolog.parser.PrologParseSession
import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import kotlin.jvm.JvmName

internal fun parseClausesLazily(session: PrologParseSession): Sequence<Clause> =
    sequence {
        var clauseIndex = 0
        try {
            var clauseAST = session.parseNextClause()
            var scope = Scope.empty()
            while (clauseAST != null) {
                val visitor = PrologTermParserVisitor(scope)
                val clause = clauseAST.root.accept(visitor)
                with(clauseAST.source) {
                    val clause = clause.toClause(id, start.line, start.column)
                    yield(clause)
                    for (goal in clause.bodyItems.filterIsInstance<Struct>()) {
                        for (operator in Operator.manyFromTerm(goal)) {
                            try {
                                session.operators.define(operator.toDefinition())
                            } catch (e: InvalidOperatorDefinitionException) {
                                throw throw ParseException(
                                    input = session.input.source.let { it.id ?: it.text() },
                                    offendingSymbol = clauseAST.toRepresentation(),
                                    line = clauseAST.root.span.start.line + 1,
                                    column = clauseAST.root.span.start.column + 1,
                                    message = e.message,
                                    throwable = e,
                                ).also { it.clauseIndex = clauseIndex }
                            }
                        }
                    }
                }
                clauseAST = session.parseNextClause()
                scope = Scope.empty()
                clauseIndex++
            }
        } catch (e: PrologSyntaxException) {
            throw ParseException(
                input = session.input.source.let { it.id ?: it.text() },
                offendingSymbol = e.offendingText,
                line = e.span.start.line + 1,
                column = e.span.start.column + 1,
                message = e.message,
                throwable = e,
            ).also { it.clauseIndex = clauseIndex }
        }
    }
