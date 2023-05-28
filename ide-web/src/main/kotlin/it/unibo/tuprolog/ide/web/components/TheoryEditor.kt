package it.unibo.tuprolog.ide.web.components

import csstype.Display
import csstype.FlexFlow
import csstype.number
import csstype.pct
import csstype.px
import emotion.react.css
import it.unibo.tuprolog.ide.web.redux.AppState
import it.unibo.tuprolog.ide.web.redux.PageWrapper
import it.unibo.tuprolog.ide.web.tuprolog.TPPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.utils.MonacoEditor
import it.unibo.tuprolog.ide.web.utils.Themes
import it.unibo.tuprolog.ui.gui.PageID
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
import react.redux.useSelector
import react.useRequiredContext

val TheoryEditors = FC<Props> {
    val provTheme by useRequiredContext(ThemeContext)

    val editorSelectedTab = useSelector<AppState, PageWrapper?> { s -> s.tuProlog.currentPage }
    val editorTabs = useSelector<AppState, MutableMap<TPPage, PageWrapper>> { s -> s.tuProlog.pages }
    val editorTabsNames = useSelector<AppState, Collection<PageID>> { s -> s.tuProlog.pages.keys.map { it.id } }

    div {
        css {
            display = Display.flex
            flexFlow = FlexFlow.column
            height = 100.pct
            padding = 0.px
        }
        if (editorSelectedTab != null) {
            TabContext {
                value = editorSelectedTab.id.name

                Tabs {
                    value = editorSelectedTab.id.name
                    variant = TabsVariant.scrollable
                    scrollButtons = TabsScrollButtons.auto
                    onChange = { _, newValue ->
                        val newPage = TuPrologController.application.pages.find { p -> p.id.name == newValue }
                        if (newPage != null) {
                            TuPrologController.application.select(newPage)
                        }
                    }

                    editorTabsNames.forEach {
                        Tab {
                            value = it.name
                            label = ReactNode(it.name)
                            wrapped = true
                        }
                    }
                }

                editorTabs.forEach {
                    TabPanel {
                        value = it.key.id.name
                        css {
                            flexGrow = number(1.0)
                            flexShrink = number(1.0)
                            padding = 0.px
                        }

                        MonacoEditor {
                            value = it.value.theory
                            onChange = { it2 ->
                                it.key.theory = it2
                            }
                            if (provTheme == Themes.Dark) {
                                theme = "vs-dark"
                            } else if (provTheme == Themes.Light) {
                                theme = "vs-light"
                            }
                            beforeMount = {
                                //                            console.log(it)
                                //                            it.languages.register("tuProlog")
                                //                            console.log(it.languages)
                                //                            console.log(it.languages.getLanguages())
                                //                            console.log(it.languages.setMonarchTokensProvider())
                            }
                        }
                    }
                }
            }
        }
    }
}
