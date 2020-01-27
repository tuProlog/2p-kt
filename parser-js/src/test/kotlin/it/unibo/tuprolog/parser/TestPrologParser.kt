package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestPrologParser {

    @Test
    fun testInitialisation() {
        val lexer = PrologLexer("1")
        val parser = PrologParser(BufferedTokenStream(lexer))
        assertEquals(PrologParser.VARIABLE, 1)
        assertEquals(PrologParser.RULE_singletonTerm,0)
    }

    @Test
    fun testInteger(){
        val parser = PrologParser(BufferedTokenStream(PrologLexer("1")))
        val tc = parser.singletonTerm().term()
        assertTrue(tc.isNum && !tc.isExpr && !tc.isList && !tc.isStruct && !tc.isVar)
        val nc = tc.number()
        assertTrue(nc.isInt && !nc.isReal)
        val ic = nc.integer()
        assertEquals(ic.value.text.toInt(), 1)
    }

    @Test
    fun testReal(){
        val parser = PrologParser(BufferedTokenStream(PrologLexer("1.1")))
        val tc = parser.singletonTerm().term()
        assertTrue(tc.isNum && !tc.isExpr && !tc.isList && !tc.isStruct && !tc.isVar)
        val nc = tc.number()
        assertTrue(nc.isReal && !nc.isInt)
        val rc = nc.real()
        assertEquals(rc.value.text.toDouble(),1.1)
    }

    @Test
    fun testAtom(){
        val parser = PrologParser(BufferedTokenStream(PrologLexer("a")))
        val tc = parser.singletonTerm().term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val sc = tc.structure()
        assertTrue(sc.arity == sc.args.count() &&
                sc.arity == 0 &&
                !sc.isList &&
                !sc.isSet &&
                !sc.isTruth &&
                sc.functor.text == "a" &&
                sc.functor.type == PrologLexer.ATOM)
    }

    @Test
    fun testString(){
        listOf("'a'","\"a\"").forEach{
            val parser = PrologParser(BufferedTokenStream(PrologLexer(it)))
            val tc = parser.singletonTerm().term()
            assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
            val s = tc.structure()
            assertTrue(s.arity == s.args.count() &&
                    s.arity == 0 &&
                    s.isSet && !s.isList && !s.isTruth &&
                    s.functor.text == "a" &&
                    (s.functor.type == PrologLexer.DQ_STRING || s.functor.type == PrologLexer.SQ_STRING)
            )
        }
    }

    @Test
    fun testTrue(){
        val parser = PrologParser(BufferedTokenStream(PrologLexer("true")))
        val tc = parser.singletonTerm().term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val s = tc.structure()
        assertTrue(s.arity == s.args.count() &&
                s.isTruth && !s.isList && !s.isString &&
                s.functor.text == "true" &&
                s.functor.type == PrologLexer.BOOL)
    }

    @Test
    fun testFalse(){
        val parser = PrologParser(BufferedTokenStream(PrologLexer("fail")))
        val tc = parser.singletonTerm().term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val s = tc.structure()
        assertTrue(s.arity == s.args.count() &&
                s.isTruth && !s.isList && !s.isString &&
                s.functor.text == "fail" &&
                s.functor.type == PrologLexer.BOOL)
    }

    @Test
    fun testEmptyList(){
        sequenceOf("[]","[ ]","[   ]").forEach{
            val parser = PrologParser(BufferedTokenStream(PrologLexer(it)))
            val tc = parser.singletonTerm().term()
            assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
            val s = tc.structure()
            assertTrue(s.arity == s.args.count() &&
                    s.arity == 0 &&
                    s.isList  && !s.isTruth && !s.isString &&
                    s.functor.type == PrologLexer.EMPTY_LIST )
        }
    }

    @Test
    fun testVar(){
        sequenceOf("A","_A","_1A","A_").forEach{
            val parser = PrologParser(BufferedTokenStream(PrologLexer(it)))
            val tc = parser.singletonTerm().term()
            assertTrue(tc.isVar && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isStruct)
            val v = tc.variable()
            assertTrue(!v.isAnonymous &&
                    v.value.text.contains("A") &&
                    v.value.type == PrologLexer.VARIABLE)
        }
    }

    @Test
    fun testSingletonList(){
        sequenceOf("[1]", "[1 ]","[ 1]", "[ 1 ]").forEach{
            val parser = PrologParser(BufferedTokenStream(PrologLexer(it)))
            val tc = parser.singletonTerm().term()
            assertTrue(tc.isList && !tc.isExpr && !tc.isVar && !tc.isNum && !tc.isStruct)
            val l = tc.list()
            assertTrue(l.length == l.items.count() &&
                    l.length == 1 &&
                    !l.hasTail && l.tail == null)
            val expr = l.items[0]
            assertTrue(expr.isTerm && expr.left != null && expr.operators.count() == 0 && expr.right.count() == 0)
            val t = expr.left
            assertTrue(t.isNum && !t.isVar && !t.isList && !t.isStruct && !t.isExpr)
            val n = t.number()
            assertTrue(n.isInt && !n.isReal)
            val i = n.integer()
            assertEquals(i.value.text.toInt(),1)
        }
    }

}