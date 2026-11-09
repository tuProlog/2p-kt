package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.theory.MutableTheory

/** Primitives get a read-only view of `:solve-prolog`'s context (#1010). */
class ReadOnlyViewsSpec :
    FunSpec({
        val leaked = Fact.of(Atom.of("leaked"))

        // runs [query] with a `probe/0` primitive that hands [use] its request, then rethrows what [use] threw (the
        // engine would turn it into a halt)
        fun probe(
            query: String,
            use: (Solve.Request<ExecutionContext>) -> Unit,
        ): List<Boolean> {
            var failure: Throwable? = null
            val primitive =
                Primitive { request ->
                    runCatching { use(request) }.onFailure { failure = it }
                    sequenceOf(request.replySuccess())
                }
            val library = Library.of("test.views", primitives = mapOf(Signature("probe", 0) to primitive))
            val solver = PrologSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(library))
            val answers = solver.solve(query.parseAsStruct()).map { it.isYes }.toList()
            failure?.let { throw it }
            return answers
        }

        test("views/knowledge-bases-are-not-mutable") {
            probe("probe") { request ->
                (request.context.dynamicKb is MutableTheory) shouldBe false
                (request.context.staticKb is MutableTheory) shouldBe false
            }
        }

        test("views/writes-on-knowledge-bases-make-copies") {
            probe("probe, leaked") { request ->
                request.context.dynamicKb
                    .assertZ(leaked)
                    .contains(leaked) shouldBe true
                request.context.dynamicKb
                    .toMutableTheory()
                    .assertZ(leaked)
            } shouldBe listOf(false)
        }

        test("views/derived-contexts-leave-the-engine-untouched") {
            probe("probe, leaked") { request ->
                request.context
                    .apply(SideEffect.AddDynamicClauses(leaked))
                    .dynamicKb
                    .contains(leaked) shouldBe true
            } shouldBe listOf(false)
        }

        test("views/sub-solvers-share-the-dynamic-knowledge-base") {
            // as before the view: a nested resolution's assertions are seen by the outer one
            probe("findall(_, assertz(leaked), _), leaked") { } shouldBe listOf(true)
        }

        test("views/response-carries-the-request-context") {
            probe("probe") { request ->
                request.replySuccess().context shouldBeSameInstanceAs request.context
                request.replyFail().context shouldBeSameInstanceAs request.context
            }
        }
    })
