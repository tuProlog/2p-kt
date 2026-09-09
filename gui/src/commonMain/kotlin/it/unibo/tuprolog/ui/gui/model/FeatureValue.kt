package it.unibo.tuprolog.ui.gui.model

sealed interface FeatureValue {
    data class Text(
        val value: String,
    ) : FeatureValue

    data class Number(
        val value: Double,
    ) : FeatureValue

    data class BooleanValue(
        val value: Boolean,
    ) : FeatureValue

    data class ListValue(
        val values: List<FeatureValue>,
    ) : FeatureValue

    data class ObjectValue(
        val values: Map<String, FeatureValue>,
    ) : FeatureValue

    data object NullValue : FeatureValue
}
