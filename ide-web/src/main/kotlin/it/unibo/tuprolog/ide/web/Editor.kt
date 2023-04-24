@file:JsModule("@monaco-editor/react")
@file:JsNonModule

package it.unibo.tuprolog.ide.web

import react.ComponentClass
import react.Props

@JsName("default")
external val Editor: ComponentClass<EditorProps>

external interface EditorProps : Props {
    var value: String
    var height: String
    var onChange: (String) -> Unit
    var beforeMount: (Any) -> Unit
}

