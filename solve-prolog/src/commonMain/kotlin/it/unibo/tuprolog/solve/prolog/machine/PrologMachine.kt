package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.warning.MissingPredicate
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.intercept.Decision
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.ReadOnlyExecutionContext
import it.unibo.tuprolog.solve.prolog.fsm.MatchedRule
import it.unibo.tuprolog.solve.prolog.fsm.isControlConstruct
import it.unibo.tuprolog.solve.prolog.fsm.unfoldGoals
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The stateful resolution engine of #1010 (step 3): one per resolution, driven as an [Iterator] of [Solution]s.
 *
 * Control state is mutable and owned by the machine: a continuation of goals still to run (an immutable linked list
 * shared by choice points, so backtracking restores it by pointer), a stack of choice points, and [Bindings] with a
 * trail. Each call gets a [Frame] recording what cut, `catch/3`, the logic stack trace and the interceptor need. The
 * solver's world state (knowledge bases, flags, libraries, channels) is a [PrologExecutionContext] that changes only
 * through side effects, and is reported to the solver through `onWorld`.
 *
 * Semantics follow the FSM exactly, including its lookahead (a call's next alternative is pulled when the call starts
 * or is retried) and its last-call optimisation of self-recursive tail calls, so that both engines give the same
 * answers until PR 7 makes alternatives lazy.
 */
