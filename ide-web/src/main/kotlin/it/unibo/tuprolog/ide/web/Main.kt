package it.unibo.tuprolog.ide.web

// TEST 1
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.theory.parsing.ClausesParser

// TEST 2
// import it.unibo.tuprolog.core.parsing.parseAsStruct

// TEST 3
//import it.unibo.tuprolog.core.Struct
//import it.unibo.tuprolog.core.Atom
//import it.unibo.tuprolog.core.Var
//import it.unibo.tuprolog.core.Fact
//import it.unibo.tuprolog.solve.Solver
//import it.unibo.tuprolog.solve.Solution
//import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
//import it.unibo.tuprolog.theory.Theory

import js.uri.encodeURIComponent
import kotlinx.browser.window
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.*
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import web.dom.document
import web.html.HTML
import kotlin.js.Date

fun testTuprolog(myTheory: String, myQuery: String) {
    console.log("Test tuProlog")

    // TEST 1
    val stringTheory = myTheory.trimIndent()
    val clauseReader = ClausesParser.withDefaultOperators()
    val theory = clauseReader.parseTheory(stringTheory)
    val solver = ClassicSolverFactory.mutableSolverWithDefaultBuiltins(staticKb = theory)
    val queryParser = TermParser.withDefaultOperators()
    val query = queryParser.parseStruct(myQuery.trimIndent())
    for (solution in solver.solve(query)) {
        if (solution.isYes)
            console.log("YES")
        else
            console.log("NO")

        if (solution.isYes) {
            console.log("QUERY", solution.query)
            console.log("SOLUTION", solution.substitution)

            val value = solution.substitution.getByName("X")
//            val valueAsBigInteger = value?.asInteger()?.value
//            val actualValue = valueAsBigInteger?.toInt()
            console.log("RESULT", value)
        }
    }

    // ERROR
    /*
    Compiled with problems:X
    ERROR in ../../node_modules/antlr4/src/antlr4/CharStreams.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in 'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    ERROR in ../../node_modules/antlr4/src/antlr4/FileStream.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in 'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    */

    // TEST 2
    /*
    val stringTheory = "increment(A, B, C) :- C is A + B."
    val parsedTheory = stringTheory.parseAsStruct()
    console.log(parsedTheory)
    val stringQuery = "increment(1,2,X)."
    val parsedQuery = stringQuery.parseAsStruct()
    console.log(parsedQuery)
    */
    // ERROR
    /*
    Compiled with problems:X
    ERROR in ../../node_modules/antlr4/src/antlr4/CharStreams.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in 'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    ERROR in ../../node_modules/antlr4/src/antlr4/FileStream.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in 'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    */

    // TEST 3
    /*
    val prolog = ClassicSolverFactory.solverWithDefaultBuiltins(
        staticKb = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c")))
        )
    )
    val goal = Struct.of("f", Var.of("X"))
    val solutions: List<Solution> = prolog.solveList(goal)
    console.log(solutions.size) // 3
    console.log(solutions) // [Yes(query=f(X_2), substitution={X_2=a}), Yes(query=f(X_2), substitution={X_2=b}) Yes(query=f(X_2), substitution={X_2=c})]
    for (solution in solutions) {
        console.log(solution.query) // f(X_2), f(X_2), f(X_2)
        console.log(solution.isYes) // true, true, true
        console.log(solution.substitution) // {X_2=a}, {X_2=b}, {X_2=c}
    }
    */
    // ERROR
    /*
    "IllegalStateException", "No viable implementation for SolverFactory in [./2p-solve-classic:it.unibo.tuprolog.solve.classic.ClassicSolverFactory, 2p-solve-classic:it.unibo.tuprolog.solve.classic.ClassicSolverFactory]"
    */

}

fun main() {
    testTuprolog("""
        increment(A, B, C) :-
            C is A + B.
    """, "increment(1,2,X).")

    val root = document.createElement(HTML.div)
        .also { document.body.appendChild(it) }
    createRoot(root)
        .render(App.create())
}

// THEORY:
//nat(z).
//nat(s(X)) :- nat(X).
// QUERY:
//nat(N)



class EditorTab(var fileName: String, var editorValue: String)

