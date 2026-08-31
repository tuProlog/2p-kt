package it.unibo.tuprolog.parser.tree

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
