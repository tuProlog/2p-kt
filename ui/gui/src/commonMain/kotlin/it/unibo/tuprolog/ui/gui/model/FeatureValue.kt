package it.unibo.tuprolog.ui.gui.model

/** A JSON-like value an extension's feature (e.g. PLP's BDD/probability) attaches to a page, keyed by name in
 * `PageFeatureState` - toolkit-neutral so any frontend can render it without knowing the extension's own types. */
sealed interface FeatureValue {
    /** A plain string value. */
    data class Text(
        val value: String,
    ) : FeatureValue

    /** A numeric value (e.g. PLP's per-solution probability). */
    data class Number(
        val value: Double,
    ) : FeatureValue

    /** A boolean flag (e.g. "is a BDD available for the current solution"). */
    data class BooleanValue(
        val value: Boolean,
    ) : FeatureValue

    /** An ordered list of nested values. */
    data class ListValue(
        val values: List<FeatureValue>,
    ) : FeatureValue

    /** A nested key-value structure. */
    data class ObjectValue(
        val values: Map<String, FeatureValue>,
    ) : FeatureValue

    /** No value is available. */
    data object NullValue : FeatureValue
}
