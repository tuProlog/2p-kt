# 2P in Kotlin

Some quick links:
* [GitLab Repository](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin) (the one used by developers)
* [GitHub Repository](https://github.com/tuProlog/2p-kt) (where JVM releases are hosted)
* [NPM Repository](https://www.npmjs.com/org/tuprolog) (where JS releases are hosted)
* [Maven Repository](https://bintray.com/pika-lab/tuprolog) (where all releases are hosted)
* [Documentation](http://pika-lab.gitlab.io/tuprolog/2p-in-kotlin/)

## Intro

![The 2P logo](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/raw/master/.img/logo.png)

[tuProlog](https://www.cs.nmsu.edu/ALP/2013/10/tuprolog-making-prolog-ubiquitous/) (2P henceforth) is a multi-paradigm 
logic programming framework written in Java.

2P-Kt is a Kotlin-based and multi-platform reboot of 2P.
It aims at becoming an open ecosystem for Symbolic Artificial Intelligence (AI).
For this reason, 2P-Kt consists of a number of incrementally inter-dependent modules aimed at supporting symbolic 
manipulation and reasoning in an extensible and flexible way.

A complete overview about modules and their dependencies is provided by the following diagram: 

![2P-Kt project map](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/raw/master/.img/project-map.png)

As shown in the project map, 2P-Kt currently focuses on supporting knowledge representation and automatic reasoning through logic programming, 
by featuring:

* a module for logic terms and clauses representation, namely `core`,

* a module for logic unification representation, namely `unify`,

* a module for in-memory indexing and storing logic theories, as well as other sorts of collections of logic clauses, namely `theory`,

* a module providing ISO Prolog resolution of logic queries, namely `solve`, coming with two implementations 
(i.e. `solve-classic` and `solve-streams`),
    
* a number of modules (i.e., the many `dsl-*` modules) supporting a Prolog-like, Domain Specific Language (DSL) 
aimed at bridging the logic programming with the Kotlin object-oriented \& functional environment,

* two parsing modules: one aimed at parsing terms, namely `parser-core`, and the other aimed at parsing theories, 
namely `parser-theory`,

* two serialisation-related modules: one aimed at (de)serialising terms and clauses, namely `serialize-core`, and the 
other aimed at  (de)serialising terms theories, namely `serialize-theory`,

* a module for using Prolog via a command-line interface, namely `repl`,

* a module for using Prolog via a graphical user interface (GUI), namely `ide`.
    
The modular, unopinionated architecture of 2P-Kt is deliberately aimed at supporting and encouraging extensions towards 
other sorts of symbolic AI systems than Prolog---such as ASP, tabled-Prolog, Problog, etc.

Furthermore, 2P-Kt is developed as in _pure_, __multi-platform__ Kotlin project. 
This brings two immediate advantages:
1. it virtually supports several platforms, there including JVM, JS, Android, and Native (even if, currently, only JVM, 
JS and Android are supported),
2. it consists of a very minimal and lightweight library, only leveraging on the Kotlin _common_ library, as it cannot 
commit to any particular platform standard library.

## Users
2P-Kt can either be used as a command-line program or as a Kotlin, JVM, Android, or JS library.

The 2P-Kt executables are currently available for download on the [Releases section](https://github.com/tuProlog/2p-kt/releases) of the
GitHub repository.

The 2P-Kt modules for JVM, Android, or Kotlin users are currently available for import 
on [Maven Central](https://search.maven.org/search?q=g:it.unibo.tuprolog), under the `it.unibo.tuprolog` group ID (not 
to be confused with the `it.unibo.alice.tuprolog`, which contains the old Java-based implementation).
The same modules are available through an _ad-hoc_ [Maven repository](https://bintray.com/pika-lab/tuprolog) as well, 
hosted by Bintray.

The 2P-Kt modules for JS users, are available for import on NPM, under the [`@tuprolog` organization](https://www.npmjs.com/org/tuprolog).

### End users

#### Graphical User Interface

If you need a GUI for your Prolog interpreter, you can rely on the 2P-Kt IDE which is available on the [Releases section of the 
GitHub repository](https://github.com/tuProlog/2p-kt/releases). 

The page of the [latest release](https://github.com/tuProlog/2p-kt/releases/latest) of 2P-Kt exposes a number of _Assets_.
There, the one named:
```
2p-ide-VERSION-redist.jar
```
is the self-contained, executable Jar containing the 2P-Kt-based Prolog interpreter (`VERSION` may vary depending on the
actual release version).

After you download the `2p-ide-VERSION-redist.jar`, you can simply launch it by running:
```bash
java -jar 2p-ide-VERSION-redist.jar
```
However, if you have properly configured the JVM on your system, it may be sufficient to just double-click on the 
aforementioned JAR to start the IDE.
In any case, running the JAR should make the following window appear:

![A screenshot of the 2P-Kt IDE](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/raw/master/.img/2p-kt-ide.png)

There, one may query the 2P-Kt Prolog interpreter against the currently opened theory file, which can of course be 
loaded from the user's file system by pressing <kbd>File</kbd> and then <kbd>Open...</kbd>.

To issue a query, the user must write it in the query text field, at the center of the application.
By either pressing <kbd>Enter</kbd> while the cursor is on the query text field, or by clicking on the <kbd>&gt;</kbd> button, the user can start a new resolution process, aimed at solving the provided query.
Further solutions can be explored by clicking on the <kbd>&gt;</kbd> over and over again.
One may also compute all the unexplored solutions at once by clicking on the <kbd>&gt;&gt;</kbd> button.

To perform a novel query, they user may either:
- write the new query in the query text field, and then press <kbd>Enter</kbd>, or
- click on the <kbd>R</kbd> (Reset) button, write the new query in the query text field, and then press <kbd>&gt;</kbd>.

Finally, users may inspect the current status of the solver by leveraging the many tabs laying at the bottom of the IDE.
There,
- the _Solutions_ tab is aimed at showing the Prolog interpreter's answers to the user's queries;
- the _Stdin_ tab is aimed at letting the user provide some text the Prolog interpreter's standard input stream;
- the _Stdout_ tab is aimed at showing the Prolog interpreter's standard output stream;
- the _Stderr_ tab is aimed at showing the Prolog interpreter's standard error stream;
- the _Warnings_ tab is aimed at showing any warning possibly generated by the Prolog interpreter while computing;
- the _Operators_ tab is aimed at showing the current content Prolog interpreter's operator table;
- the _Flags_ tab is aimed showing the actual values of all the flags currently defined with the Prolog interpreter;
- the _Libraries_ tab is aimed at letting the user inspect the currently loaded libraries and the predicates, operators, and functions they import;
- the _Static_ (resp. _Dynamic_) _KB_ tab is aimed at letting the user inspect the current content of the Prolog interpreter's static (resp. dynamic) knowledge base.

Any of these tabs may be automatically updated after a solution to some query is computed. 
Whenever something changes w.r.t. the previous content of the tab, an asterisk will appear close to the tab name, to notify an update in that tab.  
  
#### Command Line Interface

If you just need a command-line Prolog interpreter, you can rely on the 2P-Kt REPL which is available on the [Releases section of the 
GitHub repository](https://github.com/tuProlog/2p-kt/releases). 

The page of the [latest release](https://github.com/tuProlog/2p-kt/releases/latest) of 2P-Kt exposes a number of _Assets_.
There, the one named:
```
2p-repl-VERSION-redist.jar
```
is the self-contained, executable Jar containing the 2P-Kt-based Prolog interpreter (`VERSION` may vary depending on the
actual release version).

After you download the `2p-repl-VERSION-redist.jar`, you can simply launch it by running:
```bash
java -jar 2p-repl-VERSION-redist.jar
```
This should start an interactive read-eval-print loop accepting Prolog queries.
A normal output should be as follows:
```
# 2P-Kt version LAST_VERSION_HERE

?- <write your dot-terminated Prolog query here>.
```

Other options or modes of execution are supported.
One can explore them via the program help, which can be displayed by running:
```bash
java -jar 2p-repl-VERSION-redist.jar --help
```
This should display a message similar to the following one:
```
Usage: java -jar 2p-repl.jar [OPTIONS] COMMAND [ARGS]...

  Start a Prolog Read-Eval-Print loop

Options:
  -T, --theory TEXT  Path of theory file to be loaded
  -t, --timeout INT  Maximum amount of time for computing a solution (default:
                     1000 ms)
  -h, --help         Show this message and exit

Commands:
  solve  Compute a particular query and then terminate
```

### Gradle users

To import the 2P-Kt module named `2P_MODULE` (version `2P_VERSION`) into your Kotlin-based project leveraging on Gradle, 
you simply need to declare the corresponding dependency in your `build.gradle(.kts)` file:
 ```kotlin
// assumes Gradle's Kotlin DSL
dependencies {
    implementation("it.unibo.tuprolog", "2P_MODULE", "2P_VERSION")
}
 ``` 
In this way, the dependencies of `2P_MODULE` should be automatically imported. 

The step above, requires you to tell Gradle to either use Maven Central or our Bintray repository (or both) as a source 
for dependency lookup. You can do it as follows:
```kotlin
// assumes Gradle's Kotlin DSL
repositories {
    mavenCentral()
    maven("https://dl.bintray.com/pika-lab/tuprolog/")
}
``` 

#### JVM-only projects with Gradle

Remember to add the `-jvm` suffix to `2P_MODULE` in case your project only targets the JVM platform:
 ```kotlin
// assumes Gradle's Kotlin DSL
dependencies {
    implementation("it.unibo.tuprolog", "2P_MODULE-jvm", "2P_VERSION")
}
 ``` 

### Maven users

To import the 2P-Kt module named `2P_MODULE` (version `2P_VERSION`) into your Kotlin-based project leveraging on Maven,
you simply need to declare the corresponding dependency in your `pom.xml` file:
 ```xml
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>2P_MODULE</artifactId>
    <version>2P_VERSION</version>
</dependency>
 ``` 
In this way, the dependencies of `2P_MODULE` should be automatically imported. 

The step above, requires you to tell Maven to either use Maven Central or our Bintray repository (or both) as a source 
for dependency lookup. You can do it as follows:
```xml
<repositories>
    <repository>
        <id>bintray-2p-repo</id>
        <url>https://dl.bintray.com/pika-lab/tuprolog/</url>
    </repository>
</repositories>
``` 


#### JVM-only projects with Maven

Remember to add the `-jvm` suffix to `2P_MODULE` in case your project only targets the JVM platform:
 ```xml
<dependency>
    <groupId>it.unibo.tuprolog</groupId>
    <artifactId>2P_MODULE-jvm</artifactId>
    <version>2P_VERSION</version>
</dependency>
 ``` 

### NPM users (JavaScript-only projects)

The 2P-Kt software is available as a JavaScript library as well, on NPM, under the  [`@tuprolog` organization](https://www.npmjs.com/org/tuprolog).
To import the `2P_MODULE` into your `package.json`, it is sufficient to declare your dependency as follows:
```json
{
  "dependencies": {
    "@tuprolog/2P_MODULE": "^2P_MODULE_VERSION"
  }
}
```
Notice that the JS dependencies of `2P_MODULE` should be automatically imported. 

## Developers

Working with the 2P-Kt codebase requires a number of tools to be installed and properly configured on your system:
- JDK 12+ (please ensure the `JAVA_HOME` environment variable is properly) configured
- Kotlin 1.3.72+
- Gradle 6.4+ (please ensure the `GRADLE_HOME` environment variable is properly configured)
- Git 2.20+

### Develop 2P-Kt with IntelliJ Idea

To participate in the development of 2P-Kt, we suggest the [IntelliJ Idea](https://www.jetbrains.com/idea/download/) IDE. 
The free, _Community_ version will be fine. 

#### Recommended configuration
You will need the __Kotlin__ plugin for IntelliJ Idea. 
This is usually installed upon Idea's very first setup wizard.
However, one may easily late-install such plugin through the IDE's Plugins settings dialog.
To open such dialog, use <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>A</kbd>, then search for "Plugins"

#### Importing the project

1. Clone this repository in a folder of your preference using `git clone` appropriately

0. Open IntellJ Idea. 
If a project opens automatically, select "Close project". 
You should be on the welcome screen of IntelliJ idea, with an aspect similar to this image: 
![IntelliJ Welcome Screen](https://www.jetbrains.com/help/img/idea/2018.2/ideaWelcomeScreen.png)

0. Select "Import Project"

0. Navigate your file system and find the folder where you cloned the repository. 
**Do not select it**. 
Open the folder, and you should find a lowercase `2p-in-kotlin` folder. 
That is the correct project folder, created by `git` in case you cloned without specifying a different folder name. 
Once the correct folder has been selected, click <kbd>Ok</kbd>

0. Select "Import Project from external model"

0. Make sure "Gradle" is selected as external model tool

0. Click <kbd>Finish</kbd>

0. If prompted to override any `.idea` file, try to answer <kbd>No</kbd>. It's possible that IntelliJ refuses to proceed, in which case click <kbd>Finish</kbd> again, then select <kbd>Yes</kbd>

0. A dialog stating that "IntelliJ IDEA found a Gradle build script" may appear, in such case answer <kbd>Import Gradle Project</kbd>

0. Wait for the IDE to import the project from Gradle. The process may take several minutes, due to the amount of dependencies. Should the synchronization fail, make sure that the IDE's Gradle is configured correctly:

0. In 'Settings -> Build, Execution, Deployment -> Build Tools > Gradle', for the option 'Use Gradle from' select 'gradle-wrapper.properties file'. Enabling auto-import is also recommended

### Developing the project
Contributions to this project are welcome. Just some rules:

* We use [git flow](https://github.com/nvie/gitflow), so if you write new features, please do so in a separate `feature/` branch

* We recommend forking the project, developing your stuff, then contributing back via pull request directly from the Web interface

* Commit often. Do not throw pull requests with a single giant commit adding or changing the whole world. Split it in multiple commits and request a merge to the mainline often

* Stay in sync with the `develop` branch: pull often from `develop` (if the build passes), so that you don't diverge too much from the main development line

* Do not introduce low quality or untested code. Merge requests will be reviewed before merge.


#### Building the project
While developing, you can rely on IntelliJ to build the project, it will generally do a very good job.
If you want to generate the artifacts, you can rely on Gradle. Just point a terminal on the project's root and issue

```bash
./gradlew build
```

This will trigger the creation of the artifacts the executions of the tests, the generation of the documentation and of the project reports.

#### Versioning

The 2P project leverages on [Semantic Versioning](https://semver.org/) (SemVer, henceforth).

In particular, SemVer is enforced by the current Gradle configuration, which features [DanySK](https://github.com/DanySK)'s [Git sensitive SemVer Gradle Plugin](https://github.com/DanySK/git-sensitive-semantic-versioning-gradle-plugin).
This implies it is strictly forbidden in this project to create tags whose label is not a valid SemVar string.

Notice that the 2P project is still in its initial development stage---as proven by the major number equal to `0` in its version string.
According to SemVer, this implies anything _may_ change at any time, as the public API _should not_ be considered stable.

#### Issue tracking

If you meet some problem in using or developing 2P, you are encouraged to signal it through the project ["Issues" section](https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/issues) on GitLab.