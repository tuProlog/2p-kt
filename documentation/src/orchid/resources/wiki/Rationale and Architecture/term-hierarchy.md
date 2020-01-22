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

Terms is 2P-Kt are __immutable__ data structures.
This is why all subtypes of `Term` come with no `var` public property nor any public method provoking side effects.
This is deliberate as it simplify design and implementation.
Plus, this enables optimization through caching.
From the user-side, this means that once one you have checked a property (say `isGround`, for example) on a term, you are sure it will never change (for that term).

### Main functionalities of terms

Given a `Term` it is possible to:

- retrieve the [sequence](https://kotlinlang.org/docs/reference/sequences.html) of `Var`iables contained within the term through the `variables: Sequence<Var>` read-only property 

- check whether it is _ground_ (i.e., it does _not_ contain any variable) through the the `isGround: Boolean` read-only property

- graphically represent it as a `String`, through the `toString()` method

- clone it, in order to _refresh_ its variables, through the `freshCopy(): Term` method

- check if it is _equal_ to another term, through the `equals(other: Any): Boolean` method
    + of course, the semantics of `hashCode(): Int` is consistent w.r.t. `equals`

- check if it is **structurally** _equal_ to another term, through the `structurallyEquals(other: Term): Boolean` method

#### About terms representation as `String`

The `Term.toString()` method prodeces a raw, debug-friendly representation of terms where:

- all compound term are in their canonical form, e.g., `'functor'(arg1.toString(), ..., argN.toString())`
    * where apexes may be omitted in simpler cases

- except lists which are represented in square brackets, e.g., `[item1.toString(), ..., itemN.toString()]`

- except sets which are represented within braces, e.g., `{item1.toString(), ..., itemN.toString()}`

- except tuples which are represented within parentheses, e.g., `(item1.toString(), ..., itemN.toString())`

- except clauses which are represented in their canonical form, e.g. `OptionalHead :- Body`
    * where the `OptionalHead` is omitted in case of directives

- all atoms are represented are bare strings, e.g., `'atom'`
    * where apexes may be omitted in simpler cases
   
- all numbers are represented though their natural string representation as decimal integers/reals, e.g., `1` or `2.3`

- all variables are represented though their _complete_ name, e.g., `VariableName_<some unique index here>`

More details about the actual string representations of subtypes are provided in the following sections.

The takeaway here is that the `Term.toString()` is __not__ aimed at presenting terms to teh users.

#### About fresh copies of terms



#### About terms equality

Terms equality is recursively defined.
Roughly speaking the equivalence among two terms holds if and only if:

1. the type of the two terms is equal, and

0. they are both variables, and they have the same _complete_ name, or

0. they are both integers, and they have the same value, or

0. they are both real numbers, and they have the same value, or

0. they are both structures, and they have the same functor and arity, and their arguments are recursively equal.

More details are provided in the following sections.

#### About terms _structural_ equality

### Instantiation of terms

By default, terms are instantiated through static __factory__ methods in the form `<Subtype>.<factoryMethod>(<args>)`, which are described in details in the following sections.
This is necessary since the actual implementation classes of the aforementioned interfaces are __not__ part of the public API of 2P-Kt.

Of course, there is no way to create an instance of `Term` since this is just a super type for all terms.
Only subtypes can actually be instantiated. 
So, for instance, one may create 
- a structure through the `Struct.of(functor: String, varargs args: Term)` method, 
- a variable, through the `Var.of(name: String)` or `Var.anonymous()` methods,
- an atom through the `Atom.of(value: String)` method
- a number through the `Numeric.of(value: String)` method (or the `Integer.of`, or `Real.of` ones)
- etc.

Details about such factory methods are provided below. 

Static factories are not the only means to instantiate terms. 
The notion of [`Scope`](./variables-and-scopes.md) is introduced to serve a similar -- yet more articulated -- use case.

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