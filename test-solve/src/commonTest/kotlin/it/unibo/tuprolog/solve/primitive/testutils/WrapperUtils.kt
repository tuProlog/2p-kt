package it.unibo.tuprolog.solve.primitive.testutils

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import kotlin.collections.List as KtList

/**
 * Utils singleton to help testing [AbstractWrapper] implementations
 *
 * @author Enrico
 */
internal object WrapperUtils {
    /** A named [struct] to be requested to a wrapper supporting [signature] */
    internal data class WrapperCase(
        val id: String,
        val signature: Signature,
        val struct: Struct,
    ) {
        /** Creates a request for [struct] by means of [requestCreator] */
        inline fun <Request> request(requestCreator: (Signature, KtList<Term>) -> Request): Request =
            requestCreator(struct.extractSignature(), struct.args)
    }

    private val ciao = Signature("ciao", 0)
    private val another = Signature("another", 1)
    private val aVarargOne = Signature("aVarargOne", 1, true)

    /** [Struct]s matching the signature they are requested to */
    internal val matchingCases by lazy {
        listOf(
            WrapperCase("ciao-0/same-name", ciao, Atom.of("ciao")),
            WrapperCase("another-1/anonymous-var-arg", another, Struct.of("another", Var.anonymous())),
            WrapperCase("another-1/true-arg", another, Struct.of("another", Truth.TRUE)),
            WrapperCase("vararg-one-1/var-arg", aVarargOne, Struct.of("aVarargOne", Var.of("X"))),
            WrapperCase(
                "vararg-one-1/integer-and-real-args",
                aVarargOne,
                Struct.of("aVarargOne", Integer.of(1), Real.of(1.5)),
            ),
            WrapperCase(
                "vararg-one-1/two-vars-and-true-args",
                aVarargOne,
                Struct.of("aVarargOne", Var.of("X"), Var.of("Y"), Truth.TRUE),
            ),
        )
    }

    /** [Struct]s not matching the signature they are requested to, which should therefore be rejected */
    internal val notMatchingCases by lazy {
        listOf(
            WrapperCase("ciao-0/different-case", ciao, Atom.of("ciaO")),
            WrapperCase("ciao-0/different-name", ciao, Atom.of("cc")),
            WrapperCase("another-1/no-args", another, Atom.of("another")),
            WrapperCase("another-1/two-args", another, Struct.of("another", Var.of("A"), Var.of("B"))),
            WrapperCase("another-1/different-name", another, Struct.of("other", Integer.of(2))),
            WrapperCase("vararg-one-1/no-args", aVarargOne, Atom.of("aVarargOne")),
            WrapperCase("vararg-one-1/different-name", aVarargOne, Struct.of("aVararg", Var.anonymous())),
        )
    }

    /** All cases under test */
    internal val allCases by lazy { matchingCases + notMatchingCases }

    /** Registers one leaf named `prefix/id` per case in [matchingCases] */
    internal fun FunSpecRootScope.withMatchingCases(
        prefix: String,
        test: suspend (WrapperCase) -> Unit,
    ) {
        withTests(nameFn = { "$prefix/${it.id}" }, matchingCases.filterNot { it.signature.vararg }) { test(it) }
        // Known bug: vararg signatures reject requests they should match (see the TODOs in `Signature`)
        matchingCases.filter { it.signature.vararg }.forEach { case ->
            xtest("$prefix/${case.id}") { test(case) }
        }
    }
}
