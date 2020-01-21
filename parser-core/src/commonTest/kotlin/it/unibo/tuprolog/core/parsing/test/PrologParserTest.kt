package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface PrologParserTest {

    companion object {
        val scope: Scope
            get() = Scope.empty()
    }
    fun assertEquals(x: Term, y: Term){
        assertTrue(x.structurallyEquals(y),"Failing assertion: <$x> == <$y>")
    }

    fun <T> assertEquals(x: T, y: T){
        assertEquals(x,y,"Failing assertion: <$x> == <$y>")
    }

    fun getOperatorSet() = OperatorSet.EMPTY

    fun parseTerm(string: String): Term{
        val ops = getOperatorSet()
        return if(ops!=null)
            with(TermParser.withNoOperator()){
                Term.parse(string,this.defaultOperatorSet)
            } else
            with(TermParser.withOperators(ops)){
                Term.parse(string,this.defaultOperatorSet)
            }
    }
}