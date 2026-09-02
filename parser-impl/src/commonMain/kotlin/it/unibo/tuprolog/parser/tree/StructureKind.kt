package it.unibo.tuprolog.parser.tree

/** Syntactic spelling used to produce a [StructureNode]. */
enum class StructureKind {
    ORDINARY,
    TRUTH,
    SINGLE_QUOTED,
    DOUBLE_QUOTED,
    CUT,
    EMPTY_LIST,
    EMPTY_BLOCK,
    EXPLICIT_OPERATOR,
}
