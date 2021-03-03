---
---

# Errors and exceptions

<!-- tech stack diagram w/ related errors -->

The Prolog language has exceptions. 2P-Kt's design aims at defining them in the implementation language with a one-to-one mapping.

In 2P-Kt, exceptions are divided into three main categories, which will be better described in the following sections:

- *Prolog-level errors*, as defined by the [ISO standard](http://www.gprolog.org/manual/html_node/gprolog020.html);
- *Run-time exceptions*, thrown at state-machine level during the resolution process;
- *2P-Kt exceptions*, thrown at the implementation language level.

## 2P-Kt exceptions

Exceptions in 2P-Kt are all implemented by extending the `TuPrologException` class, which in turn extends Kotlin's `RuntimeException`.

This type defines two properties:

- `message: String?` the detailed error message,
- `cause: Throwable?` the cause of the exception.

## Run-time exceptions

Run-time exceptions happen during `Solver` executions. They are defined by `TuPrologException`'s subclass `TuPrologRuntimeException` which adds the `contexts: Array<ExecutionContext>` property, describing the stack of contexts that localize the exception in the execution flow.

## Prolog-level errors

These kind of errors occur when something goes wrong at Prolog language level. Their definitions and properties are described by an [ISO standard](http://www.gprolog.org/manual/html_node/gprolog020.html), which explains their meaning and when they should be raised. Each Prolog implementation is expected to support them, and 2P-Kt strives to do so.

In 2P-Kt, these errors are defined as subclasses of the `PrologError` type, which extends `TuPrologRuntimeException`. `PrologError` defines two extra properties:

- `type: Struct` describing the error structure,
- `extraData: Term?` which can contain any arbitrary implementation-dependant data.

`PrologError` instances can be created using its companion object factory method `of` method, which creates the correct instance of the error according to the given `type` structure. If it cannot recognise a corrisponding error, an anonymous `PrologError` instance is created instead.

### Supported errors

At the time being, 2P-Kt supports the following Prolog errors:

- `DomainError` occurs when something has the correct type but the value is not admissible
- `EvaluationError` occurs when some problem occurs in evaluating an arithmetic expression
- `ExistenceError` occurs when an object on which an operation is to be performed does not exist
- `InstantiationError` occurs when some Term is a Variable, and it should not
- `MessageError` used whenever no other `PrologError` instance is suitable for representing the error
- `PermissionError` occurs when an attempt to perform a prohibited operation is made
- `RepresentationError` occurs when an implementation limit has been reached
- `SyntaxError` occurs when there is a parsing failure
- `SystemError` occurs when an internal problem has occurred; if it is not caught, it will halt inferential machine
- `TypeError` occurs when something is not of `Expected` type