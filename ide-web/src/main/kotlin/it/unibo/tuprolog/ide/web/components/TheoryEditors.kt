package it.unibo.tuprolog.ide.web.components

import EditorTab
import AppState
import csstype.Display
import csstype.FlexFlow
import csstype.number
import csstype.pct
import csstype.px
import emotion.react.css
import it.unibo.tuprolog.ide.web.redux.actions.ChangeSelectedTab
import it.unibo.tuprolog.ide.web.redux.actions.UpdateEditorTheory
import it.unibo.tuprolog.ide.web.utils.MonacoEditor
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsScrollButtons
import mui.material.TabsVariant
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.div
import react.redux.useDispatch
import react.redux.useSelector
import redux.RAction
import redux.WrapperAction

val TheoryEditors = FC<Props> {

    val editorSelectedTab = useSelector<AppState, String> { s -> s.tuProlog.editorSelectedTab }
    val editorTabs = useSelector<AppState, List<EditorTab>> { s -> s.tuProlog.editorTabs }
    val dispatcher = useDispatch<RAction, WrapperAction>()

    div {
        css {
            display = Display.flex
            flexFlow = FlexFlow.column
            height = 100.pct
        }
        
        
        TabContext {
            value = editorSelectedTab
            Tabs {
                value = editorSelectedTab
                variant = TabsVariant.scrollable
                scrollButtons = TabsScrollButtons.auto
                onChange = { _, newValue ->
                    dispatcher(ChangeSelectedTab(newValue as String))
                }

                editorTabs.forEach {
                    Tab {
                        value = it.fileName
                        label = ReactNode(it.fileName)
                        wrapped = true
                    }
                }
            }

            editorTabs.forEach {
                TabPanel {
                    value = it.fileName
                    css {
                        flexGrow = number(1.0)
                        flexShrink = number(1.0)
                        padding = 0.px
                    }

                    MonacoEditor {
                        value = it.editorValue
                        onChange = {
                            dispatcher(UpdateEditorTheory(it))
                        }
                        beforeMount = {
                        }
                    }
                }
            }
        }
    }
}