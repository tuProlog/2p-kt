---
---

## Solver

In logic programming, resolution is the process of trying to find a solution to some goal by producing a suitable substitution, given a logic theory.

In 2P-Kt, this is provided by the `Solver` interface, which exposes a single method, `solve`. This method takes two arguments:

- `goal: Struct`, the goal we are trying to solve;
- `maxDuration: TimeDuration` (optional) which specifies the amount of time that is allowed to the `Solver` to complete its work. By default, it is set to be the highest possible value.

The `solve` method returns a `Sequence<Solution>`, which can be iterated upon to retrieve the execution outcomes, along with their possible variable bindings.

### Instantiating a Solver

A `Solver` can be created by using one of the factory methods accessible through its companion object. At the time being, such methods are:

- `classic()` creates a classic `Solver`;
- `classicWithDefaultBuiltins()` creates a classic solver with some predefined builtins already injected into it.

Each of these take quite a few optional parameters, which will be better described [later](#mutable-aspects-of-solvers) in this page. Also, the same methods come in a [mutable version](#mutable-solvers).

### Performing a query

The resolution process can be triggered by calling the `solve()` method on a `Solver` instance. The goal, which is passed as the `query` argument, can be any instance of `Struct`.

### Solutions

Each `Solution` returned by the `solve` method carries some information with it, which can be inspected programmatically. This information is contained in the following properties:

- `query: Struct`, the original goal which this solution refers to;
- `substitution: Substitution`, the bindings that have been applied to find the solution (or a failed substitution, when the solver could not find a suitable one);
- `solvedQuery: Struct?`, the `Struct` representing the solution, or `null` if none is available.

Depending on the outcome of the resolution process, each solution can belong to one of the following types:

- `Yes`, representing a successful solution;
- `No`, representing a failed solution;
- `Halt`, representing a solution that has failed due to some exception.

Also, the `isYes()`, `isNo()` and `isHalt()` properties can be used to query a `Solution` about its type.

### Exceptions

During the solving process, exceptions can occur. The Prolog standard defines many errors; in 2P-Kt such errors are defined in the implementation language with a one-to-one mapping.

`TuPrologRuntimeException` is the base type of all exceptions happening at run-time. Each instance carries three elements of information that help to understand in which circumstances the exception was raised:

- `message: String?`, a detailed string describing the exception;
- `cause: Throwable?`, what caused the exception;
- `contexts: Array<ExecutionContext>`, a stack of contexts localising the exception.

Let's list some notable `TuPrologRuntimeException` subtypes:

- `PrologError`, the base class for all Standard Prolog errors;
- `HaltException`, denoting an error occured inside the engine during its execution, indicating that the resolution process should be halted;
- `TimeoutException`, raised when the maxixum duration allowed for execution has been exceeded, suggesting that the execution should be terminated.

The Prolog ISO standard states that when a Prolog exception cannot be handled properly, a `SystemError` - a sub-type of `PrologError` - should be raised. If even this exception cannot be handled, the resolution process is halted by means of a `HaltException`.

Further information about this topic can be found [at this page]({{ site.baseUrl }}/wiki/Rationale%20and%20Architecture/errors-and-exceptions).

## Mutable aspects of Solvers

Every `Solver` instance employs a number of different assets behind the scenes to run a resolution process:

- a set of libraries;
- a set of operators;
- a static and a dynamic knowledge base;
- a set of flags;
- an input channel and an output channel.

As the title suggests, these properties are considered as mutable in the sense that they are meant to change inside the engine _while_ the resolution is being performed. In fact, these objects are represented as immutable data structures, which can be modified at implementation language level by using [mutable solvers](#mutable-solvers).

### Libraries

A `Library` is a container for:

- operators,
- [Prolog theories]({{ site.baseUrl }}/wiki/Rationale%20and%20Architecture/clasuedb-theories-and-RETE) (in the form of `Theory`s, which define custom predicates),
- [primitives and functions]({{ site.baseUrl }}/wiki/Rationale%20and%20Architecture/primitives-and-functions).

Since more than one library could be loaded at the same time, clashes can occur between predicate indicators. In order to overcome them, we provide a `AliasedLibrary` type which encapsulates a custom alias name. This allows the solver to search for predicates with both their plain name and their fully qualified name (alias + indicator).

### Operators

An `Operator` is a basic Prolog concept. It is a structured term with an arity of 1 or 2, which can be written in prefix, infix or postfix notation without parentheses. All operators currently known by a library are contained in an `OperatorSet` object.

Each `Operator` is defined through the following elements:

- a name (`Atom`),
- a `Specifier`, defining its notation and associativity,
- an integer settings its priority.

`Specifier`s can only represent one of the following syntaxes:

|         | Left-associative | Non-associative | Right-associative |
|---------|:----------------:|:---------------:|:-----------------:|
| Prefix  |        n/a       |        `FX`       |         `FY`        |
| Infix   |        `YFX`       |       `XFX`       |        `XFY`        |
| Postfix |        `YF`        |        `XF`       |        n/a        |

The `F` indicates the *name* of the specifier, while the number of `X` and `Y` defines the *arity* of the specifier. `X` and `Y` also define the implicit *associativity* of the operands: `Y` means that the side in which it is located is associative, whereas `X` indicates that its side is non-associative.

Implicit associativity is very useful for avoiding parentheses when writing sub-expressions with the same operator. For example, if the `'x'` is left-associative (`YFX`) the expression `(1 + 2 + 3 + 4)` is evaluated as the term `'+'('+'('+'(1, 2), 3), 4)))`.

### Knowledge bases

A knowledge base - also known as _theory_  - contains the set of clauses which is used by the solver to prove that a goal is true. Each solver comprises two knowledge bases:

- a _static_ knowledge base, which cannot be modified at runtime;
- a _dynamic_ knowledge base, that allows clauses to be inserted or removed from it.

In 2P-Kt, knowledge bases can be created and manipulated via the `Theory` interface. A more thorough look on such API can be found [in the dedicated page]({{ site.baseUrl }}/wiki/Rationale%20and%20Architecture/clausedb-theories-and-RETE).

### Flags

In Prolog, a flag is an atom associated with a value that can be either defined by the implementation or the user. Each flag has a name, a value and a range of possible values.

In 2P-Kt, flags are usually read-only properties, meaning that they are meant to act as static, global variables that can be accessed to retrieve information about the engine. For instance, one might want to check the maximum integer value that can be represented, the number of logical processors available, and so on.

In 2P-Kt solvers manage their flags via a container of type `FlagStore`, which is an immutable data structure mapping flag names to the respective `Term`s. Implementation-defined flags are conceived as sub-classes of the `NotableFlag` interface, which indeed exposes the following properties:

- `name: String`, the name of the flag;
- `defaultTerm: Term`, the default value associated to the flag;
- `admissibleValues: Sequence<Term>`, a collection of admissible values that the flag can carry.

### Channels

In 2P-Kt, the I/O interaction with a `Solver` is abstracted away in order to allow for a more flexible way of handling in- and out-going information.

This abstraction is quite straightforward: the `Channel` interface represents a generic I/O channel, and its children `InputChannel` and `OutputChannel` capture the separation between input and output.

Notice that each `Channel` is generic in the type of the elements that are been transmitted through it. This means that, in principle, any type of information can be handled, making the design more flexible. 

### Channel listeners

Each channel can be observed by registering one or more `Listener`s to it, which are just functions that are called each time an element passes through the channel. Like `Channel`s, `Listener`s also define a type parameter that determines the type of information that is being treated.

The `Channel` interface exposes a few methods that allow `Listener`s to be configured:

- `addListener(listener: Listener<T>)` registers a new `Listener`; 
- `removeListener(listener: Listener<T>)` removes a `Listener` from the `Solver`;
- `clearListeners()` removes all `Listener` from the `Solver.

### Channel storage

Each `Solver` can store multiple input and output channel. In order to organize them better, each solver collects them into two groups, of type `InputStore` and `OutputStore`, which map channel names to their actual instances.

### Default channels

Some useful channels are defined by default. These are declared in the `Channels` module as functions:

- `stdin(): InputChannel<String>` returns the standard input channel;
- `<T> stdout(): OutputChannel<T>` returns the standard output channel;
- `<T> stderr(): OutputChannel<T>` returns the standard error channel;
- `warning(): OutputChannel<PrologWarning>` returns the channel used to receive Prolog warnings from the engine.

These functions are conceived as platform-specific APIs, meaning that their actual implementation depends on the platform where the engine is being executed. This is done to enforce decoupling, thanks to Kotlin's support for [multiplatform programming](https://kotlinlang.org/docs/reference/multiplatform.html).

## Mutable solvers

