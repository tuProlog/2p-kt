package it.unibo.tuprolog.core.parsing.test

actual fun internalsOf(x: () -> Any): String {
    return JSON.stringify(x())
}

actual fun log(x: () -> Any) {
    console.log(x())
}