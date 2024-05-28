package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ExistenceError

@Suppress("unused")
class NoSuchTypeException : OopException {
    constructor(typeName: String) : super(message(typeName)) {
        missingTypeName = typeName
    }
    constructor(typeName: String, cause: Throwable) : super(message(typeName), cause) {
        missingTypeName = typeName
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val missingTypeName: String

    companion object {
        private fun message(typeName: String) = "Missing type $typeName on platform ${Info.PLATFORM.name}"
    }

    override fun toLogicError(context: ExecutionContext, signature: Signature): LogicError {
        return ExistenceError.of(
            context,
            ExistenceError.ObjectType.OOP_TYPE,
            culprit,
            message ?: "",
        )
    }

    override val culprit: Term
        get() = Atom.Companion.of(missingTypeName)
}
