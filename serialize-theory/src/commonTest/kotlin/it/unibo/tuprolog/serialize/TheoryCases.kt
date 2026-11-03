package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * A theory, and its expected representations. Theories are made of dummy clauses covering every kind of term and
 * clause, so that these tests don't change whenever the standard library does.
 */
class TheoryCase(
    val id: String,
    private val clauses: () -> List<Clause>,
    val json: String,
    val yaml: String,
) {
    /** A fresh copy of the theory. */
    fun theory(): Theory = Theory.of(Unificator.default, clauses())

    fun text(mimeType: MimeType): String =
        when (mimeType) {
            MimeType.Json -> json
            MimeType.Yaml -> yaml
            else -> error("No expected text for $mimeType")
        }
}

/** A clause built in a scope of its own, so that each clause has its own variables. */
private fun clause(build: Scope.() -> Clause): Clause = Scope.empty().build()

val theoryCases =
    listOf(
        TheoryCase("empty", { emptyList() }, "[]", "[]"),
        TheoryCase(
            "facts-of-every-kind-of-term",
            {
                listOf(
                    clause { factOf(structOf("point", intOf(1), realOf("-2.5"))) },
                    clause { factOf(atomOf("hello world")) },
                    clause { factOf(structOf("big", intOf("123456789012345678901234567890"))) },
                    clause { factOf(structOf("truths", Truth.TRUE, Truth.FAIL)) },
                    clause { factOf(structOf("partial", logicListFrom(atomOf("a"), atomOf("b"), last = varOf("T")))) },
                    clause { factOf(structOf("empties", logicListOf(), blockOf())) },
                    clause { factOf(structOf("nested", structOf("f", structOf("g", logicListOf(intOf(1)))))) },
                )
            },
            """
            [
              {"head": {"fun": "point", "args": [1, {"real": "-2.5"}]}},
              {"head": "hello world"},
              {"head": {"fun": "big", "args": [{"integer": "123456789012345678901234567890"}]}},
              {"head": {"fun": "truths", "args": [true, "fail"]}},
              {"head": {"fun": "partial", "args": [{"list": ["a", "b"], "tail": {"var": "T"}}]}},
              {"head": {"fun": "empties", "args": [{"list": []}, {"block": []}]}},
              {"head": {"fun": "nested", "args": [{"fun": "f", "args": [{"fun": "g", "args": [{"list": [1]}]}]}]}}
            ]
            """.trimIndent(),
            """
            - head:
                fun: "point"
                args:
                  - 1
                  - real: "-2.5"
            - head: "hello world"
            - head:
                fun: "big"
                args:
                  - integer: "123456789012345678901234567890"
            - head:
                fun: "truths"
                args:
                  - true
                  - "fail"
            - head:
                fun: "partial"
                args:
                  - list:
                      - "a"
                      - "b"
                    tail:
                      var: "T"
            - head:
                fun: "empties"
                args:
                  - list: []
                  - block: []
            - head:
                fun: "nested"
                args:
                  - fun: "f"
                    args:
                      - fun: "g"
                        args:
                          - list:
                              - 1
            """.trimIndent(),
        ),
        TheoryCase(
            "rules-with-shared-variables-and-operators",
            {
                listOf(
                    clause {
                        ruleOf(
                            structOf("grandparent", varOf("X"), varOf("Z")),
                            structOf("parent", varOf("X"), varOf("Y")),
                            structOf("parent", varOf("Y"), varOf("Z")),
                        )
                    },
                    clause {
                        ruleOf(
                            structOf("max", varOf("X"), varOf("Y"), varOf("X")),
                            structOf(">=", varOf("X"), varOf("Y")),
                            atomOf("!"),
                        )
                    },
                    clause {
                        ruleOf(
                            structOf("abs", varOf("X"), varOf("Y")),
                            structOf(
                                ";",
                                structOf(
                                    "->",
                                    structOf("<", varOf("X"), intOf(0)),
                                    structOf("is", varOf("Y"), structOf("-", varOf("X"))),
                                ),
                                structOf("=", varOf("Y"), varOf("X")),
                            ),
                        )
                    },
                    clause {
                        ruleOf(
                            structOf("group", varOf("L")),
                            structOf("=", varOf("L"), blockOf(atomOf("a"), atomOf("b"), atomOf("c"))),
                        )
                    },
                )
            },
            """
            [
              {
                "head": {"fun": "grandparent", "args": [{"var": "X"}, {"var": "Z"}]},
                "body": {"tuple": [
                  {"fun": "parent", "args": [{"var": "X"}, {"var": "Y"}]},
                  {"fun": "parent", "args": [{"var": "Y"}, {"var": "Z"}]}
                ]}
              },
              {
                "head": {"fun": "max", "args": [{"var": "X"}, {"var": "Y"}, {"var": "X"}]},
                "body": {"tuple": [{"fun": ">=", "args": [{"var": "X"}, {"var": "Y"}]}, "!"]}
              },
              {
                "head": {"fun": "abs", "args": [{"var": "X"}, {"var": "Y"}]},
                "body": {"fun": ";", "args": [
                  {"fun": "->", "args": [
                    {"fun": "<", "args": [{"var": "X"}, 0]},
                    {"fun": "is", "args": [{"var": "Y"}, {"fun": "-", "args": [{"var": "X"}]}]}
                  ]},
                  {"fun": "=", "args": [{"var": "Y"}, {"var": "X"}]}
                ]}
              },
              {
                "head": {"fun": "group", "args": [{"var": "L"}]},
                "body": {"fun": "=", "args": [{"var": "L"}, {"block": ["a", "b", "c"]}]}
              }
            ]
            """.trimIndent(),
            """
            - head:
                fun: "grandparent"
                args:
                  - var: "X"
                  - var: "Z"
              body:
                tuple:
                  - fun: "parent"
                    args:
                      - var: "X"
                      - var: "Y"
                  - fun: "parent"
                    args:
                      - var: "Y"
                      - var: "Z"
            - head:
                fun: "max"
                args:
                  - var: "X"
                  - var: "Y"
                  - var: "X"
              body:
                tuple:
                  - fun: ">="
                    args:
                      - var: "X"
                      - var: "Y"
                  - "!"
            - head:
                fun: "abs"
                args:
                  - var: "X"
                  - var: "Y"
              body:
                fun: ";"
                args:
                  - fun: "->"
                    args:
                      - fun: "<"
                        args:
                          - var: "X"
                          - 0
                      - fun: "is"
                        args:
                          - var: "Y"
                          - fun: "-"
                            args:
                              - var: "X"
                  - fun: "="
                    args:
                      - var: "Y"
                      - var: "X"
            - head:
                fun: "group"
                args:
                  - var: "L"
              body:
                fun: "="
                args:
                  - var: "L"
                  - block:
                      - "a"
                      - "b"
                      - "c"
            """.trimIndent(),
        ),
        TheoryCase(
            "directives-among-facts",
            {
                listOf(
                    clause { directiveOf(structOf("dynamic", indicatorOf("counter", 1))) },
                    clause { factOf(structOf("counter", intOf(0))) },
                    clause { directiveOf(structOf("write", atomOf("hello")), atomOf("nl")) },
                )
            },
            """
            [
              {"body": {"fun": "dynamic", "args": [{"name": "counter", "arity": 1}]}},
              {"head": {"fun": "counter", "args": [0]}},
              {"body": {"tuple": [{"fun": "write", "args": ["hello"]}, "nl"]}}
            ]
            """.trimIndent(),
            """
            - body:
                fun: "dynamic"
                args:
                  - name: "counter"
                    arity: 1
            - head:
                fun: "counter"
                args:
                  - 0
            - body:
                tuple:
                  - fun: "write"
                    args:
                      - "hello"
                  - "nl"
            """.trimIndent(),
        ),
    )

/** The two textual formats every platform supports. */
val textualMimeTypes = listOf(MimeType.Json, MimeType.Yaml)
