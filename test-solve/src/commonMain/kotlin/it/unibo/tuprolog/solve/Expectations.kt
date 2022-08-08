package it.unibo.tuprolog.solve

data class Expectations(
    val classicShouldWork: Boolean = false,
    val streamsShouldWork: Boolean = false,
    val prologShouldWork: Boolean = false,
    val problogShouldWork: Boolean = false,
)
