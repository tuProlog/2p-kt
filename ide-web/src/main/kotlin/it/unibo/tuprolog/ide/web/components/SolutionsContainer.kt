package it.unibo.tuprolog.ide.web.components

import AppState
import csstype.pct
import csstype.vh
import emotion.react.css
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.List
import mui.material.Tab
import mui.material.Tabs
import react.FC
import react.Props
import react.ReactNode
import react.redux.useSelector
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.libs.io.IOLib
import mui.lab.TreeItem
import mui.lab.TreeView
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.summary
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.tr
import react.dom.html.ReactHTML.ul
import react.useState
import web.html.HTML.details

val SolutionsContainer = FC<Props> {

    val solutions =
        useSelector<AppState, Collection<TuPrologSolution>> { s -> s.tuProlog.solutions }
    val eContext =
        useSelector<AppState, ExecutionContextAware?> { s -> s.tuProlog.executionContext }
    val outputs = useSelector<AppState, String> { s -> s.tuProlog.stdOutMessage }
    val errors = useSelector<AppState, String> {s -> s.tuProlog.stdErrMessage }
    val warnings = useSelector<AppState, Warning?> { s -> s.tuProlog.warningMessage }

    var activeTab by useState("solutionsTab")

    TabContext {
        value = activeTab
        Tabs {
            value = activeTab
            onChange = { _, newValue -> activeTab = newValue }

            Tab {
                value = "solutionsTab"
                label = ReactNode("Solutions")
            }
            Tab {
                value = "stdInTab"
                label = ReactNode("Standard In")
            }
            Tab {
                value = "stdOutTab"
                label = ReactNode("Standard Out")
            }
            Tab {
                value = "stdErr"
                label = ReactNode("Standard Error")
            }
            Tab {
                value = "warnings"
                label = ReactNode("Warnings")
            }
            Tab {
                value = "operators"
                label = ReactNode("Operators")
            }
            Tab {
                value = "libraries"
                label = ReactNode("Libraries")
            }
            Tab {
                value = "staticKb"
                label = ReactNode("Static KB")
            }
            Tab {
                value = "dynamicKb"
                label = ReactNode("Dynamic KB")
            }
        }

        div {
            css {
            }

            TabPanel {
                value = "solutionsTab"

                List {
                    dense = true
                    solutions.forEach {
                        if (it.isYes)
                            YesView {
                                solution = it
                            }
                        if (it.isNo)
                            NoView {
                                solution = it
                            }
                        if (it.isHalt)
                            HaltView {
                                solution = it
                            }
                    }
                }
            }
            TabPanel {
                value = "stdInTab"
                //+inputs
               textarea {
                   css  {
                       width = 100.pct
                       height = 30.vh
                   }
                   onChange = { it2 ->
                       TuPrologController.application.currentPage?.stdin = it2.target.value
                   }
               }
            }
            TabPanel {
                value = "stdOutTab"
                +outputs
            }
            TabPanel {
                value = "stdErr"
                +errors
                /*if (eContext != null)
                    +eContext.standardError.toString()
                else
                    +"Empty stdErr"*/
            }
            TabPanel {
                value = "warnings"
                if (warnings != null) {
                    +warnings.message.toString()
                }
//                if (eContext != null)
//                    +eContext.warnings.toString()
//                else
//                    +"Empty warnings"
            }
            TabPanel {
                value = "operators"
                if (eContext != null)
                    table {
                        tr {
                            th {
                                +"FUNCTOR"
                            }
                            th {
                                +"PRIORITY"
                            }
                            th {
                                +"SPECIFIER"
                            }
                        }
                        for (operator in eContext.operators) {
                            tr {
                                td {
                                    +operator.functor
                                }
                                td {
                                    +operator.priority.toString()
                                }
                                td {
                                    +operator.specifier.toString()
                                }
                            }
                        }
                    }
                else
                    +"Empty operators"
            }
            TabPanel {
                value = "libraries"
                if (eContext != null) {
                    eContext.libraries.libraries.forEach {
                        details {
                            summary {
                                +it.alias
                            }
                                details {
                                    summary {
                                        +"Functions"
                                    }
                                    ul {
                                it.functions.keys.forEach { it2 ->
                                    li {
                                        +it2.toIndicator().toString()
                                    }
                                }
                            }
                                }
                                details {
                                    summary {
                                        +"Predicates"
                                    }
                                    ul {
                                        val myPredicates: MutableList<String> =
                                            mutableListOf()
                                        it.clauses.filterIsInstance<Rule>()
                                            .forEach { it2 ->
                                                myPredicates += it2.head.indicator.toString()
                                            }
                                        it.primitives.keys.forEach { it3 ->
                                            myPredicates += it3.toIndicator()
                                                .toString()
                                        }
                                        myPredicates.distinct().sorted()
                                            .forEach {
                                                li {
                                                    +it
                                                }
                                            }
                                    }
                                }
                                details {
                                    summary {
                                        +"Operators"
                                    }
                                    ul {
                                        it.operators.forEach { it2 ->
                                            li {
                                                +it2.functor.plus(" , ")
                                                    .plus(it2.specifier.toString())
                                                    .plus(" , ")
                                                    .plus(it2.priority.toString())
                                            }
                                        }
                                    }
                            }
                        }
                    }
                } else
                    +"Empty libraries"
            }
            TabPanel {
                value = "staticKb"
                if (eContext != null)
                    +eContext.staticKb.toString()
                else
                    +"Empty staticKb"
            }
            TabPanel {
                value = "dynamicKb"
                if (eContext != null)
                    eContext.dynamicKb
                else
                    +"Empty dynamicKb"
            }
        }
    }
}
