package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.TypeTester
import kotlin.reflect.KClass

object TypeRef : TypeTester<ExecutionContext>("type_ref") {
    override fun testType(term: Term): Boolean = term.asObjectRef()?.let { it.value is KClass<*> } ?: false
}
