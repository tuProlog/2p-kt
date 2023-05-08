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
import react.useState

val SolutionsContainer = FC<Props> {

    val solutions = useSelector<AppState, Collection<TuPrologSolution>> { s -> s.tuProlog.solutions }

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
            Tab {
                value = "stdInTab"
                label = ReactNode("Standard In")
            }
            Tab {
                value = "stdOutTab"
                label = ReactNode("Standard Out")
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
            }
            TabPanel {
                value = "stdOutTab"
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
