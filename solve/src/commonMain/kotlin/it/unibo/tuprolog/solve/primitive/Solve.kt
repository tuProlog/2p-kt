package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Durable
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.sideffects.SideEffectManager
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.ChronometerFactory
import kotlin.js.JsName
import kotlin.time.Duration

/** A base class for Solve requests and responses */
sealed class Solve {
    /** Class representing a Request to be full-filled by the Solver */
    data class Request<out C : ExecutionContext>(
        /** Signature of the goal to be solved in this [Request] */
        @JsName("signature")
        val signature: Signature,
        /** Arguments with which the goal is invoked in this [Request] */
        @JsName("arguments")
        val arguments: List<Term>,
        /** The context that's current at Request making */
        @JsName("context")
        val context: C,
    ) : Solve(),
        Durable {
        init {
            when {
                signature.vararg ->
                    require(arguments.count() >= signature.arity) {
                        "Trying to create Solve.Request of signature `$signature` " +
                            "with not enough arguments ${arguments.toList()}"
                    }
                else ->
                    require(arguments.count() == signature.arity) {
                        "Trying to create Solve.Request of signature `$signature` " +
                            "with wrong number of arguments ${arguments.toList()}"
                    }
            }
        }

        /** The chronometer of the resolution this request belongs to, i.e. its [context]'s. */
        override val chronometer: Chronometer
            get() = context.chronometer

        /** The current query [Struct] of this request */
        @JsName("query")
        val query: Struct by lazy { signature withArgs arguments }

        @JsName("unificator")
        val unificator: Unificator
            get() = context.unificator

        /** Creates a new [Response] to this Request */
        @JsName("replyWith")
        fun replyWith(
            substitution: Substitution,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) = when (substitution) {
            is Substitution.Unifier -> replySuccess(substitution, sideEffectManager, *sideEffects)
            else -> replyFail(sideEffectManager, *sideEffects)
        }

        /** Creates a new [Response] to this Request */
        @JsName("replyWithBuildingSideEffects")
        fun replyWith(
            substitution: Substitution,
            sideEffectManager: SideEffectManager? = null,
            buildSideEffects: SideEffectsBuilder.() -> Unit,
        ) = when (substitution) {
            is Substitution.Unifier -> replySuccess(substitution, sideEffectManager, buildSideEffects)
            else -> replyFail(sideEffectManager, buildSideEffects)
        }

        /** Creates a new [Response] to this Request */
        @JsName("replyWithSolution")
        fun replyWith(
            solution: Solution,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) = solution.whenIs(
            yes = { replySuccess(it.substitution, sideEffectManager, *sideEffects) },
            no = { replyFail(sideEffectManager, *sideEffects) },
            halt = { replyException(it.exception, sideEffectManager, *sideEffects) },
        )

        /** Creates a new [Response] to this Request */
        @JsName("replyWithSolutionBuildingSideEffects")
        fun replyWith(
            solution: Solution,
            sideEffectManager: SideEffectManager? = null,
            buildSideEffects: SideEffectsBuilder.() -> Unit,
        ) = solution.whenIs(
            yes = { replySuccess(it.substitution, sideEffectManager, buildSideEffects) },
            no = { replyFail(sideEffectManager, buildSideEffects) },
            halt = { replyException(it.exception, sideEffectManager, buildSideEffects) },
        )

        /** Creates a new successful or failed [Response] depending on [condition]; to be used when the substitution doesn't change */
        @JsName("replyWithCondition")
        fun replyWith(
            condition: Boolean,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) = if (condition) {
            replySuccess(Substitution.empty(), sideEffectManager, *sideEffects)
        } else {
            replyFail(sideEffectManager, *sideEffects)
        }

        /** Creates a new successful or failed [Response] depending on [condition]; to be used when the substitution doesn't change */
        @JsName("replyWithConditionBuildingSideEffects")
        fun replyWith(
            condition: Boolean,
            sideEffectManager: SideEffectManager? = null,
            buildSideEffects: SideEffectsBuilder.() -> Unit,
        ) = if (condition) {
            replySuccess(Substitution.empty(), sideEffectManager, buildSideEffects)
        } else {
            replyFail(sideEffectManager, buildSideEffects)
        }

        /** Creates a new successful [Response] to this Request, with substitution */
        @JsName("replySuccess")
        fun replySuccess(
            substitution: Substitution.Unifier = Substitution.empty(),
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) = Response(
            Solution.yes(query, substitution),
            context,
            sideEffectManager,
            *sideEffects,
        )

        /** Creates a new successful [Response] to this Request, with substitution */
        @JsName("replySuccessBuildingSideEffects")
        fun replySuccess(
            substitution: Substitution.Unifier = Substitution.empty(),
            sideEffectManager: SideEffectManager? = null,
            buildSideEffects: SideEffectsBuilder.() -> Unit,
        ) = Response(
            Solution.yes(query, substitution),
            context,
            sideEffectManager,
            SideEffectsBuilder.empty().also { it.buildSideEffects() }.build(),
        )

        /** Creates a new failed [Response] to this Request */
        @JsName("replyFail")
        fun replyFail(
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) = Response(
            Solution.no(query),
            context,
            sideEffectManager,
            *sideEffects,
        )

        /** Creates a new failed [Response] to this Request */
        @JsName("replyFailBuildingSideEffects")
        fun replyFail(
            sideEffectManager: SideEffectManager? = null,
            buildSideEffects: SideEffectsBuilder.() -> Unit,
        ) = Response(
            Solution.no(query),
            context,
            sideEffectManager,
            SideEffectsBuilder.empty().also { it.buildSideEffects() }.build(),
        )

        /** Creates a new halt [Response] to this Request, with cause exception */
        @JsName("replyException")
        fun replyException(
            exception: ResolutionException,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) = Response(
            Solution.halt(query, exception),
            context,
            sideEffectManager,
            *sideEffects,
        )

        /** Creates a new halt [Response] to this Request, with cause exception */
        @JsName("replyExceptionBuildingSideEffects")
        fun replyException(
            exception: ResolutionException,
            sideEffectManager: SideEffectManager? = null,
            buildSideEffects: SideEffectsBuilder.() -> Unit,
        ) = Response(
            Solution.halt(query, exception),
            context,
            sideEffectManager,
            SideEffectsBuilder.empty().also { it.buildSideEffects() }.build(),
        )

        /** Creates a new, independent [Solver] sharing this request's [context] state, for nested resolutions. */
        @JsName("subSolver")
        fun subSolver(): Solver = context.createSolver()

        /**
         * Solves [goal] in a nested resolution (via [subSolver]), timed by a child scope of this request's
         * [chronometer] (see [Chronometer.childScope]): on the same timeline, and never outliving this resolution's
         * deadline, nor `now + limit`. It counts into this resolution's [ExecutionContext.counters], if any.
         */
        @JsName("solve")
        fun solve(
            goal: Struct,
            limit: Duration = Duration.INFINITE,
        ): Sequence<Solution> =
            subSolver().solve(
                goal,
                SolveOptions
                    .allLazily()
                    .setTimeout(
                        limit,
                    ).setChronometerFactory(ChronometerFactory.childrenOf(chronometer))
                    .setCounters(context.counters),
            )
    }

