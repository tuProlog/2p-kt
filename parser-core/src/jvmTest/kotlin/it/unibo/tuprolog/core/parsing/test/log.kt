package it.unibo.tuprolog.core.parsing.test

actual fun internalsOf(x: () -> Any): String {
    return x().toString()
}

actual fun log(x: () -> Any) {
    System.err.println(x())
}