val App = FC<Props> {
    var editorSelectedTab by useState("")
    val editorTabs by useState(mutableListOf<EditorTab>())

    var isErrorAlertOpen by useState(false)
    var errorAlertMessage by useState("")

    fun addNewEditor() {
        val fileName: String = "undefined_" + Date().getTime() + ".pl"
        editorTabs.add(
            EditorTab(
                fileName, """
                    % member2(List, Elem, ListWithoutElem)
                    member2([X|Xs],X,Xs).
                    member2([X|Xs],E,[X|Ys]):-member2(Xs,E,Ys).
                    % permutation(Ilist, Olist)
                    permutation([],[]).
                    permutation(Xs, [X | Ys]) :-
                    member2(Xs,X,Zs),
                    permutation(Zs, Ys).
        
                    % permutation([10,20,30],L).
                """.trimIndent()
            )
        )
        editorSelectedTab = fileName
    }

    useEffectOnce {
        addNewEditor()
    }

    ReactHTML.div {
        Stack {
            NavBar {
                onFileLoad = { fileName: String, editorValue: String ->
                    if (editorTabs.find { it.fileName == fileName } == null) {
                        editorTabs.add(EditorTab(fileName, editorValue))
                    } else {
                        errorAlertMessage = "File already exists"
                        isErrorAlertOpen = true
                    }
                    editorSelectedTab = fileName
                }
                onAddEditor = {
                    addNewEditor()
                }
                onCloseEditor = {
                    if (editorTabs.size > 1) {
                        // find the deletable tab panel index
                        val index = editorTabs.indexOfFirst { it.fileName == editorSelectedTab }
                        editorTabs.removeAt(index)
                        // select new ide
                        if (index == 0)
                            editorSelectedTab = editorTabs[index].fileName
                        else
                            editorSelectedTab = editorTabs[index - 1].fileName
                    }
                }
                onDownloadTheory = {
                    val editorText = editorTabs.find { it2 -> it2.fileName == editorSelectedTab }?.editorValue ?: ""
                    if (editorText != "") {
                        val elem = document.createElement(HTML.a)
                        elem.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(editorText))
                        elem.setAttribute("download", editorSelectedTab)
                        elem.click()
                        isErrorAlertOpen = false
                    } else {
                        errorAlertMessage = "No theory specified"
                        isErrorAlertOpen = true
                    }
                }

                currentFileName = editorSelectedTab

                onRenameEditor = { it ->
                    val isOk: EditorTab? = editorTabs.find { it3 -> it3.fileName == it }
                    if (isOk == null) {
                        val indexForRename = editorTabs.indexOfFirst { it3 -> it3.fileName == editorSelectedTab }
                        editorTabs[indexForRename].fileName = it
                        editorSelectedTab = editorTabs[indexForRename].fileName
                        isErrorAlertOpen = false
                    } else {
                        errorAlertMessage = if (it != editorSelectedTab)
                            "Cannot rename file. A file with this name already exists"
                        else
                            "Cannot rename file with the same value"
                        isErrorAlertOpen = true
                    }
                }

            }

            TabContext {
                value = editorSelectedTab
                Tabs {
                    value = editorSelectedTab
                    variant = TabsVariant.scrollable
                    scrollButtons = TabsScrollButtons.auto
                    onChange = { _, newValue ->
                        editorSelectedTab = newValue as String
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
                        Editor {
                            value = it.editorValue
                            height = "63vh"
                            onChange = {
                                editorTabs.find { it2 -> it2.fileName == editorSelectedTab }?.editorValue = it
                            }
                            beforeMount = {
                            }
                        }
                    }
                }
            }

            Snackbar {
                open = isErrorAlertOpen
                autoHideDuration = 6000
                onClose = { _, _ -> isErrorAlertOpen = false }

                Alert {
                    severity = AlertColor.error
                    +errorAlertMessage
                }
                // TODO: change snack-bar anchor
            }

            QueryEditor {
                onSolve = {
                    val editorTheory = editorTabs.find { it2 -> it2.fileName == editorSelectedTab }?.editorValue ?: ""
                    val editorQuery = it
                    testTuprolog(editorTheory, editorQuery)
                }
            }

            SolutionsContainer {}

            Footer {}
        }
    }
}