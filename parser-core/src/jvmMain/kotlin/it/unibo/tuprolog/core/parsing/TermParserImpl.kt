package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet

class TermParserImpl(override val defaultOperatorSet: OperatorSet) : TermParser {


    override fun Term.Companion.parse(input: String, operators: OperatorSet): Term {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}