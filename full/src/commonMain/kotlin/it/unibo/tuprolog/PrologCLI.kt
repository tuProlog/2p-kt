@file:JvmName("PrologCLI")

package it.unibo.tuprolog

import kotlin.jvm.JvmName

/**
 * Command-line entry point of the `:full` module -- the "batteries-included" umbrella artifact that depends
 * on every other 2P-Kt module (except the test-support and `examples` ones -- see this module's
 * `build.gradle.kts`), so an embedder can pull in the whole framework through a single dependency instead of
 * picking individual modules one by one.
 *
 * Delegates, per platform, to `it.unibo.tuprolog.ui.repl`'s own `main` function (see the `actual`
 * implementations of this function), which builds [it.unibo.tuprolog.ui.repl.TuPrologCmd] with its `solve`
 * subcommand and hands it [args] -- i.e. this is the very same REPL CLI shipped standalone by the `:repl`
 * module, just reachable from a project that only depends on `:full`. See `TuPrologCmd`'s own KDoc for the
 * supported options and subcommands.
 *
 * The `@file:JvmName("PrologCLI")` file annotation (present on both this file and its JVM `actual`
 * counterpart) names the JVM class backing this file `PrologCLI` instead of the compiler-default
 * `PrologCLIKt`, so it can be invoked directly under that name, e.g.
 * `java -cp <classpath> it.unibo.tuprolog.PrologCLI [OPTIONS] [solve QUERY [-n N]]`.
 *
 * @param args command-line arguments, forwarded verbatim to `TuPrologCmd`'s clikt-based parser.
 */
expect fun main(args: Array<String>)
