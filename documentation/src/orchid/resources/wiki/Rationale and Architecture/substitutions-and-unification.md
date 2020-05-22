Unification is the process of matching two terms by finding a suitable substitution to their variables, also known as the _most general unifier_ (MGU hereafter).

In 2P-Kt, unification utilities are encapsulated in the `Unificator` interface, which is partially implemented by the `AbstractUnificator` class.

Unification is provided by three functions:
- `mgu(Term, Term): Substitution` tries to unify two Terms, returning the MGU if it succeds, or the Fail object otherwise;
- `match(Term, Term): Boolean` tells whether two Terms match each other, that is there's a MGU for them;
- `unify(Term, Term): Term?` tries to unify two Terms, possibly returning the unified Term in case of success.

## Creating Unificators

Unificator's companion object provides three default implementations of the Unificator interface:

- `default` uses plain `equals` to determine Terms identity (like `strict`) and exploits a LRU cache with a fixed default capacity;
- `naive()` compares numbers by their value;
- `strict()` uses plain `equals` to determine Terms identity.

The `strict` and `naive` versions can also be called by giving them a starting unification _context_ to start with, which is simply a `Substitution` object containing pre-determined variable bindings that the user wishes to employ when unifying. Such behaviour can be obtained as follows:

```kotlin
val context: Substitution = ...
val strict = Unificator.strict()
val strictWithContext = Unificator.strict(context)
```

### Caching Unificators through decoration

Optionally, unificators can also be enabled to cache their results between several operations. This is easily accomplished by _decorating_ them through the companion object's `cached` method, which wraps them in a CachedUnificator instance, allowing them to store a limited amount of requests (default is 32) and repeating their response when necessary.

```kotlin
val unificator = Unificator.default

val cached = Unificator.cached(unificator) // default cache size
val smaller = Unificator.cached(unificator, capacity = 5) // stores up to 5 requests
```

By default, requests are cached following a LRU strategy (least recently used).

## Enabling/disabling occurs-check

The unification algorithm performs occurs-check by default. However, this check can be disabled by specifying the `occurCheckEnabled: Boolean` parameter when calling the aforementioned methods. For example:

```kotlin
val unificator = Unificator.default
val subtitution = unificator.mgu(term1, term2, occurCheckEnabled = false)
```

Note that this mechanism does not work when using the infix, operator-like variants of the unification functions.

## Infix variants

Unificator's companion object provides a handy alternative for calling unification functions in the form of **infix** methods.

These variants -- namely `mguWith`, `matches`, `unifyWith` -- allow developers to employ unification features in a more light and intuitive way, by exploting the syntactic tools provided by the Kotlin language.

This allows us to compute the MGU between two Terms simply by writing:

```kotlin
import it.unibo.tuprolog.unify.Unificator
...
val substitution = term1 mguWith term2
```

Keep in mind that these default variants **always perform occurs-check**, since they employ the implementations provided by `Unificator.default`.

## Implementing different unification strategies

Implementing custom Unificators comes down to defining the `checkTermsEquality` method from the `AbstractUnificator` class. This method is used by the unification algorithm to test whether two terms are matching.

Say we want to define a Unificator that compares numeric values by their absolute value. Then, we can write a new instance of `AbstractUnificator` that compares numbers like so:

```kotlin
val unificator = object : AbstractUnificator() {
    override fun checkTermsEquality(first: Term, second: Term): Boolean = when {
        first is Integer && second is Integer ->
            first.value.absoluteValue.compareTo(second.value.absoluteValue) == 0
        first is Numeric && second is Numeric ->
            first.decimalValue.absoluteValue.compareTo(second.decimalValue.absoluteValue) == 0
        else -> first == second
    }
}
```

Notice how the default case still relies on checking term equality through the `==` operator, which aliases the `Term.equals` method.

This example shows how you can create custom unificator instances on-the-fly, but the same approach can be adopted if you want to extend the hierarchy by proper sub-classing.