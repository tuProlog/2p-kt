package it.unibo.tuprolog.solve

/** Kotest registration of the [TestTagsPreservationDuringResolution] cases. */
fun tagsPreservationDuringResolutionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestTagsPreservationDuringResolution.prototype(solverFactory) }) {
    case("tags-preservation-during-resolution/untagged-query-against-tagged-theory") {
        testUntaggedQueryAgainstTaggedTheory()
    }
    case("tags-preservation-during-resolution/query-tagged-like-theory") { testQueryTaggedLikeTheory() }
    case("tags-preservation-during-resolution/query-tagged-differently-from-theory") {
        testQueryTaggedDifferentlyFromTheory()
    }
    case("tags-preservation-during-resolution/tag-origin-during-computation") { testTagOriginDuringComputation() }
}
