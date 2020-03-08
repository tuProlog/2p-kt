package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals

object TermFormatterWithPrettyVariablesUtils {
    val expectedFormats: Map<Term, String> = mapOf(
        Atom.of("a") to "a",
        Atom.of("a b") to "'a b'",
        Integer.of(1) to "1",
        Real.of("3.2") to "3.2",
        Var.of("A") to "A",
        Var.anonymous() to Var.ANONYMOUS_VAR_NAME,
        Var.of("A b") to Var.escapeName("A b"),
        Struct.of("f", Var.of("A"), Atom.of("b")) to "f(A, b)",
        Struct.of("f", Var.of("A"), Var.of("B"), Var.of("A")) to "f(A, B, A_1)",
        Var.of("A").let { Struct.of("f", it, Var.of("B"), it) } to "f(A, B, A)",
        Struct.of("F", Atom.of("a"), Integer.of(1), Real.of("3.2")) to "'F'(a, 1, 3.2)",
        List.empty() to EmptyList.FUNCTOR,
        List.of(Var.of("A"), Var.of("A"), Var.of("A")) to "[A, A_1, A_2]",
        Var.of("A").let { List.of(it, it, it) } to "[A, A, A]",
        List.from(items = listOf(Var.of("A"), Var.of("A"), Var.of("A")), last = Var.of("A")) to "[A, A_1, A_2 | A_3]",
        Var.of("A").let { List.from(items = listOf(it, it, it), last = it) } to "[A, A, A | A]",
        Set.empty() to EmptySet.FUNCTOR,
        Set.of(Var.of("A"), Var.of("A"), Var.of("A")) to "{A, A_1, A_2}",
        Var.of("A").let { Set.of(it, it, it) } to "{A, A, A}",
        Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")) to "','(A, ','(A_1, A_2))",
        Var.of("A").let { Tuple.of(it, it, it) } to "','(A, ','(A, A))"
    )

    fun TermFormatter.assertProperlyFormats(entry: Map.Entry<Term, String>) {
        this.assertProperlyFormats(entry.component2(), entry.component1())
    }

    fun TermFormatter.assertProperlyFormats(expected: String, actual: Term) {
        val formatted = actual.format(this)
        assertEquals(expected, formatted, message = """
            |Formatting 
            |   $actual
            |with ${this::class} should result in
            |   $expected
            |while 
            |   $formatted
            |is produced instead
            |
        """.trimMargin())
    }
}