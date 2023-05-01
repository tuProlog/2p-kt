@file:JsModule("@monaco-editor/react")
@file:JsNonModule

package it.unibo.tuprolog.ide.web.utils

import react.ComponentClass
import react.Props

external interface EditorProps : Props {
    var value: String
    var height: String
    var onChange: (String) -> Unit
    var beforeMount: (Any) -> Unit
}

@JsName("default")
external val Editor: ComponentClass<EditorProps>


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
// 			[/[\/*]/, 'comment'],
// 			[/\/\/.*$/, 'comment'],
// 			[/%.*$/, 'comment']
// 		],
//
// 		string: [
// 			[/[^\\"]+/, 'string'],
// 			[/@escapes/, 'string.escape'],
// 			[/\\./, 'string.escape.invalid'],
// 			[/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }]
// 		],
//
// 		whitespace: [
//   			[/[ \t\r\n]+/, 'white'],
// 		]
// 	},
// })
