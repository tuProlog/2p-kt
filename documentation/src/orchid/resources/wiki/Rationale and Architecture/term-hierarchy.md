---
---

Data and Knowledge are represented in 2P through _logic terms_.
Logic terms are essentially tree-based data structures coming with no predefined semantics.
More information on logic terms can be found on [Wikipedia](https://en.wikipedia.org/wiki/Term_(logic)).

Logic terms can be recursively defined by means of the following context-free grammar:

|-----------:|:----:|----------------------------------------------------------|
|     `Term` | `:=` | `Constant | Var | Struct`                                |
| `Constant` | `:=` | `Atom | Numeric`                                         |
|      `Var` | `:=` | all possible strings over an alphabet for variables      |
|     `Atom` | `:=` | all possible strings over an alphabet for atoms          |
|  `Numeric` | `:=` | `Integer | Real`                                         |
|  `Integer` | `:=` | all possible integer numbers                             |
|     `Real` | `:=` | all possible real numbers                                |
|   `Struct` | `:=` | `Functor(Argument)`                                      |
| `Argument` | `:=` | `Term | Term, Argument`                                  |
|  `Functor` | `:=` | all possible strings over an alphabet for function names |

where non-terminal symbols are uppercase.
There, `Var` is a shortcut for "variable" and `Struct` is a shortcut for "structure".
Structure are also known as "compound terms", whereas constants are also knwon as "atomic terms". 

Often, for practical reasons, the alphabets for atoms and variables both coincide with (some sub-set of) the [UTF-8](https://en.wikipedia.org/wiki/UTF-8) or [UTF-16](https://en.wikipedia.org/wiki/UTF-16) charsets. 
When this is the case, some convention is needed to distinguish among variables and atoms.

In 2P-Kt we adopt the Prolog syntactical convention which states that:
- variables are UTF-16 strings matching the regular expression `[_A-Z](_A-Za-z0-9)*`, i.e., strings of at least 1 alphanumeric character, possibly containing underscores, and starting by either an uppercase letter or an underscore
- atoms are UTF-16 _double/single_-quoted strings or strings matching the regular expression `[a-z](_A-Za-z0-9)*`, i.e., strings of at least 1 alphanumeric character, possibly containing underscores, and starting by either a lowercase letter
- function names are either _non-double_-quoted atoms or UTF-16 strings matching the regular expression `[+*/\^<>=~:.,;?!@#$&-]+`

Thus, for instance, the term: `f(X, y, g(1, 2.3), h(_, 'j'("k")))` must be interpreted as a `Struct`, whose functor is `f`, and having 4 arguments:
- the variable `X`
- the atom `y`
- the structure `g(1, 2.3)`, whose functor is `g`, which in turn has 2 arguments:
    * the integer number `1`
    * the real number `2.3` 
- the structure `h(_, 'j'("k"))`, whose functor is `h`, which in turn has 2 arguments:
    * the variable  `_`
    * the structure `'j'("k")`, which is equal to `j(k)`
    
## Logic terms in 2P-Kt

The base type for logic terms in 2P-Kt is [`Term`](/kotlindoc/it/unibo/tuprolog/core/term). 

As shown in the following diagram, the `Term` interface is the root of an articulate hierarchy of term types: 

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/terms-nofields.puml') | raw }}
<!--div-->

### Terms 

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/term.puml') | raw }}
<!--div-->

### Numbers 

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/numeric.puml') | raw }}
<!--div-->

### Variables 

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/var.puml') | raw }}
<!--div-->

### Structures 

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/struct.puml') | raw }}
<!--div-->

### Atoms 

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/atom.puml') | raw }}
<!--div-->

### Booleans

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/truth.puml') | raw }}
<!--div-->

### Indicators

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/indicator.puml') | raw }}
<!--div-->

### Collections

#### Lists

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/list.puml') | raw }}
<!--div-->

#### Tuples

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/tuple.puml') | raw }}
<!--div-->

#### Sets

<!--div style="width: 100%; overflow: auto; background-color:LightGray" -->
{{ load('assets/diagrams/set.puml') | raw }}
<!--div-->

### Clauses

#### Rules

#### Facts

#### Directives