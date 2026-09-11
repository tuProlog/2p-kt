package it.unibo.tuprolog.ui.gui.prolog

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
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

internal fun Solution.toStep(
    queryText: String,
    signals: List<SolverSignal>,
    features: Map<FeatureId, Map<String, FeatureValue>>,
): ResolutionStep =
    when (this) {
        is Solution.Yes ->
            ResolutionStep.Yield(
                SolutionPresentation.Yes(
                    query = queryText,
                    bindings =
                        query.variables
                            .filterNot(Var::isAnonymous)
                            .mapNotNull { variable ->
                                valueOf(variable)?.let { BindingPresentation(variable.name, it.toString()) }
                            }.toList(),
                    solvedQuery = solvedQuery.toString(),
                ),
                hasMorePotentially = true,
                signals = signals,
                featureStateReplacements = features,
            )
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
                ),
                hasMorePotentially = false,
                signals = signals,
                featureStateReplacements = features,
            )
    }

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