    /** Class representing a Response, from the Solver, to a [Solve.Request] */
    data class Response(
        /** The solution attached to the response */
        @JsName("solution")
        val solution: Solution,
        /**
         * The context the response was produced in, i.e. its request's: a read-only view, valid as long as the
         * response is (see [ExecutionContext]).
         */
        @JsName("context")
        val context: ExecutionContext,
        /** The Prolog flow modification manager after request execution (use `null` in case nothing changed) */
        @JsName("sideEffectManager")
        val sideEffectManager: SideEffectManager? = null,
        /** The (possibly empty) [List] of [SideEffect]s to be applied to the execution context after a primitive has been
         * executed */
        @JsName("sideEffects")
        val sideEffects: List<SideEffect>,
    ) : Solve() {
        constructor(
            solution: Solution,
            context: ExecutionContext,
            sideEffectManager: SideEffectManager? = null,
            sideEffects: Iterable<SideEffect>,
        ) : this(solution, context, sideEffectManager, sideEffects as? List<SideEffect> ?: sideEffects.toList())

        constructor(
            solution: Solution,
            context: ExecutionContext,
            sideEffectManager: SideEffectManager? = null,
            sideEffects: Sequence<SideEffect>,
        ) : this(solution, context, sideEffectManager, sideEffects.asIterable())

        constructor(
            solution: Solution,
            context: ExecutionContext,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect,
        ) : this(solution, context, sideEffectManager, listOf(*sideEffects))
    }
}
