@file:JvmName("Utils")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.parsing.PrologTermParserVisitor
import it.unibo.tuprolog.core.parsing.toClause
import it.unibo.tuprolog.parser.PrologParseSession
import kotlin.jvm.JvmName

internal fun parseClausesLazily(session: PrologParseSession): Sequence<Clause> =
    sequence {
        var clauseAST = session.parseNextClause()
        var scope = Scope.empty()
        while (clauseAST != null) {
            val visitor = PrologTermParserVisitor(scope)
            val clause = clauseAST.root.accept(visitor)
            with(clauseAST.source) {
                yield(clause.toClause(id, start.line, start.column))
            }
            clauseAST = session.parseNextClause()
            scope = Scope.empty()
        }
    }
