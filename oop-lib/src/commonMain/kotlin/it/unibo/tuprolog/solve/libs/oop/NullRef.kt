package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term

/**
 * The [ObjectRef] representing a `null` JVM/Kotlin value, i.e. the Prolog-side counterpart of
 * `null` -- obtainable as [ObjectRef.NULL].
 *
 * Since there is no actual object to reflect upon, every member inherited from [ObjectRef]
 * that would need one throws [NullPointerException], mirroring what dereferencing a `null`
 * reference does on the JVM. Use [it.unibo.tuprolog.solve.libs.oop.primitives.NullRef]
 * (the `null_ref/1` predicate) to test whether a term is this reference before invoking on it.
 *
 * @see ObjectRef.NULL
 */
interface NullRef : ObjectRef {
    /** @throws NullPointerException always, as there is no wrapped object. */
    override val `object`: Any
        get() = throw NullPointerException()

    /** @throws NullPointerException always, as there is no wrapped object to invoke members on. */
    override fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: List<Term>,
    ): Result = throw NullPointerException()

    /** @throws NullPointerException always, as there is no wrapped object to assign properties on. */
    override fun assign(
        objectConverter: TermToObjectConverter,
        propertyName: String,
        value: Term,
    ): Boolean = throw NullPointerException()
}
