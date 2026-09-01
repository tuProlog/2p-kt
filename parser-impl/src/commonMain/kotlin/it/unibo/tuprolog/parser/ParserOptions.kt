package it.unibo.tuprolog.parser

data class ParserOptions(
    val maximumNestingDepth: Int = 1024,
    val ambiguityPolicy: OperatorAmbiguityPolicy = OperatorAmbiguityPolicy.REJECT,
) {
    init {
        require(maximumNestingDepth > 0) { "maximumNestingDepth must be positive" }
    }
}
