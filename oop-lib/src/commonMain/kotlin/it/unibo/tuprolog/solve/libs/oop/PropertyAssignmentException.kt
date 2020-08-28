package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.reflect.KClass

class PropertyAssignmentException : TuPrologException {

    constructor(type: KClass<*>, missingPropertyName: String, admissibleTypes: Set<KClass<*>>) :
            super("There is no property on type ${type.fullName} which is named `$missingPropertyName` and can be " +
                    "assigned to a value of type " + admissibleTypes.joinToString("|") { it.name }
            )

    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}