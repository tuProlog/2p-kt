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
