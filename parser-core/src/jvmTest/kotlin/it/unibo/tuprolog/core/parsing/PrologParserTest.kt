package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import org.junit.Assert

interface PrologParserTest {

    val scope: Scope
        get() = Scope.empty()

    fun assertEquals(x: Term, y: Term){
        Assert.assertTrue("Failing assertion: <$x> == <$y>",x.structurallyEquals(y))
    }

    fun <T> assertEquals(x: T, y: T){
        Assert.assertEquals("Failing assertion: <$x> == <$y>",x,y)
    }

    fun getOperatorSet() = OperatorSet.EMPTY

    fun parseTerm(string: String): Term{
        val ops = getOperatorSet()
        return if(ops!=null)
            with(TermParser.withNoOperator()){
                Term.parse(string,this.defaultOperatorSet)
        } else
            with(TermParserImpl(ops)){
                Term.parse(string,this.defaultOperatorSet)
        }
    }
}