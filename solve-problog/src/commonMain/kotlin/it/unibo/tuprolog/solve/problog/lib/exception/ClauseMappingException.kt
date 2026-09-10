package it.unibo.tuprolog.solve.problog.lib.exception

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.jvm.JvmOverloads

/**
 * Thrown when `:solve-problog`'s internal clause-mapping logic -- the step that rewrites a ProbLog theory
 * (annotated disjunctions, evidence, plain Prolog clauses) into a Prolog-compliant one carrying explanation
 * terms -- receives a [it.unibo.tuprolog.core.Clause] it cannot handle, e.g. one of an unexpected subtype for
 * the mapper being applied, or malformed with respect to the mapper's expectations (a badly-formed annotated
 * disjunction, an evidence predicate with the wrong arity or argument type, and similar cases). It generally
 * signals a bug in the mapping pipeline itself, or a theory that superficially matched a mapper's predicate
 * but violates one of its structural preconditions.
 *
 * @param message a description of what went wrong during clause mapping.
 * @param cause the underlying [Throwable] that caused this exception, if any.
 */
open class ClauseMappingException
    @JvmOverloads
    constructor(
        override val message: String?,
        cause: Throwable? = null,
    ) : TuPrologException(cause)
