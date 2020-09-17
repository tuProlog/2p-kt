package it.unibo.tuprolog.serialize

object Instances {
    val commonRulesInJSON =
        """
        |[
        |  {
        |    "head":{
        |      "fun":"not",
        |      "args":[
        |        {
        |          "var":"G"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "tuple":[
        |        {
        |          "fun":"ensure_executable",
        |          "args":[
        |            {
        |              "var":"G"
        |            }
        |          ]
        |        },
        |        {
        |          "fun":"\\+",
        |          "args":[
        |            {
        |              "var":"G"
        |            }
        |          ]
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":"->",
        |      "args":[
        |        {
        |          "var":"Cond"
        |        },
        |        {
        |          "var":"Then"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "tuple":[
        |        {
        |          "fun":"call",
        |          "args":[
        |            {
        |              "var":"Cond"
        |            }
        |          ]
        |        },
        |        "!",
        |        {
        |          "var":"Then"
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":";",
        |      "args":[
        |        {
        |          "fun":"->",
        |          "args":[
        |            {
        |              "var":"Cond"
        |            },
        |            {
        |              "var":"Then"
        |            }
        |          ]
        |        },
        |        {
        |          "var":"Else"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "tuple":[
        |        {
        |          "fun":"call",
        |          "args":[
        |            {
        |              "var":"Cond"
        |            }
        |          ]
        |        },
        |        "!",
        |        {
        |          "var":"Then"
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":";",
        |      "args":[
        |        {
        |          "fun":"->",
        |          "args":[
        |            {
        |              "var":"Cond"
        |            },
        |            {
        |              "var":"Then"
        |            }
        |          ]
        |        },
        |        {
        |          "var":"Else"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "tuple":[
        |        "!",
        |        {
        |          "var":"Else"
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":";",
        |      "args":[
        |        {
        |          "var":"A"
        |        },
        |        {
        |          "var":"B"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "var":"A"
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":";",
        |      "args":[
        |        {
        |          "var":"A"
        |        },
        |        {
        |          "var":"B"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "var":"B"
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":"member",
        |      "args":[
        |        {
        |          "var":"H"
        |        },
        |        {
        |          "list":[
        |            {
        |              "var":"H"
        |            }
        |          ],
        |          "tail":{
        |            "var":"_"
        |          }
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":"member",
        |      "args":[
        |        {
        |          "var":"H"
        |        },
        |        {
        |          "list":[
        |            {
        |              "var":"_"
        |            }
        |          ],
        |          "tail":{
        |            "var":"T"
        |          }
        |        }
        |      ]
        |    },
        |    "body":{
        |      "fun":"member",
        |      "args":[
        |        {
        |          "var":"H"
        |        },
        |        {
        |          "var":"T"
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":"append",
        |      "args":[
        |        {
        |          "list":[
        |            
        |          ]
        |        },
        |        {
        |          "var":"X"
        |        },
        |        {
        |          "var":"X"
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":"append",
        |      "args":[
        |        {
        |          "list":[
        |            {
        |              "var":"X"
        |            }
        |          ],
        |          "tail":{
        |            "var":"Y"
        |          }
        |        },
        |        {
        |          "var":"Z"
        |        },
        |        {
        |          "list":[
        |            {
        |              "var":"X"
        |            }
        |          ],
        |          "tail":{
        |            "var":"W"
        |          }
        |        }
        |      ]
        |    },
        |    "body":{
        |      "fun":"append",
        |      "args":[
        |        {
        |          "var":"Y"
        |        },
        |        {
        |          "var":"Z"
        |        },
        |        {
        |          "var":"W"
        |        }
        |      ]
        |    }
        |  },
        |  {
        |    "head":{
        |      "fun":"once",
        |      "args":[
        |        {
        |          "var":"G"
        |        }
        |      ]
        |    },
        |    "body":{
        |      "tuple":[
        |        {
        |          "fun":"ensure_executable",
        |          "args":[
        |            {
        |              "var":"G"
        |            }
        |          ]
        |        },
        |        {
        |          "fun":"call",
        |          "args":[
        |            {
        |              "var":"G"
        |            }
        |          ]
        |        },
        |        "!"
        |      ]
        |    }
        |  }
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
        """.trimMargin()
}
