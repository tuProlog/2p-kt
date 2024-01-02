@file:JvmName("FlagsUtils")

package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmName

fun <T : NotableFlag> flag(
    notableFlag: T,
    f: T.() -> Term,
): Pair<String, Term> = notableFlag.let { it.name to it.f() }

operator fun <T : NotableFlag> T.invoke(f: T.() -> Term): Pair<String, Term> = flag(this, f)
