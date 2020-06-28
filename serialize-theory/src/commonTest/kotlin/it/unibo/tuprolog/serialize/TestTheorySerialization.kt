package it.unibo.tuprolog.serialize

class TestTheorySerialization {
    // [{"head":{"fun":"not","args":[{"var":"G"}]},"body":{"fun":"\\+","args":[{"var":"G"}]}},{"head":{"fun":"->","args":[{"var":"Cond"},{"var":"Then"}]},"body":{"tuple":[{"fun":"call","args":[{"var":"Cond"}]},"!",{"var":"Then"}]}},{"head":{"fun":";","args":[{"fun":"->","args":[{"var":"Cond"},{"var":"Then"}]},{"var":"Else"}]},"body":{"tuple":[{"fun":"call","args":[{"var":"Cond"}]},"!",{"var":"Then"}]}},{"head":{"fun":";","args":[{"fun":"->","args":[{"var":"Cond"},{"var":"Then"}]},{"var":"Else"}]},"body":{"tuple":["!",{"var":"Else"}]}},{"head":{"fun":";","args":[{"var":"A"},{"var":"B"}]},"body":{"var":"A"}},{"head":{"fun":";","args":[{"var":"A"},{"var":"B"}]},"body":{"var":"B"}},{"head":{"fun":"member","args":[{"var":"H"},{"list":[{"var":"H"},{"var":"_"}],"tail":{"var":"_"}}]}},{"head":{"fun":"member","args":[{"var":"H"},{"list":[{"var":"_"},{"var":"T"}],"tail":{"var":"T"}}]},"body":{"fun":"member","args":[{"var":"H"},{"var":"T"}]}},{"head":{"fun":"append","args":[{"list":[]},{"var":"X"},{"var":"X"}]}},{"head":{"fun":"append","args":[{"list":[{"var":"X"},{"var":"Y"}],"tail":{"var":"Y"}},{"var":"Z"},{"list":[{"var":"X"},{"var":"W"}],"tail":{"var":"W"}}]},"body":{"fun":"append","args":[{"var":"Y"},{"var":"Z"},{"var":"W"}]}}]

    /*
- head:
    fun: "not"
    args:
    - var: "G"
  body:
    fun: "\\+"
    args:
    - var: "G"
- head:
    fun: "->"
    args:
    - var: "Cond"
    - var: "Then"
  body:
    tuple:
    - fun: "call"
      args:
      - var: "Cond"
    - "!"
    - var: "Then"
- head:
    fun: ";"
    args:
    - fun: "->"
      args:
      - var: "Cond"
      - var: "Then"
    - var: "Else"
  body:
    tuple:
    - fun: "call"
      args:
      - var: "Cond"
    - "!"
    - var: "Then"
- head:
    fun: ";"
    args:
    - fun: "->"
      args:
      - var: "Cond"
      - var: "Then"
    - var: "Else"
  body:
    tuple:
    - "!"
    - var: "Else"
- head:
    fun: ";"
    args:
    - var: "A"
    - var: "B"
  body:
    var: "A"
- head:
    fun: ";"
    args:
    - var: "A"
    - var: "B"
  body:
    var: "B"
- head:
    fun: "member"
    args:
    - var: "H"
    - list:
      - var: "H"
      - var: "_"
      tail:
        var: "_"
- head:
    fun: "member"
    args:
    - var: "H"
    - list:
      - var: "_"
      - var: "T"
      tail:
        var: "T"
  body:
    fun: "member"
    args:
    - var: "H"
    - var: "T"
- head:
    fun: "append"
    args:
    - list: []
    - var: "X"
    - var: "X"
- head:
    fun: "append"
    args:
    - list:
      - var: "X"
      - var: "Y"
      tail:
        var: "Y"
    - var: "Z"
    - list:
      - var: "X"
      - var: "W"
      tail:
        var: "W"
  body:
    fun: "append"
    args:
    - var: "Y"
    - var: "Z"
    - var: "W"
     */
}