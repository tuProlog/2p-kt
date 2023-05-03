package it.unibo.tuprolog.ide.web.components

import Message
import AppState
import mui.icons.material.CancelOutlined
import mui.icons.material.CheckCircleOutline
import mui.icons.material.ErrorOutlineOutlined
import mui.icons.material.TimerOffOutlined
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Alert
import mui.material.List
import mui.material.ListItem
import mui.material.ListItemIcon
import mui.material.ListItemText
import mui.material.Stack
import mui.material.SvgIconColor
import mui.material.Tab
import mui.material.Tabs
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML
import react.redux.useSelector
import csstype.Display
import csstype.FlexFlow
import csstype.Overflow
import csstype.number
import csstype.pct
import csstype.px
import emotion.react.css
import mui.material.Typography
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

val SolutionsContainer = FC<Props> {

    val messages = useSelector<AppState, List<Message>> { s -> s.messages }

    class SolutionIde(val state: String, val sol: String)

    var activeTab by useState("solutionsTab")

    val solutionsMixeds by useState(
        mutableListOf(
            SolutionIde("yes", "x/6"),
            SolutionIde("no", ""),
            SolutionIde("timeout", ""),
            SolutionIde("error", "")
        )
    )
    ReactHTML.div {
        css {
            height = 100.pct
            overflow = Overflow.hidden
        }

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
                    label = ReactNode("Messages")
                }
            }

            div {
                css {
//                    overflowY = Overflow.auto

                }

                TabPanel {
                    value = "solutionsTab"

                    List {
                        dense = true

                        solutionsMixeds.forEach { i ->
                            ListItem {
                                ListItemIcon {
                                    //CheckCircleOutline
                                    if (i.state == "yes")
                                        CheckCircleOutline {
                                            color = SvgIconColor.success
                                        }
                                    if (i.state == "no")
                                        CancelOutlined {
                                            color = SvgIconColor.disabled
                                        }
                                    if (i.state == "timeout")
                                        TimerOffOutlined {
                                            color = SvgIconColor.warning
                                        }
                                    if (i.state == "error")
                                        ErrorOutlineOutlined {
                                            color = SvgIconColor.error
                                        }
                                }
                                ListItemText {
                                    primary = ReactNode(i.state)
                                    secondary = ReactNode(i.sol)
                                }
                            }
                        }
                    }
                }
                TabPanel {
                    value = "stdInTab"
                    Stack {
                        messages.forEach { m ->
                            Alert {
                                severity = m.color
                                +m.text
                            }
                        }
                    }
                }
                TabPanel {
                    value = "stdOutTab"
                    Stack {
                        messages.forEach { m ->
                            Alert {
                                severity = m.color
                                +m.text
                            }
                        }
                    }
                }
                TabPanel {
                    value = "messagesTab"
                    Stack {
                        messages.forEach { m ->
                            Alert {
                                severity = m.color
                                +m.text
                            }
                        }
                    }
                }
            }
        }
    }
}