internal class PrologMachine(
    root: PrologExecutionContext,
    private val query: Struct,
    private val onWorld: (PrologExecutionContext) -> Unit,
) : Iterator<Solution> {
    private class Frame(
        val procedure: Struct?,
        val parent: Frame?,
        val depth: Int,
        // choice points below this index existed before the call: a cut in the call keeps them
        val cutBarrier: Int,
        val trailMark: Int,
        // what runs after the call, i.e. after a catch/3's recovery
        val after: Cont?,
        val invocation: Long,
        // whether [procedure] was resolved when called (primitives); otherwise its bindings are in [bindings]
        val resolved: Boolean = true,
    ) {
        val transparent: Boolean = procedure != null && procedure.arity == 2 && procedure.functor in TRANSPARENT

        val isCatch: Boolean = procedure != null && procedure.arity == 3 && procedure.functor == "catch"
    }

    // a goal to run in a frame, or the end of the frame (goal == null), then the rest
    private class Cont(
        val goal: Term?,
        val frame: Frame,
        val next: Cont?,
    )

    private class Choice(
        val alternatives: Iterator<Any>,
        val isRules: Boolean,
        val frame: Frame,
        val cont: Cont?,
        val trailMark: Int,
    )

    private enum class Location { SELECT, BACKTRACK }

    private var world: PrologExecutionContext = root.copy(customData = root.customData.preservePersistent())
    private val bindings = Bindings()
    private val choices = ArrayList<Choice>()
    private val rootFrame = Frame(null, null, 0, 0, 0, null, 0)
    private var cont: Cont? = goals(query, rootFrame, null)
    private var location = Location.SELECT
    private var finished = false
    private var pending: Solution? = null

    // each answer gets the whole time budget, as in the FSM: the clock restarts when the search resumes
    private var resumed = false

    // the goal being called, as written (before resolving its bindings)
    private var selectedGoal: Term? = null

    private val unificator: Unificator
        get() = world.unificator

    init {
        onWorld(world)
    }

    override fun hasNext(): Boolean {
        if (pending == null && !finished) pending = run()
        return pending != null
    }

    override fun next(): Solution {
        if (!hasNext()) throw NoSuchElementException()
        return pending!!.also { pending = null }
    }

    private fun run(): Solution? {
        if (resumed) {
            world.chronometer.restart()
            resumed = false
        }
        while (true) {
            if (world.chronometer.leap()) return timeOut()
            val solution =
                when (location) {
                    Location.SELECT -> select()
                    Location.BACKTRACK -> backtrack()
                }
            if (solution != null) return solution
        }
    }

    private fun timeOut(): Solution =
        halt(TimeOutException(exceededDuration = world.maxDuration, context = context(rootFrame)))

    private fun halt(error: ResolutionException): Solution {
        finished = true
        return Solution.halt(query, error)
    }

    private fun select(): Solution? {
        val current = cont ?: return answer()
        val goal = current.goal
        if (goal == null) {
            cont = current.next
            return exit(current.frame)
        }
        val selected = selectGoal(current)
        cont = selected.next
        selectedGoal = selected.goal
        return call(bindings.deref(selected.goal!!), selected.frame, selected.next)
    }

    private fun answer(): Solution {
        if (world.chronometer.checkNow()) return timeOut()
        if (choices.isEmpty()) finished = true else location = Location.BACKTRACK
        resumed = true
        return Solution.yes(query, querySubstitution())
    }

    // the EXIT port of [frame]'s call, if traced
    private fun exit(frame: Frame): Solution? =
        when (val decision = world.interception?.exit(frame.invocation, ::bound) { view(frame) }) {
            is Decision.Fail -> null.also { location = Location.BACKTRACK }
            is Decision.Halt -> haltOn(decision, frame)
            else -> null
        }

    private fun bound(goal: Struct): Struct = bindings.resolve(goal).castToStruct()

    // the goal the policy chooses among the frame's goals, moved to the front of the continuation
    private fun selectGoal(current: Cont): Cont {
        val policy = world.goalSelection ?: return current
        val frame = current.frame
        val run = generateSequence(current) { it.next }.takeWhile { it.frame === frame && it.goal != null }.toList()
        if (run.size < 2) return current
        val index = policy.select(run.map { bindings.resolve(it.goal!!) }, view(frame, pinned = false))
        require(index in run.indices) { "Goal index $index out of ${run.indices}" }
        if (index == 0) return current
        var rest = run.last().next
        for (i in run.indices.reversed()) if (i != index) rest = Cont(run[i].goal, frame, rest)
        return Cont(run[index].goal, frame, rest)
    }

    private fun call(
        goal: Term,
        frame: Frame,
        rest: Cont?,
    ): Solution? {
        if (goal is Var) {
            val procedure = frame.procedure?.extractSignature() ?: QUERY
            return raise(InstantiationError.forGoal(context(frame), procedure, goal), frame)
        }
        if (goal !is Struct) {
            val procedure = frame.procedure?.extractSignature() ?: QUERY
            return raise(TypeError.forGoal(context(frame), procedure, TypeError.Expected.CALLABLE, goal), frame)
        }
        val signature = goal.extractSignature()
        return when {
            world.libraries.hasPrimitive(signature) -> callPrimitive(bound(goal), signature, frame, rest)
            goal.isTruth -> null.also { if (goal.isTrue) cont = rest else location = Location.BACKTRACK }
            goal.isAtom && goal.castToAtom().value == "!" -> {
                cut(if (goal is MagicCut) frame else generateSequence(frame) { it.parent }.first { !it.transparent })
                cont = rest
                null
            }
            else -> callRules(goal, frame, rest)
        }
    }

    private fun cut(scope: Frame) {
        while (choices.size > scope.cutBarrier) choices.removeAt(choices.lastIndex)
    }

    // the CALL port of [goal], if traced: null to go on with the call
    private fun intercept(
        goal: Struct,
        invocation: Long,
        frame: Frame,
        rest: Cont?,
    ): Solution? {
        val interception = world.interception
        if (interception == null || goal.isControlConstruct) return null
        return when (val decision = interception.call(invocation, bound(goal), frame.depth + 1) { view(frame) }) {
            is Decision.Continue -> null
            is Decision.Fail -> REFUSED.also { location = Location.BACKTRACK }
            is Decision.Redirect -> REFUSED.also { cont = Cont(decision.goal, frame, rest) }
            is Decision.Halt -> haltOn(decision, frame)
        }
    }

    private fun callPrimitive(
        goal: Struct,
        signature: Signature,
        frame: Frame,
        rest: Cont?,
    ): Solution? {
        val invocation = world.interception?.nextId() ?: 0
        intercept(goal, invocation, frame, rest)?.let { return it.orNull() }
        if (!goal.isControlConstruct) world.counters?.countInference(frame.depth + 1)
        val callee = Frame(goal, frame, frame.depth + 1, choices.size, bindings.mark, rest, invocation)
        val primitive = world.libraries.primitives[signature] ?: error("Inconsistent primitive lookup for $signature")
        return try {
            val request =
                Solve.Request(
                    signature,
                    goal.args,
                    ReadOnlyExecutionContext(context(callee, rest, pinned = false)),
                )
            val responses = primitive.solve(request).iterator()
            if (!responses.hasNext()) return null.also { location = Location.BACKTRACK }
            val first = responses.next()
            if (responses.hasNext()) push(Choice(responses, false, callee, rest, bindings.mark))
            respond(first, callee, rest)
        } catch (error: ResolutionException) {
            raise(error.updateLastContext(context(callee.skipThrow())), callee)
        }
    }

    private fun Frame.skipThrow(): Frame =
        generateSequence(this) { it.parent }.first { it.procedure?.functor != "throw" }

    private fun respond(
        response: Solve.Response,
        callee: Frame,
        rest: Cont?,
    ): Solution? {
        if (!callee.procedure.isControlConstruct) world.counters?.countAlternative()
        if (response.sideEffects.isNotEmpty()) {
            world = world.apply(response.sideEffects)
            onWorld(world)
        }
        val solution = response.solution
        return when {
            solution is Solution.Yes -> {
                if (!bindings.bindAll(solution.substitution, unificator)) {
                    location = Location.BACKTRACK
                    return null
                }
                cont = rest
                exit(callee)
            }
            solution is Solution.Halt ->
                raise(solution.exception.updateLastContext(context(callee.skipThrow())), callee)
            else -> null.also { location = Location.BACKTRACK }
        }
    }

    private fun push(choice: Choice) {
        choices.add(choice)
        if (!choice.frame.procedure.isControlConstruct) world.counters?.countChoicePoint(open = choices.size)
    }

    private fun callRules(
        goal: Struct,
        frame: Frame,
        rest: Cont?,
    ): Solution? {
        val invocation = world.interception?.nextId() ?: 0
        intercept(goal, invocation, frame, rest)?.let { return it.orNull() }
        if (!goal.isControlConstruct) world.counters?.countInference(frame.depth + 1)
        // an incremental unificator unifies the goal as is, under the bindings; any other one needs it resolved
        val incremental = unificator.incremental != null
        val called = if (incremental) goal else bound(goal)
        val probe = indexProbe(called)
        val sources = sequenceOf(world.libraries.asTheory(unificator), world.staticKb, world.dynamicKb)
        val candidates = sources.flatMap { it.selectClauses(called, probe) }
        val ordered = world.ruleSelection?.order(bound(goal), candidates, view(frame, pinned = false)) ?: candidates
        val alternatives = ordered.iterator()
        if (!alternatives.hasNext()) return missingProcedure(sources, goal.extractSignature(), frame)
        val tail = isTailCall(goal, frame, rest)
        val callee =
            Frame(
                called,
                if (tail) frame.parent else frame,
                frame.depth + 1,
                choices.size,
                bindings.mark,
                rest,
                invocation,
                resolved = !incremental,
            )
        val first = alternatives.next()
        if (alternatives.hasNext()) push(Choice(alternatives, true, callee, rest, bindings.mark))
        return execute(first, callee, rest)
    }

    // the FSM's last-call optimisation: a self-recursive call that ends its caller's body drops the caller's frame
    private fun isTailCall(
        goal: Struct,
        frame: Frame,
        rest: Cont?,
    ): Boolean {
        val procedure = frame.procedure ?: return false
        return rest?.frame !== frame &&
            world.interception == null &&
            world.flags[LastCallOptimization] == LastCallOptimization.ON &&
            procedure.functor == goal.functor &&
            procedure.arity == goal.arity &&
            !frame.isCatch
    }

    private fun execute(
        alternative: Rule,
        callee: Frame,
        rest: Cont?,
    ): Solution? {
        if (!callee.procedure.isControlConstruct) world.counters?.countAlternative()
        val rule = if (alternative is MatchedRule) alternative.rule else alternative
        val unified =
            if (alternative is MatchedRule) {
                bindings.bindAll(alternative.mgu, unificator)
            } else {
                bindings.unify(Rule.of(callee.procedure!!, rule.body), rule, unificator)
            }
        if (!unified) {
            location = Location.BACKTRACK
            return null
        }
        val body = prepare(rule.body)
        val next = if (world.interception != null) Cont(null, callee, rest) else rest
        cont = goals(body, callee, next)
        return null
    }

    private fun missingProcedure(
        sources: Sequence<Theory>,
        missing: Signature,
        frame: Frame,
    ): Solution? {
        val unknown = world.flags[Unknown]
        if (unknown != Unknown.FAIL && sources.none { missing.toIndicator() in it }) {
            when (unknown) {
                Unknown.ERROR -> return raise(ExistenceError.forProcedure(context(frame), missing), frame)
                Unknown.WARNING -> world.warnings.write(MissingPredicate(context = context(frame), signature = missing))
            }
        }
        location = Location.BACKTRACK
        return null
    }

    private fun backtrack(): Solution? {
        while (true) {
            val choice = choices.lastOrNull()
            if (choice == null) {
                (world.interception?.backtrack(null) { view(rootFrame) } as? Decision.Halt)?.let {
                    return haltOn(it, rootFrame)
                }
                finished = true
                return Solution.no(query)
            }
            when (val decision = world.interception?.backtrack(choice.frame.invocation) { view(choice.frame) }) {
                is Decision.Fail -> {
                    choices.removeAt(choices.lastIndex)
                    continue
                }
                is Decision.Halt -> return haltOn(decision, choice.frame)
                else -> Unit
            }
            bindings.undo(choice.trailMark)
            if (world.customData.ephemeral.isNotEmpty()) {
                world = world.copy(customData = world.customData.discardEphemeral())
            }
            location = Location.SELECT
            return try {
                val alternative = choice.alternatives.next()
                if (!choice.alternatives.hasNext()) choices.removeAt(choices.lastIndex)
                if (choice.isRules) {
                    execute(alternative as Rule, choice.frame, choice.cont)
                } else {
                    respond(alternative as Solve.Response, choice.frame, choice.cont)
                }
            } catch (error: ResolutionException) {
                raise(error.updateLastContext(context(choice.frame.skipThrow())), choice.frame)
            }
        }
    }

    // an exception raised in [frame]: recovered by the innermost catch/3 whose catcher unifies, or ending the resolution
    private fun raise(
        error: ResolutionException,
        frame: Frame,
    ): Solution? {
        if (error is LogicError) {
            val ball = if (error is MessageError) error.content else error.errorStruct
            for (catcher in generateSequence(frame) { it.parent }.filter { it.isCatch }) {
                bindings.undo(catcher.trailMark)
                cut(catcher)
                val catchGoal = catcher.called!!
                val mgu = unificator.mgu(catchGoal[1], ball)
                if (mgu is Substitution.Unifier) {
                    world.interception?.exception(error, catcher.invocation) { view(catcher) }?.let {
                        return haltOn(it, catcher)
                    }
                    bindings.bindAll(mgu, unificator)
                    cont = goals(bindings.resolve(catchGoal[2]), catcher.parent!!, catcher.after)
                    location = Location.SELECT
                    return null
                }
            }
        }
        val public = if (error is MessageError) SystemError.forUncaughtError(error) else error
        world.interception?.exception(public, null) { view(frame) }?.let { return haltOn(it, frame) }
        return halt(public)
    }

    private fun haltOn(
        decision: Decision.Halt,
        frame: Frame,
    ): Solution =
        halt(
            decision.error ?: it.unibo.tuprolog.solve.exception
                .HaltException(context = context(frame)),
        )

    /**
     * [body] ready to run, as `prepareForExecution` with the current bindings would make it, without resolving them:
     * goals that are bound variables become their values, unbound ones call/1 (as the FSM does).
     */
    private fun prepare(body: Term): Term =
        when {
            body is Var ->
                when (val value = bindings.deref(body)) {
                    is Var -> Struct.of("call", value)
                    else -> prepare(value)
                }
            body is Struct && body.arity == 2 && body.functor in TRANSPARENT -> {
                val first = prepare(body[0])
                val second = prepare(body[1])
                if (first === body[0] && second === body[1]) body else Struct.of(body.functor, first, second)
            }
            else -> body
        }

    // [goal] with its arguments dereferenced one level, so that clause indexes see their principal symbols
    private fun indexProbe(goal: Struct): Rule {
        val args = goal.args
        val probe =
            if (args.none { it is Var && bindings[it] != null }) {
                goal
            } else {
                Struct.of(
                    goal.functor,
                    args.map(bindings::deref),
                )
            }
        return Rule.of(probe, Var.anonymous())
    }

    private fun goals(
        body: Term,
        frame: Frame,
        next: Cont?,
    ): Cont? {
        var result = next
        val goals = body.unfoldGoals().toList()
        for (i in goals.indices.reversed()) {
            val goal = goals[i]
            result = Cont(if (goal is Var) Struct.of("call", goal) else goal, frame, result)
        }
        return result
    }

    private fun querySubstitution(): Substitution.Unifier =
        Substitution.of(
            query.variables
                .distinct()
                .associateWith { bindings.resolve(it) }
                .filter { (k, v) -> k != v },
        )

    // the bindings of the variables of the query, of the calls in progress, and of the goals left, as the FSM shows
    // them; computed only if a primitive reads them
    private fun substitution(
        frame: Frame,
        goal: Term?,
        rest: Cont?,
    ): Substitution.Unifier =
        bindings.substitutionOf(
            query.variables +
                generateSequence(frame) { it.parent }.flatMap { it.procedure?.variables ?: emptySequence() } +
                (goal?.variables ?: emptySequence()) +
                generateSequence(rest) { it.next }.flatMap { it.goal?.variables ?: emptySequence() },
        )

    // pinned unless read right away: the bindings it shows may change before it is read
    private fun context(
        frame: Frame,
        rest: Cont? = null,
        pinned: Boolean = true,
    ): ExecutionContext {
        val goal = selectedGoal
        val context =
            CallContext(world, frame.called, { substitution(frame, goal, rest) }) {
                generateSequence(frame) { it.parent }.map { it.called ?: Struct.of("?-", query) }.toList()
            }
        return if (pinned) context.pin() else context
    }

    // the frame's procedure, resolved
    private val Frame.called: Struct?
        get() = if (resolved) procedure else procedure?.let(::bound)

    private fun view(
        frame: Frame,
        pinned: Boolean = true,
    ): ExecutionContext = ReadOnlyExecutionContext(context(frame, pinned = pinned))

    /**
     * The rules of this theory whose head unifies with [goal], renamed apart: candidates come from the indexes, and are
     * renamed and unified one at a time, as they are pulled (as in the FSM), always under the bindings of the call.
     * With an incremental unificator, each is unified in the bindings, then undone, and unified again by [execute];
     * otherwise, each comes with its mgu.
     */
    private fun Theory.selectClauses(
        goal: Struct,
        probe: Rule,
    ): Sequence<Rule> =
        candidates(probe).mapNotNull { candidate ->
            val rule = candidate.freshCopy().asRule() ?: return@mapNotNull null
            // the whole clause is unified, so that a unificator sees the tags of the clause itself too
            val clause = Rule.of(goal, rule.body)
            if (unificator.incremental != null) {
                val mark = bindings.mark
                rule.takeIf { bindings.unify(clause, rule, unificator) }.also { bindings.undo(mark) }
            } else {
                val mgu = unificator.mgu(clause, rule)
                if (mgu.isSuccess) MatchedRule(rule, mgu.castToUnifier()) else null
            }
        }

    private fun Solution?.orNull(): Solution? = if (this === REFUSED) null else this

    private companion object {
        val TRANSPARENT = setOf(",", ";", "->")

        val QUERY = Signature("?-", 1)

        // a CALL decision that replaced the call without producing a solution
        val REFUSED: Solution = Solution.no(Struct.of("refused"))
    }
}
