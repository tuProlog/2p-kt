## Solver

In logic programming, resolution is the process of trying to find a solution to some goal by producing a suitable substitution, given a logic theory.

In 2P-Kt, this is provided by the `Solver` interface, which exposes a single method, `solve`. This method takes two arguments:

- `goal: Struct`, the goal we are trying to solve;
- `maxDuration: TimeDuration` (optional) which specifies the amount of time that is allowed to the `Solver` to complete its work. By default, it is set to be the highest possible value.

The `solve` method returns a `Sequence<Solution>`, which can be iterated upon to retrieve the execution outcomes, along with their possible variable bindings.

### Performing a query

### Solutions

### Exceptions

## Mutable aspects of Solvers

### Libraries

### Knowledge bases

### Flags

### Operators

### Channels

## Mutable solvers

