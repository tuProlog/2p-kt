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

## Mutable aspects of Solvers

### Libraries

### Knowledge bases

### Flags

### Operators

### Channels

## Mutable solvers

