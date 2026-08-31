package it.unibo.tuprolog.parser

data class ParserOptions(
    val maximumNestingDepth: Int = 1024,
    val ambiguityPolicy: OperatorAmbiguityPolicy = OperatorAmbiguityPolicy.LEGACY_ORDER,
) {
    init {
        require(maximumNestingDepth > 0) { "maximumNestingDepth must be positive" }
    }
}
