package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.solve.stdlib.CommonRules
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

object Instances {
    val commonRules = Theory.of(Unificator.default, CommonRules.clauses)

    val commonRulesInJSON =
        """
        |[
        |   {
        |      "head":{
        |         "fun":"not",
        |         "args":[
        |            {
        |               "var":"G"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "tuple":[
        |            {
        |               "fun":"ensure_executable",
        |               "args":[
        |                  {
        |                     "var":"G"
        |                  }
        |               ]
        |            },
        |            {
        |               "fun":"\\+",
        |               "args":[
        |                  {
        |                     "var":"G"
        |                  }
        |               ]
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"->",
        |         "args":[
        |            {
        |               "var":"Cond"
        |            },
        |            {
        |               "var":"Then"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "tuple":[
        |            {
        |               "fun":"call",
        |               "args":[
        |                  {
        |                     "var":"Cond"
        |                  }
        |               ]
        |            },
        |            "!",
        |            {
        |               "var":"Then"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":";",
        |         "args":[
        |            {
        |               "fun":"->",
        |               "args":[
        |                  {
        |                     "var":"Cond"
        |                  },
        |                  {
        |                     "var":"Then"
        |                  }
        |               ]
        |            },
        |            {
        |               "var":"Else"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "tuple":[
        |            {
        |               "fun":"call",
        |               "args":[
        |                  {
        |                     "var":"Cond"
        |                  }
        |               ]
        |            },
        |            "!",
        |            {
        |               "var":"Then"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":";",
        |         "args":[
        |            {
        |               "fun":"->",
        |               "args":[
        |                  {
        |                     "var":"Cond"
        |                  },
        |                  {
        |                     "var":"Then"
        |                  }
        |               ]
        |            },
        |            {
        |               "var":"Else"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "tuple":[
        |            "!",
        |            {
        |               "var":"Else"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":";",
        |         "args":[
        |            {
        |               "var":"A"
        |            },
        |            {
        |               "var":"B"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "var":"A"
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":";",
        |         "args":[
        |            {
        |               "var":"A"
        |            },
        |            {
        |               "var":"B"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "var":"B"
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"member",
        |         "args":[
        |            {
        |               "var":"H"
        |            },
        |            {
        |               "list":[
        |                  {
        |                     "var":"H"
        |                  }
        |               ],
        |               "tail":{
        |                  "var":"_"
        |               }
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"member",
        |         "args":[
        |            {
        |               "var":"H"
        |            },
        |            {
        |               "list":[
        |                  {
        |                     "var":"_"
        |                  }
        |               ],
        |               "tail":{
        |                  "var":"T"
        |               }
        |            }
        |         ]
        |      },
        |      "body":{
        |         "fun":"member",
        |         "args":[
        |            {
        |               "var":"H"
        |            },
        |            {
        |               "var":"T"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"append",
        |         "args":[
        |            {
        |               "list":[
        |                  
        |               ]
        |            },
        |            {
        |               "var":"X"
        |            },
        |            {
        |               "var":"X"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"append",
        |         "args":[
        |            {
        |               "list":[
        |                  {
        |                     "var":"X"
        |                  }
        |               ],
        |               "tail":{
        |                  "var":"Y"
        |               }
        |            },
        |            {
        |               "var":"Z"
        |            },
        |            {
        |               "list":[
        |                  {
        |                     "var":"X"
        |                  }
        |               ],
        |               "tail":{
        |                  "var":"W"
        |               }
        |            }
        |         ]
        |      },
        |      "body":{
        |         "fun":"append",
        |         "args":[
        |            {
        |               "var":"Y"
        |            },
        |            {
        |               "var":"Z"
        |            },
        |            {
        |               "var":"W"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"once",
        |         "args":[
        |            {
        |               "var":"G"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "tuple":[
        |            {
        |               "fun":"ensure_executable",
        |               "args":[
        |                  {
        |                     "var":"G"
        |                  }
        |               ]
        |            },
        |            {
        |               "fun":"call",
        |               "args":[
        |                  {
        |                     "var":"G"
        |                  }
        |               ]
        |            },
        |            "!"
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"set_prolog_flag",
        |         "args":[
        |            {
        |               "var":"Key"
        |            },
        |            {
        |               "var":"Value"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "fun":"set_flag",
        |         "args":[
        |            {
        |               "var":"Key"
        |            },
        |            {
        |               "var":"Value"
        |            }
        |         ]
        |      }
        |   },
        |   {
        |      "head":{
        |         "fun":"current_prolog_flag",
        |         "args":[
        |            {
        |               "var":"Key"
        |            },
        |            {
        |               "var":"Value"
        |            }
        |         ]
        |      },
        |      "body":{
        |         "fun":"current_flag",
        |         "args":[
        |            {
        |               "var":"Key"
        |            },
        |            {
        |               "var":"Value"
        |            }
        |         ]
        |      }
        |   }
        |]
    """.trimMargin()

