@file:JvmName("Aliases")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.jvm.JvmName

internal expect val platformSpecificAliases: Array<Alias>

internal val platformAgnosticAliases: Array<Alias> =
    arrayOf(
        Alias.forType("string", String::class),
        Alias.forType("array", Array::class),
        Alias.forType("list", List::class),
        Alias.forType("arraylist", ArrayList::class),
        Alias.forType("map", Map::class),
        Alias.forType("hashmap", LinkedHashMap::class),
        Alias.forType("int", Int::class),
        Alias.forType("integer", Int::class),
        Alias.forType("double", Double::class),
        Alias.forType("float", Float::class),
        Alias.forType("long", Long::class),
        Alias.forType("short", Short::class),
        Alias.forType("byte", Byte::class),
        Alias.forType("char", Char::class),
        Alias.forType("bool", Boolean::class),
        Alias.forType("boolean", Boolean::class),
        Alias.forType("any", Any::class),
        Alias.forType("nothing", Nothing::class),
        Alias.forType("big_integer", BigInteger::class),
        Alias.forType("big_decimal", BigDecimal::class),
    )

val defaultAliases: Array<Alias> by lazy {
    arrayOf(*platformAgnosticAliases, *platformSpecificAliases)
}
