---
---

Logic variables in 2P-Kt are __immutable__ and named placeholders for terms.
There are two important aspects of logic variables which must be introduced in order to fruitfully use 2P-Kt programmatically.
One aspect concerns the _scoping_ of logic variables within terms.
The other aspect concerns the _substitution_ of logic variables within terms.

## Scoping   
 
There is _no way_ in 2P-Kt to create two instances of `Var` which are equals
(recall that `Var`iables are considered equal if their _complete_ name is equal).
This implies for instance that the following equality:
```kotlin
Var.of("X") == Var.of("X") 
```
will always return `false`, regardless of the string passed to `Var.of`
(recall that the `==` operator in Kotlin corresponds to an invocation of the `.equals` method in Java).
Such behaviour is intended and deliberate.

However in some cases, it may be useful to re-use the same `Var`iable more than once.
To do so, developers must _explicitly store_ the `Var`iables they want to re-use into Kotlin variables.
For instance, the following code:  
```kotlin
val X = Var.of("X")
X == X
```
will always return `true`.

Of course, in there exist articulated cases where defining a lot of Kotlin variables may be cumbersome.
Consider for instance the code for creating the following Prolog rule representing the recursive case of the
well known `member/2` standard predicate:
```prolog
member(H, [_ | T]) :- member(H, T).
```
There, the `H` and `T` `Var`iables both occur twice within the rule.
A correct -- yet cumbersome -- way of creating that rule, would be as follows:
```kotlin
val H = Var.of("H")
val T = Var.of("T")

Rule.of(
    Struct.of("member", H, Cons.of(Var.anonymous(), T)),
    Struct.of("member", H, T)
)
```
In simpler cases, such approach may be ok.
However, in more complex cases this approach may easily fall short.

To simplify the creation of complex terms, the 2P-Kt `core` library comprehends the notion 
of [`Scope`]({{ site.baseUrl }}/kotlindoc/it/unibo/tuprolog/core/scope/).
Briefly speaking, from the OOP perspective, objects of type `Scope`s are _factories_ of `Term`s.
The following diagram summarises the main methods exposed by the `Scope` interface.
{{ load('assets/diagrams/scope.puml') | raw }}

So, for instance, the `member/2` predicate above could be more easily instantiated as follows:
```kotlin
Scope.empty {
    ruleOf(
        structOf("member", varOf("H"), consOf(anonymous(), varOf("T"))),
        structOf("member", varOf("H"), varOf("T"))
    )
}
```
which is equivalent to:
```kotlin
val s = Scope.empty()
s.ruleOf(
    s.structOf("member", s.varOf("H"), s.consOf(anonymous(), s.varOf("T"))),
    s.structOf("member", s.varOf("H"), s.varOf("T"))
)
```
There, any invocation of the `varOf` method either creates a new `Var`iable -- using the name provided as argument --
or retrieves a previously created one having the same name, if any.
Conversely, any invocation of the `anonymous` method is guaranteed to create a fresh new anonymous variable, similarly to
what the `Var.anonymous()` method does.
Analogously, the other factory methods in `Scope` are simple shortcuts for their static counterparts.
So, for instance:
- `Scope.atomOf` corresponds to `Atom.of`
- `Scope.structOf` corresponds to `Struct.of`
- `Scope.listOf` corresponds to `List.of`
- etc  

Notice that two or more successive invocations of the `varOf("_")` method on the same `Scope`, always return the _same_
anonymous variable, whereas two or more successive invocations of the `anonymous()` always return different anonymous variables.

All the variables stored within some `Scope` are accessible by means of the `variables` property, which returns a 
`Map<String, Var>`, mapping the simple names of the locally valid `Var`iables with the `Var` instances themselves.

`Scope`s are __mutable__ objects which are _not_ conceived to be reused after their usage.
This is way a fresh new `Scope` should be created every time a different scope is needed.
For instance, while parsing a theory (i.e., a list of clauses), it would be reasonable to use one different instance of 
`Scope` per clause.
A new _empty_ `Scope` can be created through the `Scope.empty` static factory method and its overloads.
However, `Scope`s could also be created out of some pre-existing variables, through the `Scope.of` method and its overloads.

### Refreshing terms

It is worth to be mentioned that the `Term.freshCopy` method works by exploiting the methods of the `Scope` interface.
In particular a new empty `Scope` is created every time the `freshCopy()` is invoked on some `Term`.

The `Term.freshCopy()` method returns a fresh copy of the `Term` it is invoked upon.
A fresh copy is _another_ `Term` which is _equal_ to the original one in any aspect, except variables directly or
indirectly contained in it, which are __refreshed__---i.e. replaced by a new variable having the same _simple_ 
name of the original one. 
In particular, thanks to the instance of `Scope` created behind the scenes, variables are refreshed consistently,
meaning that, if more variables exists within the to-be-copied `Term`, which have the same name,
then all fresh copies of such variables will have the same complete name.
Thus, for instance, a fresh copy of the `f(X, g(X))` would be something like `f(X_1, g(X_1))` 
instead of `f(X_1, g(X_2))`.