    val commonRulesInYAML =
        """
        |- head:
        |    fun: "not"
        |    args:
        |    - var: "G"
        |  body:
        |    tuple:
        |    - fun: "ensure_executable"
        |      args:
        |      - var: "G"
        |    - fun: "\\+"
        |      args:
        |      - var: "G"
        |- head:
        |    fun: "->"
        |    args:
        |    - var: "Cond"
        |    - var: "Then"
        |  body:
        |    tuple:
        |    - fun: "call"
        |      args:
        |      - var: "Cond"
        |    - "!"
        |    - var: "Then"
        |- head:
        |    fun: ";"
        |    args:
        |    - fun: "->"
        |      args:
        |      - var: "Cond"
        |      - var: "Then"
        |    - var: "Else"
        |  body:
        |    tuple:
        |    - fun: "call"
        |      args:
        |      - var: "Cond"
        |    - "!"
        |    - var: "Then"
        |- head:
        |    fun: ";"
        |    args:
        |    - fun: "->"
        |      args:
        |      - var: "Cond"
        |      - var: "Then"
        |    - var: "Else"
        |  body:
        |    tuple:
        |    - "!"
        |    - var: "Else"
        |- head:
        |    fun: ";"
        |    args:
        |    - var: "A"
        |    - var: "B"
        |  body:
        |    var: "A"
        |- head:
        |    fun: ";"
        |    args:
        |    - var: "A"
        |    - var: "B"
        |  body:
        |    var: "B"
        |- head:
        |    fun: "member"
        |    args:
        |    - var: "H"
        |    - list:
        |      - var: "H"
        |      tail:
        |        var: "_"
        |- head:
        |    fun: "member"
        |    args:
        |    - var: "H"
        |    - list:
        |      - var: "_"
        |      tail:
        |        var: "T"
        |  body:
        |    fun: "member"
        |    args:
        |    - var: "H"
        |    - var: "T"
        |- head:
        |    fun: "append"
        |    args:
        |    - list: []
        |    - var: "X"
        |    - var: "X"
        |- head:
        |    fun: "append"
        |    args:
        |    - list:
        |      - var: "X"
        |      tail:
        |        var: "Y"
        |    - var: "Z"
        |    - list:
        |      - var: "X"
        |      tail:
        |        var: "W"
        |  body:
        |    fun: "append"
        |    args:
        |    - var: "Y"
        |    - var: "Z"
        |    - var: "W"
        |- head:
        |    fun: "once"
        |    args:
        |    - var: "G"
        |  body:
        |    tuple:
        |    - fun: "ensure_executable"
        |      args:
        |      - var: "G"
        |    - fun: "call"
        |      args:
        |      - var: "G"
        |    - "!"
        |- head:
        |    fun: "set_prolog_flag"
        |    args:
        |    - var: "Key"
        |    - var: "Value"
        |  body:
        |    fun: "set_flag"
        |    args:
        |    - var: "Key"
        |    - var: "Value"
        |- head:
        |    fun: "current_prolog_flag"
        |    args:
        |    - var: "Key"
        |    - var: "Value"
        |  body:
        |    fun: "current_flag"
        |    args:
        |    - var: "Key"
        |    - var: "Value"
        """.trimMargin()
}
