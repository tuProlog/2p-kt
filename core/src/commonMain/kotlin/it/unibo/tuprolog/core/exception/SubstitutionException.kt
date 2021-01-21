package it.unibo.tuprolog.core.exception

import it.unibo.tuprolog.core.Substitution
import kotlin.jvm.JvmOverloads

open class SubstitutionException : TuPrologException {

    val substitution: Substitution

    @JvmOverloads
    constructor(substitution: Substitution, message: String?, cause: Throwable? = null) :
        super(message, cause) {
            this.substitution = substitution
        }

    constructor(substitution: Substitution, cause: Throwable? = null) :
        this(substitution, prettyMessage(substitution), cause)

    @JvmOverloads
    constructor(cause: Throwable? = null) : this(Substitution.failed(), cause)

    companion object {
        private fun prettyMessage(substitution: Substitution): String =
            when (substitution) {
                is Substitution.Fail -> "A unifier was expected, while a failed substitution was provided instead"
                else -> "A unifier was expected, while $substitution was provided instead"
            }
    }
}
