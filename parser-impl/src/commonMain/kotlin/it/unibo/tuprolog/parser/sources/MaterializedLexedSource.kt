package it.unibo.tuprolog.parser.sources

/** An immutable, self-contained token and source snapshot. */
interface MaterializedLexedSource : LexedSource {
    /** Complete immutable source fragment owned by this snapshot. */
    override val source: SourceText

    /** Returns this already materialized instance. */
    override fun materialize(): MaterializedLexedSource = this
}
