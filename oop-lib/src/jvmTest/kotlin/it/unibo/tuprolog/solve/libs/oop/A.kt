package it.unibo.tuprolog.solve.libs.oop

sealed class A(
    open val z: String,
) {
    data class B(
        val x: Int,
        override val z: String,
    ) : A(z)

    data class C(
        val y: Long,
        override val z: String,
    ) : A(z)
}
