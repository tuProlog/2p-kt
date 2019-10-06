package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import org.gciatto.kt.math.BigInteger

object Natural : UnaryPredicate("natural") {
    override fun uncheckedImplementation(request: Solve.Request): Sequence<Solve.Response> =
            when (val x = request.arguments[0]) {
                is Var -> generateValues(x).map { request.toSuccessfulResponse(Substitution.of(x, it)) }
                is Integer -> sequenceOf(request.toResponse(checkValue(x)))
                else -> sequenceOf(request.toFailingResponse())
            }

    private fun generateValues(variable: Var): Sequence<Term> = sequence {
        var i = BigInteger.ZERO

        while (true) {
            yield(Integer.of(i))

            i += BigInteger.ONE
        }
    }

    private fun checkValue(value: Integer): Boolean =
            value.intValue.signum >= 0

}