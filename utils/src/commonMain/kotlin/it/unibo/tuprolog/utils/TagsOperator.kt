package it.unibo.tuprolog.utils

/**
 * A [BinaryOperator] over [Taggable.tags] maps, used to combine the tags of two [Taggable] instances into
 * a single tags map, e.g. when two tagged objects are merged into one and their tags need to be reconciled
 * (see, for instance, `Substitution.plus(Substitution, TagsOperator)` in the `:core` module).
 */
typealias TagsOperator = BinaryOperator<Map<String, Any>>
