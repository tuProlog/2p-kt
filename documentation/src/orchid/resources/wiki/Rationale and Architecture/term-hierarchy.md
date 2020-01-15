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

{% filter compileAs('uml') %}
interface Term {
  + isGround: Boolean
  + variables: Sequence<Var>
  + equals(other: Any): Boolean
  + structurallyEquals(other: Term): Boolean
  + freshCopy(): Term
  + toString(): String
}
interface Constant {
  + value: Any
}
interface Var {
  + name: String
  + complete: Name
  + {static} of(name: String): Var
}
interface Struct {
  + functor: String
  + arity: Int
  + args: Array<Term>
  + indicator: Indicator
  + get(index: Int): Term
  + {static} of(functor: String, **varargs** args: Term): Struct
}
interface Numeric {
  + intValue: BigInteger
  + decimalValue: BigDecimal
  + {static} of(value: Number): Numeric
  + {static} of(number: String): Numeric
  + {static} of(integer: BigInteger): Integer
  + {static} of(decimal: BigDecimal): Real
}
interface Integer {
  + value: BigInteger
  + {static} of(integer: Int): Integer
  + {static} of(integer: Long): Integer
  + {static} of(integer: Byte): Integer
  + {static} of(integer: Short): Integer
  + {static} of(integer: BigInteger): Integer
  + {static} of(integer: BigDecimal): Integer
  + {static} of(integer: String): Integer
  + {static} of(integer: String, radix: Int): Integer
}
interface Real {
  + value: BigDecimal
  + {static} of(real: BigDecimal): Real
  + {static} of(real: Float): Real
  + {static} of(real: Double): Real
  + {static} of(real: String): Real
}
interface Atom {
  + value: String
  + {static} of(value: String): Atom
}
interface Truth {
  + isTrue: Boolean
  + isFail: Boolean
  + {static} of(value: Boolean): Truth
}
interface Indicator {
  + nameTerm: Term
  + indicatedName: String?
  + arityTerm: Term
  + indicatedArity: Int?
  + isWellFormed: Boolean
  + {static} of(name: String, arity: Int): Indicator
  + {static} of(name: Term, arity: Term): Indicator
}
interface Empty {
  + {static} list(): EmptyList
  + {static} set(): EmptySet
}
interface EmptySet {
  + {static} invoke(): EmptySet
}
interface EmptyList {
  + {static} invoke(): EmptyList
}
interface List {
  + size: Int
  + unfoldedArray: Array<Term>
  + unfoldedList: List<Term>
  + unfoldedSequence: Sequence<Term>
  + toArray(): Array<Term>
  + toList(): List<Term>
  + toSequence(): Sequence<Term>
  + {static} empty(): List
  + {static} of(**varargs** items: Term): List
  + {static} of(items: Iterable<Term>): List
  + {static} from(iterable: Iterable<Term>, last: Term? **= null**): List
}
interface Cons {
  + head: Term
  + tail: Term
  + {static} of(head: Term, tail: Term): Cons
  + {static} singleton(head: Term): Cons
}
interface Set {
  + unfoldedArray: Array<Term>
  + unfoldedList: List<Term>
  + unfoldedSequence: Sequence<Term>
  + toArray(): Array<Term>
  + toList(): List<Term>
  + toSequence(): Sequence<Term>
  + {static} empty(): Set
  + {static} of(**varargs** items: Term): Set
  + {static} of(items: Iterable<Term>): Set
}
interface Tuple {
  + left: Term
  + right: Term
  + {static} of(left: Term, right: Term): Tuple
  + {static} of(**varargs** items: Term): Tuple
  + {static} of(items: Iterable<Term>): Tuple
}

Term <|-down- Struct
Term <|-down- Constant
Term <|-down- Var 

Constant <|-down- Numeric
Constant <|-down- Atom

Numeric <|-down- Real
Numeric <|-down- Integer

Struct <|-down- Atom
Struct <|-down- List
Struct <|-down- Tuple 
Struct <|-down- Set
Struct <|-down- Indicator

Atom <|-down- Truth
Atom <|-down- Empty

Empty <|-up- EmptyList
Empty <|-up- EmptySet

List <|-down- EmptyList
List <|-down- Cons
Set <|-down- EmptySet

package clauses <<Rectangle>> {

    interface Clause {
      + head: Struct?
      + body: Term
      + isWellFormed: Boolean
      + {static} of(head: Struct?, **varargs** body: Term): Clause
    }
    interface Rule {
      + head: Struct
      + body: Term
      + {static} of(head: Struct, **varargs** body: Term): Rule
    }
    interface Directive {
      + head: Struct? **= null**
      + {static} of(**varargs** body: Term): Directive
    }
    interface Fact {
      + body: Term **= Truth.of(true)**
      + {static} of(head: Struct): Fact
    }
    Struct <|-down- Clause
    Clause <|-down- Rule
    Clause <|-down- Directive
    Rule <|-down- Fact

}
{% endfilter %}

