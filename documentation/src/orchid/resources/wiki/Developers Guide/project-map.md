---
---

The 2P-Kt project is composed by several *modules*, providing several features in an incremental way.
Currently, it comprehends:

* a module for logic terms representation, namely `core`,

* a module for logic unification representation, namely `unify`,

* a module for in-memory indexing and storing logic theories, namely `theory`,

* a module providing ISO Prolog resolution of logic queries, namely `solve`, coming with two implementations 
(i.e. `solve-classic` and `solve-streams`),
    
* a number of modules (i.e., the many `dsl-*` modules) supporting a Prolog-like, Domain Specific Language (DSL) 
aimed at bridging the logic programming with the Kotlin object-oriented & functional environment,

* two parsing modules: one aimed at parsing terms, namely `parser-core`, and the other aimed at parsing theories, 
namely `parser-theory`.

A complete overview about modules and their dependencies is provided by the following diagram: 

![project-map class diagram]({{ site.baseUrl }}/assets/generated/project-map.svg)

All modules are currently available as pre-compiled Jars, through an _ad-hoc_ [Maven repository](https://bintray.com/pika-lab/tuprolog) 
for JVM and Kotlin users. 
NPM modules will be deployed soon, making 2P easily available for JS users as well.

### Gradle

To import the 2P-Kt module named `2P_MODULE` (version `2P_VERSION`) into your Gradle-based project, you must 
setup your Maven repositories first:
```kotlin
// assumes Gradle's Kotlin DSL
repositories {
    mavenCentral()
    maven("https://dl.bintray.com/pika-lab/tuprolog/")
}
``` 
and then declare the desired dependency:
 ```kotlin
// assumes Gradle's Kotlin DSL
dependencies {
    implementation("it.unibo.tuprolog", "2P_MODULE", "2P_VERSION")
}
 ``` 
Notice that dependencies of `2P_MODULE` should be automatically imported. 
So, for instance, by importing module `theory`, one will automatically import `unify`, and therefore `core`, as well.

### Maven

Similarly, to import the 2P-Kt module named `2P_MODULE` (version `2P_VERSION`) into your Maven-based project, 
you must setup your Maven repositories first:
```xml
<repositories>
    <repository>
        <id>bintray-2p-repo</id>
        <url>https://bintray.com/pika-lab/tuprolog</url>
    </repository>
</repositories>
``` 
and then declare the desired dependency:
 ```xml
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>2P_MODULE</artifactId>
    <version>2P_VERSION</version>
</dependency>
 ``` 
Notice that dependencies of `2P_MODULE` should be automatically imported. 