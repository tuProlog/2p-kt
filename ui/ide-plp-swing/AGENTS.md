# ui/ide-plp-swing — agent notes

For the probabilistic-solving flag that feeds this module's Probability/BDD tabs, see
`../gui-plp/AGENTS.md`. For the general rule against `graphviz-java`/GraalVM, see root `AGENTS.md`.

## BDD graph rendering: the graphviz-java incident

BDD-diagram rendering (`PlantUmlSwingBddGraphRenderer.kt` + `DotToPlantUml.kt`, which converts the BDD's DOT
text into an equivalent PlantUML state-diagram source) intentionally does **not** use
`guru.nidi:graphviz-java`. An earlier version (`GraphvizSwingBddGraphRenderer`) did, and its bundled
GraalVM-JS engine (`org.graalvm.js:js:23.0.13`) crashed on every JDK from 22 up with:

```
java.lang.NoSuchMethodError: 'void sun.misc.Unsafe.ensureClassInitialized(java.lang.Class)'
	at ... GraphvizJdkEngine.tryGraal(GraphvizJdkEngine.java:41)
```

Only JDK 21 avoided the crash, but pinning the whole app to JDK 21 broke real (non-headless) app startup —
not an acceptable tradeoff for an app that has to run on whatever JDK the user has. Rendering was rewritten
to use PlantUML's own pure-JVM Smetana layout engine instead (`!pragma layout smetana`); no JDK pin is
needed anywhere in this module anymore.
