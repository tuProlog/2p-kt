package it.unibo.tuprolog.ide.web

// TEST 1
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
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

fun testTuprolog() {
    console.log("Test tuProlog")

    // TEST 1
    val stringTheory = """
        increment(A, B, C) :-
            C is A + B.
    """.trimIndent()
    val clauseReader = ClausesParser.withDefaultOperators()
    val theory = clauseReader.parseTheory(stringTheory)
    val solver = Solver.prolog.mutableSolverWithDefaultBuiltins(staticKb = theory)
    val queryParser = TermParser.withDefaultOperators()
    val query = queryParser.parseStruct("increment(1,2,X).".trimIndent())
    for (solution in solver.solve(query)) {
        if (solution.isYes) {
//            val value = solution.substitution.getByName("X")
//            val valueAsBigInteger = value?.asInteger()?.value
//            val actualValue = valueAsBigInteger?.toInt()
            console.log(solution.query)
            console.log(solution.isYes)
            console.log(solution.substitution)
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
    val prolog = Solver.prolog.solverWithDefaultBuiltins(
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
    testTuprolog()

    val root = document.createElement(HTML.div)
        .also { document.body.appendChild(it) }
    createRoot(root)
        .render(App.create())
}

class EditorTab(var fileName: String, var editorValue: String)

val App = FC<Props> {
//    var isOpen by useState(false)
//
//    var inputRef by useState(null)
//
//    var isMenuFileOpen by useState(false)
//    var isMenuAboutOpen by useState(false)
//    var editorValue by useState("")
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

                    testTuprolog()
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
                                //  console.log("ASD")
                                console.log(it)

//
//                                monaco.languages.setMonarchTokensProvider('tuprolog', {
//
//                                    symbols: /[=><!~?:&|+\-*\/\^%]+/,
//
//                                    escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
//
//                                    tokenizer: {
//                                    root: [
//                                    // functors
//                                    [/([a-z][a-zA-Z_0-9]*)\s*(?=\()/, 'type.identifier'],
//
//                                    // whitespace
//                                    { include: '@whitespace' },
//
//                                    // delimiters and operators
//                                    [/[{}()\[\]]/, '@brackets'],
//                                    [/((?!\/\*)[+*\/^<>=~:.?@#$&\\-]+)|!|;|,|rem|mod|is/, 'type.operators'],
//
//                                    // numbers
//                                    [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
//                                    [/0[xX][0-9a-fA-F]+/, 'number.hex'],
//                                    [/0[oO][0-7]+/, 'number.oct'],
//                                    [/0[bB][0-1]+/, 'number.bin'],
//                                    [/\d+/, 'number'],
//
//                                    [/[;,.]/, 'delimiter'],
//
//                                    // strings
//                                    [/"([^"\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
//                                    [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],
//
//                                    // characters
//                                    [/'[^\\']'/, 'string'],
//                                    [/(')(@escapes)(')/, ['string', 'string.escape', 'string']],
//                                    [/'/, 'string.invalid']
//                                    ],
//
//                                    comment: [
//                                    [/[^\/*]+/, 'comment'],
//			[/[\/*]/, 'comment'],
//			[/\/\/.*$/, 'comment'],
//			[/%.*$/, 'comment']
//		],
//
//		string: [
//			[/[^\\"]+/, 'string'],
//			[/@escapes/, 'string.escape'],
//			[/\\./, 'string.escape.invalid'],
//			[/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }]
//		],
//
//		whitespace: [
//			[/[ \t\r\n]+/, 'white'],
//		]
//	},
//})

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

            QueryEditor {}

            SolutionsContainer {}

            Footer {}
        }


//        Stack {
//            Button{
//                variant = contained
//                disabled = false
//                onClick = {
//                    isMenuFileOpen = true }
//                +"File"
//            }
//            Button{
//                variant = contained
//                disabled = false
//                onClick = {
//                    showAbout()
//                }
//                +"About"
//            }
//            Menu {
//                open = isMenuFileOpen
//
//                onClose = { isMenuFileOpen = false }
//                MenuItem {
//                    onClick = {
//                        isMenuFileOpen = false
//                        //funtestdiag2()
//                    }
//                    +"Open ..."
//                }
//
//                MenuItem {
//                    onClick = { isMenuFileOpen = false }
//                    +"Save As ..."
//                }
//                MenuItem {
//                    onClick = { isMenuFileOpen = false }
//                    +"Save"
//                }
//            }
//        }
//
//        input {
//            key = "zipCode"
//            type = InputType.file
//            name = "zipCode"
//            placeholder = "Enter ZIP code"
//            onChange = {
//                it.target.files?.get(0)?.text()?.then { it1 ->
//                    console.log(it1)
//                    editorValue = it1
//                }
//            }
//        }
//        div {
//            Button {
//                variant = contained
//                onClick = {
//                    console.log(inputRef)
//                }
//            }
//        }
//
//        div {
//            Button {
//                variant = contained
//                onClick = { isOpen = true }
//                +"Open dialog"
//            }
//
//            Dialog {
//                open = isOpen
//                onClose = { _, _ -> isOpen = false }
//
//                DialogTitle {
//                    +"About"
//                }
//                DialogContent {
//                    DialogContentText {
//                        +"TupKTWeb versione 0.1"
//                    }
//                    DialogContentText {
//
//                        h3 {
//                            +"basata su versione di Tuprolog"
//                        }
//                        h1 {
//                            +"dev by pollo111"
//                        }
//                    }
//                    DialogActions {
//                        Button {
//                            onClick = { isOpen = false }
//                            +"OK"
//                        }
//                        /*
//                        Button {
//                            onClick = { isOpen = false }
//                            +"Subscribe"
//                        }
//                         */
//                    }
//                }
//            }
//        }
    }

}


/* creare una promise o la fai lui e la svolte lui ???

private operator fun <T> Promise<T>?.get(t: T): Any {

}

 */


fun showAbout() {
    window.alert("SOSOSOSOOSOSOSSOSO")
}


/*    <SimpleDialog
    selectedValue={selectedValue}
    open={open}
    onClose={handleClose}
    />*/


//fun openFileFun() {
//
//
////    https://www.pluralsight.com/guides/uploading-files-with-reactjs
//
//
//}

//input {
//    key = "zipCode"
//
//    type = InputType.text
//    name = "zipCode"
//    value = zipCode
//    placeholder = "Enter ZIP code"
//    title = infoText
//    onChange = {
//        handleChange(it.target.value)
//    }
//}

//private data class Point(
//    val x: Double,
//    val y: Double,
//)


//        Tabs {
//            value = activeTab
//            onChange = { _, newValue -> activeTab = newValue }
//
//            Tab {
//                value = "one"
//                id = "0"
//                label = ReactNode("New Arrivals in the Longest Text of Nonfiction that should appear in the next line")
//                wrapped = true
//            }
//            Tab {
//                value = "two"
//                id = "1"
//                label = ReactNode("Item Two")
//            }
//            Tab {
//                value = "three"
//                id = "2"
//                label = ReactNode("Item Three")
//            }
//        }