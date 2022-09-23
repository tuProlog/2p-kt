package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.rules.Alias

internal actual val platformSpecificAliases: Array<Alias>
    get() = arrayOf(
        Alias.forType("system", System::class),
        Alias.forType("math", Math::class),
        Alias.forObject("stdout", System.out),
        Alias.forObject("stderr", System.err),
        Alias.forObject("stdin", System.`in`)
    )
