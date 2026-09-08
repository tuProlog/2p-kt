package it.unibo.tuprolog.solve

/**
 * Platform-specific tuning knob for [TestBigList], which builds and resolves a recursive list of [SIZE] elements to
 * catch performance regressions or stack-depth issues on deep/large resolutions.
 *
 * The `actual` value is provided per Kotlin Multiplatform target (see the `jvmMain` and `jsMain` source sets of this
 * module) since a size that comfortably exercises the JVM would be too slow, or blow the stack, on JS.
 */
expect object BigListOptions {
    /** The length of the list [TestBigList] builds and checks; smaller on JS than on the JVM. */
    val SIZE: Int
}
