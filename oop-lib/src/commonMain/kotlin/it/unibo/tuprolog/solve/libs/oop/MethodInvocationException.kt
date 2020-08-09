package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.reflect.KClass

class MethodInvocationException : TuPrologException {
    constructor(ref: ObjectRef, missingMethodName: String) :
            super("Missing method `$missingMethodName` on term $ref referencing object ${ref.`object`}")

    constructor(type: KClass<*>, missingMethodName: String) :
            super("Missing method `$missingMethodName` on type ${type.fullName}")

    constructor(type: KClass<*>) :
            super("Missing companion object for type ${type.fullName}")

    constructor(type: KClass<*>, missingMethodName: String, admissibleTypes: List<Set<KClass<*>>>) :
            super("There is no method on type ${type.fullName} which is named `$missingMethodName` and accepts " +
                    admissibleTypes.map { it.joinToString("|") { t -> t.name } } +
                    " as formal arguments"
            )

    constructor(type: KClass<*>, admissibleTypes: List<Set<KClass<*>>>) :
            super("There is no constructor on type ${type.fullName} which accepts " +
                    admissibleTypes.map { it.joinToString("|") { t -> t.name } } +
                    " as formal arguments"
            )

    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}