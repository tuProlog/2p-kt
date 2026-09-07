# Design Philosophy

2P-Kt is not meant to be "just another Prolog implementation." It is an attempt to build an open,
extensible ecosystem for **symbolic AI**, of which Prolog-style logic programming is only the most visible
instance. Three commitments drive most of the design decisions documented elsewhere in this section: being
general about logic programming rather than committed to Prolog, being radically modular, and blending
symbolic reasoning with the object-oriented and functional worlds it has to run alongside.

## Wider than Prolog

Classical Prolog fixes a knowledge representation (Horn clauses), an inference rule (deduction), and a
resolution strategy (SLD, generally with negation as failure). 2P-Kt treats each of those as a variation
point rather than a given:

- **multiple logics** — besides first-order Horn clauses, the design leaves room for description logics,
  temporal logics, BDI (belief-desire-intention) models, and so on;
- **multiple inference rules** — deduction is the default, but abduction and induction are legitimate modes
  of reasoning the architecture does not rule out;
- **multiple resolution strategies** for the same inference rule — SLD-NF is what most users want most of
  the time, but it is one strategy among possibly several (IFF, probabilistic resolution, ...), not *the*
  strategy.

Concretely, this is why "resolution" and "SLD-NF resolution" are two different modules: `:solve` defines a
generic, strategy-agnostic API for resolving logic queries, and `:solve-classic` / `:solve-streams` are two
independent, swappable implementations of Prolog-style SLD-NF resolution on top of it. The same pattern
repeats with `:solve-plp`, which resolves queries under a probabilistic-logic-programming semantics instead.
Nothing in `:core`, `:unify`, or `:theory` assumes Prolog is the only client.

## Modularity as a first-class goal

The practical expression of "each LP aspect individually usable on its own" is an architecture split into
roughly thirty small, incrementally inter-dependent Gradle modules, described as **onion-like**: `:core`
knows nothing about unification; `:unify` knows nothing about theories or resolution; `:theory` knows
nothing about resolution strategies; and so on outward. Each layer only depends on the layers beneath it,
never sideways or upward. See [Module map](../reference/module-map.md) for the concrete dependency graph.

This is a deliberate reaction to the very problem that justified [rewriting tuProlog in the first
place](project-history.md): a decade of unstructured growth had left the original codebase without clear
boundaries, making it hard to reuse, test, or reason about any single piece in isolation. Fine-grained
modules fix that by construction — you can depend on `:core` alone to manipulate logic terms without pulling
in a resolution engine, or depend on `:theory` without committing to any particular solving strategy. The
same discipline is what makes it feasible to bolt on genuinely new capabilities — a probabilistic solver, a
concurrent solver, an OOP-interoperability library — as new leaf modules instead of invasive changes to the
core.

## Blending symbolic AI with OOP and FP, on any platform

The 2P-Kt ecosystem is also explicit about not wanting to live in a purely symbolic silo. It aims to blend
logic programming with object-oriented and functional programming (Kotlin being a language that supports
both comfortably), and — beyond the ecosystem's current modules — to leave room for bridging symbolic and
sub-symbolic AI, rather than treating logic programming as a closed, self-sufficient paradigm.

That ambition is inseparable from the platform decision covered in [Why Kotlin
Multiplatform](kotlin-multiplatform.md): a symbolic-AI toolkit that only runs on the JVM cannot easily
integrate with browser-based or JavaScript-hosted systems, nor can it be embedded into applications written
outside the JVM ecosystem. Choosing Kotlin Multiplatform was as much a philosophical choice — logic
programming should not be siloed away from where the rest of modern software runs — as a technical one.
