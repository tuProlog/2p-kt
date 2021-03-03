---
---

# Errors and exceptions

<!-- tech stack diagram w/ related errors -->

The Prolog language has exceptions. 2P-Kt's design aims at defining them in the implementation language with a one-to-one mapping.

In 2P-Kt, exceptions are divided into three main categories, which will be better described in the following sections:

- *Prolog-level errors*, as defined by the [ISO standard](http://www.gprolog.org/manual/html_node/gprolog020.html);
- *Run-time exceptions*, thrown at state-machine level during the resolution process;
- *2P-Kt exceptions*, thrown at the implementation language level.

<!-- TuPrologRuntimeException -->
<!-- PrologWarning? -->

## 2P-Kt exceptions

Exceptions in 2P-Kt are all implemented by extending the `TuPrologException` class, which in turn extends Kotlin's `RuntimeException`.

This type defines two properties:

- `message: String?` the detailed error message,
- `cause: Throwable?` the cause of the exception.

## Run-time exceptions

## Prolog-level errors

These kind of errors occur when something goes wrong at Prolog language level. Their definitions and properties are described by an [ISO standard](http://www.gprolog.org/manual/html_node/gprolog020.html), which explains their meaning and when they should be raised. Each Prolog implementation is expected to support them, and 2P-Kt strives to do so.

In 2P-Kt, these errors are defined as subclasses of the `PrologError` type, which extends `TuPrologRuntimeException`. `PrologError` defines two extra properties:

- `type: Struct` describing the error structure,
- `extraData: Term?` which can contain any arbitrary implementation-dependant data.

`PrologError` instances can be created using its companion object factory method `of` method, which creates the correct instance of the error according to the given `type` structure. If it cannot recognise a corrisponding error, an anonymous `PrologError` instance is created instead.
