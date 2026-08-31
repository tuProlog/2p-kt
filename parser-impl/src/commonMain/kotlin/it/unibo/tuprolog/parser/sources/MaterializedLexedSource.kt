package it.unibo.tuprolog.parser.sources

/** An immutable, self-contained token and source snapshot. */
interface MaterializedLexedSource : LexedSource {
    override val source: SourceText

    override fun materialize(): MaterializedLexedSource = this
}
