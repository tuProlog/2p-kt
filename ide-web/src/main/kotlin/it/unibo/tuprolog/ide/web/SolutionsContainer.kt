package it.unibo.tuprolog.ide.web

import mui.icons.material.CancelOutlined
import mui.icons.material.CheckCircleOutline
import mui.icons.material.ErrorOutlineOutlined
import mui.icons.material.TimerOffOutlined
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.*
import react.*

/*
class Solution(state : String, sol : String) {
    state -> So
    //state puo' essere yes, no, timeout, error
    //sol puo' essere vuoto o string
}
class getSolutionState(s: Solution) {
    val s -> soluzione
    return s.state

}*/

class SolutionIde(val state: String, val sol: String) {
    //private val field: String = ""

    // val s: String = ""
    // s = this.s
    //s: String = this.s
    //return (Solution(state, sol)
}


val SolutionsContainer = FC<Props> {
    var activeTab by useState("tab1")

    var solutionsListYes by useState(
        mutableListOf(
            SolutionIde("yes", "x/4"),
            SolutionIde("yes", "x/2"),
            SolutionIde("yes", "x/5")
        )
    )

    var solutionsListNo by useState(mutableListOf(SolutionIde("no", "")))
    var solutionsListTimeout by useState(mutableListOf(SolutionIde("timeout", "")))
    var solutionsListError by useState(mutableListOf(SolutionIde("error", "")))

    var solutionsMixeds by useState(
        mutableListOf(
            SolutionIde("yes", "x/6"),
            SolutionIde("no", ""),
            SolutionIde("timeout", ""),
            SolutionIde("error", "")
        )
    )

    useEffectOnce {

        //fake solution yes
        solutionsListYes.add(SolutionIde("yes", "x/4"))
        solutionsListYes.add(SolutionIde("yes", "x/2"))
        solutionsListYes.add(SolutionIde("yes", "X/5"))

        //fake solution no
        solutionsListNo.add(SolutionIde("no", ""))

        //fake solution timeout
        solutionsListTimeout.add(SolutionIde("timeout", ""))

        //fake solution error
        solutionsListError.add(SolutionIde("error", ""))


    }

    TabContext {
        value = activeTab
        Tabs {
            value = activeTab
            onChange = { _, newValue -> activeTab = newValue }

            Tab {
                value = "tab1"
                label = ReactNode("Solutions")
                //wrapped = true
            }
            Tab {
                value = "tab2"
                label = ReactNode("Standard In")
            }
            Tab {
                value = "tab3"
                label = ReactNode("Standard Out")
            }
        }
        TabPanel {
            value = "tab1"
            List {
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
            TabPanel {
                value = "tab2"
                List {

                }
            }
            TabPanel {
                value = "tab3"
                List {
                }

            }
        }
    }
}