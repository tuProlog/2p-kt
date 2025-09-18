package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Atom

enum class IOMode {
    READ,
    WRITE,
    APPEND,
    ;

    companion object {
        val atomValues =
            values()
                .asSequence()
                .map { it.name.lowercase() }
                .map(Atom.Companion::of)
                .toSet()
    }
}
