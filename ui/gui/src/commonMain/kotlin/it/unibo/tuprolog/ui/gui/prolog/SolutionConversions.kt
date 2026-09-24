package it.unibo.tuprolog.ui.gui.prolog

import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverSignal

/**
 * Converts this [Solution] into a toolkit-neutral [ResolutionStep.Yield], formatting the solved query and every
 * variable binding with [TermFormatter.prettyExpressions] (using [operators] and pretty variable names) rather
 * than each [it.unibo.tuprolog.core.Term]'s raw `toString()`.
 *
 * When [groundQueriesHaveBooleanSolution] is `true` and [query] is ground (no variables at all), the resulting
 * [SolutionPresentation.Yes.solvedQuery] is left `null` -- a frontend renders that as a bare `yes.`, since the
 * solved query would otherwise just repeat [query] verbatim.
 */
internal fun Solution.toStep(
    queryText: String,
    signals: List<SolverSignal>,
    features: Map<FeatureId, Map<String, FeatureValue>>,
    operators: OperatorSet,
    groundQueriesHaveBooleanSolution: Boolean = false,
): ResolutionStep =
    when (this) {
        is Solution.Yes -> {
            // Pretty expressions (e.g. `1 + 2` rather than `+(1, 2)`), using the solver's own operators so a
            // custom infix/prefix/postfix operator renders the same way here as it would when parsed back in;
            // pretty variables so repeated occurrences of the same variable within one solution stay
            // recognizable (e.g. `X` rather than `_G123`).
            val formatter = TermFormatter.prettyExpressions(operators)
            ResolutionStep.Yield(
                SolutionPresentation.Yes(
                    query = queryText,
                    bindings =
                        query.variables
                            .filterNot(Var::isAnonymous)
                            // A term contains one Var occurrence per syntactic mention, not per distinct
                            // variable (see Variabled.variables); without this, a query mentioning the same
                            // variable twice (e.g. "member(X, [1,2,3]), Y is X + Z.") would show X twice.
                            .distinct()
                            .mapNotNull { variable ->
                                valueOf(variable)?.let { BindingPresentation(variable.name, formatter.format(it)) }
                            }.toList(),
                    solvedQuery =
                        if (groundQueriesHaveBooleanSolution && query.variables.none()) {
                            null
                        } else {
                            formatter.format(solvedQuery)
                        },
                    metadata = features.toSolutionMetadata(),
                ),
                hasMorePotentially = true,
                signals = signals,
                featureStateReplacements = features,
            )
        }
        is Solution.No ->
            ResolutionStep.Yield(
                SolutionPresentation.No(queryText),
                hasMorePotentially = false,
                signals = signals,
                featureStateReplacements = features,
            )
        is Solution.Halt ->
            ResolutionStep.Yield(
                SolutionPresentation.Halt(
                    queryText,
                    exception.message ?: "Resolution halted",
                    exception.logicStackTrace.map {
                        it.toString()
                    },
                    isTimeout = exception is TimeOutException,
                ),
                hasMorePotentially = false,
                signals = signals,
                featureStateReplacements = features,
            )
    }

/**
 * Flattens numeric extension feature values (e.g. PLP's per-solution probability) into a solution-scoped,
 * toolkit-neutral `Map<String, String>` that survives into history (unlike [FeatureId]-keyed page feature
 * state, which only ever holds the *latest* solution's values) - so a frontend's solution list/tree can show
 * it next to every past solution, not just the current one. Only [FeatureValue.Number] is flattened: other
 * kinds (e.g. a BDD's DOT text) are already shown via their own dedicated feature tab and would otherwise be
 * needlessly duplicated into every historical solution's metadata.
 */
private fun Map<FeatureId, Map<String, FeatureValue>>.toSolutionMetadata(): Map<String, String> =
    values
        .asSequence()
        .flatMap { it.entries.asSequence() }
        .mapNotNull { (key, value) -> (value as? FeatureValue.Number)?.let { key to it.value.toString() } }
        .toMap()

/** Captures this [Solver]'s current operators, flags, libraries, and knowledge base as a toolkit-neutral snapshot. */
internal fun Solver.inspectionSnapshot(): SolverInspectionSnapshot =
    SolverInspectionSnapshot(
        operators = operators.map { OperatorPresentation(it.functor, it.priority, it.specifier.name) },
        flags = flags.entries.map { FlagPresentation(it.key, it.value.toString()) }.sortedBy { it.name },
        libraries =
            libraries.libraries
                .map { library ->
                    LibraryPresentation(
                        alias = library.alias,
                        predicates =
                            (library.primitives.keys.asSequence() + library.rulesSignatures)
                                .map(Signature::format)
                                .distinct()
                                .sorted()
                                .toList(),
                        operators =
                            library.operators.map {
                                OperatorPresentation(
                                    it.functor,
                                    it.priority,
                                    it.specifier.name,
                                )
                            },
                        functions =
                            library.functions.keys
                                .map(Signature::format)
                                .sorted(),
                    )
                }.sortedBy { it.alias },
        staticKnowledgeBase = staticKb.joinToString("\n") { "$it." },
        dynamicKnowledgeBase = dynamicKb.joinToString("\n") { "$it." },
    )

private fun Signature.format(): String = "$name/$arity${if (vararg) "+" else ""}"
