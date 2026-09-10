package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solve.solverFactoryProfile
import it.unibo.tuprolog.ui.gui.template.ClassicTheoryTemplates
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun main() {
    val scope = MainScope()
    // Solver.prolog resolves ClassicSolverFactory via a runtime `require("2p-solve-classic")` by string module
    // name (see solve/src/jsMain/.../SolverExtensionsJs.kt), which only works when Kotlin/JS modules are
    // resolved by Node at runtime. Webpack bundles ide-web ahead of time instead, so that lookup fails at
    // startup; importing the factory directly (ide-web already depends on :solve-classic) sidesteps it.
    val profile = solverFactoryProfile(ClassicSolverFactory, SolverProfileId("prolog"), "Prolog")
    val application =
        buildGuiApplication(scope) {
            solverProfile(profile, makeDefault = true)
        }
    val view = WebIdeView(application.controller, scope, ClassicTheoryTemplates.ALL)
    val effects = WebIdeEffectHandler(application.controller, scope)

    scope.launch {
        application.controller.state.collectLatest(view::render)
    }
    scope.launch {
        application.controller.effects.collectLatest(effects::handle)
    }
    scope.launch {
        application.controller.dispatch(ApplicationAction.Start)
        application.controller.dispatch(WorkspaceAction.NewDocumentPage())
    }
}
