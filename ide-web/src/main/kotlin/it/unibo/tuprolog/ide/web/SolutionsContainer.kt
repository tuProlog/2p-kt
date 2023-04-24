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

class Solution(val state: String, val sol: String) {
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
            Solution("yes", "x/4"),
            Solution("yes", "x/2"),
            Solution("yes", "x/5")
        )
    )

    var solutionsListNo by useState(mutableListOf(Solution("no", "")))
    var solutionsListTimeout by useState(mutableListOf(Solution("timeout", "")))
    var solutionsListError by useState(mutableListOf(Solution("error", "")))

    var solutionsMixed by useState(
        mutableListOf(
            Solution("yes", "x/6"),
            Solution("no", ""),
            Solution("timeout", ""),
            Solution("error", "")
        )
    )

    useEffectOnce {

        //fake solution yes
        solutionsListYes.add(Solution("yes", "x/4"))
        solutionsListYes.add(Solution("yes", "x/2"))
        solutionsListYes.add(Solution("yes", "X/5"))

        //fake solution no
        solutionsListNo.add(Solution("no", ""))

        //fake solution timeout
        solutionsListTimeout.add(Solution("timeout", ""))

        //fake solution error
        solutionsListError.add(Solution("error", ""))


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
                solutionsMixed.forEach { i ->

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
                            console.log(i.sol)
                            console.log(i.state)
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