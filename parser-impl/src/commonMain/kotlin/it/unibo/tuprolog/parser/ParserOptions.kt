package it.unibo.tuprolog.parser

/**
 * Configures syntax parsing safeguards and compatibility behavior.
 *
 * @property maximumNestingDepth positive maximum recursive grammar depth
 * @property ambiguityPolicy strategy for occurrences matching multiple operator definitions
 * @throws IllegalArgumentException if [maximumNestingDepth] is not positive
 */
data class ParserOptions(
    val maximumNestingDepth: Int = 1024,
    val ambiguityPolicy: OperatorAmbiguityPolicy = OperatorAmbiguityPolicy.REJECT,
) {
    init {
        require(maximumNestingDepth > 0) { "maximumNestingDepth must be positive" }
    }
}
