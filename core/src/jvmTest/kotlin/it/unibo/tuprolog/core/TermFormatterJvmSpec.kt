package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import java.lang.reflect.Modifier

/** Factories of [TermFormatter] whose every parameter has a default, so Java can call them with no arguments. */
private val factoriesWithDefaults = listOf("default", "readable", "prettyVariables", "prettyExpressions")

/** [TermFormatter]'s factories, as Java (and JVM wrappers such as 2PPy) see them. */
class TermFormatterJvmSpec :
    FunSpec({
        withTests(nameFn = { "java-overloads/$it-without-arguments" }, factoriesWithDefaults) { name ->
            val factory = TermFormatter::class.java.getMethod(name)
            Modifier.isStatic(factory.modifiers).shouldBeTrue()
            factory.invoke(null).shouldBeInstanceOf<TermFormatter>()
        }
    })
