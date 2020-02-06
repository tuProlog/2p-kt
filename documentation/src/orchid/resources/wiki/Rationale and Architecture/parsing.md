#General introduction and differences between 2P-Java and 2P-Kotlin
##2P Java
tuProlog (2P-Java) is a Java project born with multi-platform ideas.
2P-Java is a Prolog system for distributed applications and it consists of several modules:
- **core**: module for logic representation and parser factory/visitor implementation. It includes
    terms logic, with all relative extension classes, clauses logic and theories.
- **parser**: module for parser specific classes and methods, ANTLR parser and lexer grammar
    and all dynamic features, with a direct dependency with `core` module.
- **runtime**: module to set theories and solve Prolog strings/terms at runtime with
    corba, rmi and tcp.
- **ui**: module for graphical interface using Java swing
The main purpose of this Java project is to offer the possibility to use Prolog engine
in different contests. We can use a simple graphical interface and manage strings in the Java World
and simplify all features that need a declarative paradigm.

##2P Kotlin
2P-Kotlin is a Kotlin is a multi-platform project that aims to extend and optimize 2P-Java. First, it was decided to refactor modules in a better way.
Let's start with main existing modules:
- **core**: such as 2P-Java, it includes logic representation; the difference with 2P-Java is that this module
    does not include specifics features logic (such as parser or theories), but only the main logic representation (like Term and all
    extension classes, Operator and exception). This module does not depend on any other module.
- **parser-jvm**: this module is like `parser` module in 2P-Java. It includes only specific JVM features, implementations and libraries.
    It contains ANTLR lexer and parser grammar, all dynamic features for those and it depends only on jdk implementation library. It depends only on
    jdk libraries and antlr features.
- **parser-js**: this module is like `parser-jvm` module, but specific for JavaScript. This is a 2P-Kotlin news, introduced 
    for interoperate with three different paradigms: declarative, functional and imperative. It depends only on antlr and js features.
- **parser-core**: this module is like a bridge between platform specific world and common world. Consists of three main submodules:
    a JVM specific module, a JS specific module and a common module (written in bare Kotlin). The JVM submodule depends on `parser-jvm`,
    ths JS submodule depends on `parser-js`, the common module depends on `core` and the test submodule depends on `solve-test`. The mapping established between the various worlds
    takes place through the concept of expect/actual Kotlin multi-platform feature. 
- **dsl-x**: this modules creates a Kotlin dsl to simplify interaction between Prolog world and the others. For example,
    to test parsing in `parser-core`, we used Prolog dsl (to parse "a :- b" we can simply use this notation "a" impliedBy "b")

The rationale for project management is to optimize dependencies: each module must do its job and must be specialized in a single thing.
Software engineering teaches us that the single responsibility principle helps to maintain the project and every change depends only on other strictly necessary modules.

    