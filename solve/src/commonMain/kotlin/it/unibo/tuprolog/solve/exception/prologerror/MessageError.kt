package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError

/**
 * The [MessageError] is used whenever no other [PrologError] instance is suitable for representing the error
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Giovanni
 */
class MessageError internal constructor( // TODO: 16/01/2020 test this class
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext,
    extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    /** The content of this message error */
    val content: Term by lazy { extraData ?: errorStruct }

    override fun updateContext(newContext: ExecutionContext): PrologError = // TODO: 21/01/2020 since PrologError already correctly implements updateContext for all of its subtypes, this is not needed
        MessageError(message, cause, newContext, extraData)

    companion object {

        /** The message error Struct functor */
        const val typeFunctor = ""

        /** Factory method to create a [MessageError] */
        fun of(content: Term, context: ExecutionContext, cause: Throwable? = null) =
            MessageError(content.toString(), cause, context, content)

    }
}