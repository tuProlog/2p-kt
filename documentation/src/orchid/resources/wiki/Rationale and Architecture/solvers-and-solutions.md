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

During the solving process, exceptions can occur. The Prolog standard defines many errors; in 2p-Kt such errors are defined in the implementation language with a one-to-one mapping.

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

### Operators

### Knowledge bases

### Flags

### Channels

## Mutable solvers

