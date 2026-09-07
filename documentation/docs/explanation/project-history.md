# Project History

## tuProlog, the Java years

2P-Kt is the second life of [tuProlog](https://www.cs.nmsu.edu/ALP/2013/10/tuprolog-making-prolog-ubiquitous/)
(often abbreviated "2P"), a Prolog engine born at the University of Bologna. The original tuProlog was a
lightweight Prolog solver written in Java, designed from the outset around three ideas that still show
through in 2P-Kt today:

- a deliberately **minimal core**, kept small enough to embed in other applications;
- **configurability through libraries**: predicates, operators, and functors could be loaded and unloaded
  at runtime rather than being baked into a monolithic interpreter;
- **native bidirectional interoperability with OOP hosts** — first Java, so that Prolog and Java objects
  could call into each other directly, without a foreign-function boundary getting in the way.

Its solver was built around an explicit state machine, a design credited to Michela Piancastelli's work,
which turned the classical SLD-resolution loop into a small set of states and transitions rather than a
recursive interpreter. That state-machine approach is a direct ancestor of the `:solve-classic` module
described in [Module map](../reference/module-map.md).

Because it was lightweight, embeddable, and easy to interoperate with, tuProlog ended up as the logic
engine underneath a number of research systems built at Bologna and elsewhere over the years, including
TuCSoN/ReSpecT (a coordination model for network-aware, mobile multi-agent systems), MoK (a
self-organising, tuple-space-based knowledge model), LPaaS (logic programming as a micro-service, aimed at
IoT/Cloud/Edge deployments), TuSoW (tuple-based coordination at the edge), and Arg2P (a tool for defeasible
argumentation). Each of these projects pushed tuProlog into environments and roles its original Java design
had not anticipated.

## Why a full rewrite

By the late 2010s, that same success had produced a codebase under strain. tuProlog had been developed and
extended by many hands over more than a decade, and it showed: accumulated cruft, design choices that made
sense under early-2000s Java but not any more, a lack of fine-grained tests that made refactoring risky, and
a lack of clean module boundaries and automated dependency management that made it hard to reuse just one
part of the system (say, the term representation) without pulling in the rest.

At the same time, the ambitions for the project had grown. tuProlog had always been *a Prolog solver*; what
the projects built on top of it increasingly needed was something more general: an open ecosystem for
**logic programming** at large — able to support other logics and inference styles besides classical Prolog
resolution, individually reusable, and portable to more than just the JVM (JavaScript-based agents and
browser-hosted reasoners were an explicit target). Patching those requirements onto the existing Java
codebase was judged less viable than starting over with a design built for them from day one — see
[Design Philosophy](philosophy.md) for how that translated into concrete architectural choices.

The rewrite adopted Kotlin specifically for its multiplatform story (see
[Why Kotlin Multiplatform](kotlin-multiplatform.md)) and started, according to the project's git history,
in April 2019 as "2p-in-kotlin" — the ecosystem now known as 2P-Kt.

## From rewrite to ecosystem

What began as a term/unification/resolution core has grown, module by module, into a considerably larger
ecosystem than plain tuProlog ever was. The project now ships around thirty incrementally dependent Gradle
modules (see [Module map](../reference/module-map.md)), and reached its `1.4.0` release at the time of
writing. Beyond classical SLD-resolution, it now includes a probabilistic logic programming stack
(`:solve-plp`, with a ProbLog-flavoured implementation backed by a multiplatform binary-decision-diagram
module), OR-concurrent resolution (`:solve-concurrent`), and both a command-line and a graphical front-end.
The name "tuProlog" is still used for the Prolog-specific parts of the ecosystem; "2P-Kt" is the name of the
whole, deliberately more-than-Prolog project.
