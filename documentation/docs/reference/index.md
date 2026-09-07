# Reference

Information-oriented technical description of 2P-Kt's modules and APIs.

The full generated API documentation (Dokka, covering every module) is available at
[`/api/`](../../api/index.html).

## At a glance: the `Term` hierarchy

Every piece of logic data in 2P-Kt — atoms, numbers, variables, structures, clauses — is a [`Term`][term-src].
`Term`s are immutable, tree-like data structures:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt:1:16"
```

![Term interface](../assets/diagrams/term.svg)

[term-src]: https://github.com/tuProlog/2p-kt/blob/master/core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt

See [Module map](module-map.md) for how the ~30 modules relate to each other.
