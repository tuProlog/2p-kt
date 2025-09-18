package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.exception.ClauseMappingException

/** [ClauseMapper] implementation that handles special directive-related mappings
 *
 * @author Jason Dellaluce */
internal object DirectiveClauseMapper : ClauseMapper {
    override fun isCompatible(clause: Clause): Boolean = clause.isDirective

    override fun apply(clause: Clause): List<Clause> {
        val body = clause.body
        if (clause !is Directive) {
            throw ClauseMappingException("Clause is not an instance of Directive: $clause")
        }

        if (body is Struct && (body.functor == "static" || body.functor == "dynamic")) {
            val indicatorTerm = body[0]
            if (indicatorTerm !is Indicator) {
                throw ClauseMappingException("Malformed directive: $clause")
            }
            val name = indicatorTerm.nameTerm
            val arity = indicatorTerm.arityTerm
            if (arity !is Numeric) {
                throw ClauseMappingException("Malformed directive: $clause")
            }

            return listOf(
                Directive.of(
                    Struct.of(
                        body.functor,
                        Indicator.of(name, Numeric.of(arity.decimalValue.toInt() + 1)),
                    ),
                ),
            )
        }

        if (body is Struct && (body.functor == "initialization" || body.functor == "solve")) {
            return listOf(
                Directive.of(
                    Struct.of(
                        body.functor,
                        body[0].withBodyExplanation(Var.anonymous()),
                    ),
                ),
            )
        }

        return listOf(clause)
    }
}
