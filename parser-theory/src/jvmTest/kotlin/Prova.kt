import it.unibo.tuprolog.theory.parsing.ClauseDatabaseParser

fun main() {
    with(ClauseDatabaseParser.withStandardOperators) {
        val db = parseClauseDatabase("f(1).\nf(2).")
        println(db)
    }
}