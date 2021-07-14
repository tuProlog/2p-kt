package it.unibo.tuprolog.solve.libs.oop

object OverloadDetectorObject : OverloadDetector by OverloadDetectorImpl() {
    fun refresh(): OverloadDetectorObject {
        reset()
        return this
    }
}
