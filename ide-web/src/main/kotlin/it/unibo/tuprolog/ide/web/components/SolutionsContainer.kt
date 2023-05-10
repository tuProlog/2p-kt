package it.unibo.tuprolog.ide.web.components

import Message
import AppState
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Alert
import mui.material.List
import mui.material.Stack
import mui.material.Tab
import mui.material.Tabs
import react.FC
import react.Props
import react.ReactNode
import react.redux.useSelector
import emotion.react.css
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.ide.web.utils.xs
import it.unibo.tuprolog.solve.ExecutionContextAware
import mui.icons.material.Mail
import mui.material.Button
import mui.material.ButtonColor
import mui.material.ButtonVariant
import mui.material.Grid
import mui.material.IconPosition
import mui.material.Size
import mui.material.Typography
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.tr
import react.dom.html.ReactHTML.ul
import react.useState

val SolutionsContainer = FC<Props> {

    val solutions = useSelector<AppState, Collection<TuPrologSolution>> { s -> s.tuProlog.solutions }
    val eContext = useSelector<AppState, ExecutionContextAware?> { s -> s.tuProlog.executionContext }

    val messages = useSelector<AppState, List<Message>> { s -> s.messages }

    var activeTab by useState("solutionsTab")

    TabContext {
        value = activeTab
        Tabs {
            value = activeTab
            onChange = { _, newValue -> activeTab = newValue }

            Tab {
                value = "solutionsTab"
                label = ReactNode("Solutions")
                //wrapped = true
            }
//            Tab {
//                value = "stdInTab"
//                label = ReactNode("Standard In")
//            }
//            Tab {
//                value = "stdOutTab"
//                label = ReactNode("Standard Out")
//            }

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
/*                if (eContext != null) {
                    console.log(eContext.libraries.libraries.toString())
                    console.log("AAAAA")
                    console.log(eContext.libraries.toString())
                    console.log(eContext.libraries)
                    console.log(eContext.libraries.libraries)
                    val myvar = eContext.libraries.libraries.toList()
                    console.log(myvar)
                    console.log("BBB")
                    console.log(myvar[0])
                }*/
            }
            Tab {
                value = "staticKb"
                label = ReactNode("Static KB")
            }
            Tab {
                value = "dynamicKb"
                label = ReactNode("Dynamic KB")
            }
            Tab {
                value = "messagesTab"
                icon = NumberedIcon.create {
                    icon = Mail
                    number = messages.size
                }
                iconPosition = IconPosition.end
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

                if (eContext != null)
                    +eContext.standardInput.toString()
                else
                    +"Empty"
            }
            TabPanel {
                value = "stdOutTab"
                if (eContext != null)
                    +eContext.standardOutput.toString()
                else
                    +"Empty"
            }
            TabPanel {
                value = "stdErr"
                if (eContext != null)
                    +eContext.standardError.toString()
                else
                    +"Empty"
            }
            TabPanel {
                value = "warnings"
                if (eContext != null)
                    +eContext.warnings.toString()
                else
                    +"Empty"
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
                    +"Empty OPERATORS"
            }
            TabPanel {
                value = "libraries"
                if (eContext != null) {
                    ul {
                            eContext.libraries.libraries.forEach {
                                li {
                                    +"operators"
                                }
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
                                    for (operator in it.operators) {
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
                                li {
                                    +"functions"
                                    +it.functions.toString()
                                }
                                //it.functions

                            }
                    }
                }
                else
                    +"Empty LIBRARIES"
            }
            TabPanel {
                value = "staticKb"
                if (eContext != null)
                    +eContext.staticKb.toString()
                else
                    +"Empty"
            }
            TabPanel {
                value = "dynamicKb"
                if (eContext != null)
                    eContext.dynamicKb
                else
                    +"Empty"
            }
            TabPanel {
                value = "messagesTab"

                Grid {
                    container = true

                    Grid {
                        item = true
                        xs = 4

                        Stack {
                            messages.forEach { m ->
                                Alert {
                                    severity = m.color
                                    onClose = {}
                                    +m.text
                                }
                            }
                        }
                    }

                    Grid {
                        item = true
                        xs = true

                        Button {
                            color = ButtonColor.error
                            size = Size.small
                            variant = ButtonVariant.text
                            + "Delete all"
                        }
                        Button {
                            color = ButtonColor.error
                            size = Size.small
                            variant = ButtonVariant.text
                            + "Sort by severity"
                        }
                        Button {
                            color = ButtonColor.error
                            size = Size.small
                            variant = ButtonVariant.text
                            + "Sort recent"
                        }
                        Typography {
                            + "total messages: 1123"
                        }
                    }
                }
            }
        }
    }
}
