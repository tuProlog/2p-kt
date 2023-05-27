@file:JsModule("@monaco-editor/react")
@file:JsNonModule

package it.unibo.tuprolog.ide.web.utils

import react.ComponentClass
import react.Props

//external interface BeforeMount {
//    val languages: Languages
//}
//
//external interface Languages {
//    fun getLanguages(): Any
//
//    val register: (String) -> Any
//
//    val setMonarchTokensProvider: (String, IMonarchLanguage) -> Any
//}

external interface IMonarchLanguage

external interface EditorProps : Props {
    var value: String
    var height: String
    var onChange: (String) -> Unit
    var beforeMount: () -> Unit
    var theme: String
}

@JsName("default")
external val MonacoEditor: ComponentClass<EditorProps>

