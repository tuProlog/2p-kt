package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Covers `open/3,4` (Stream selection and control), against real temp files on the host file
 * system, exactly like [TestOpenLocalFile] does for [Url.openInputChannel]/[Url.openOutputChannel]
 * directly. A [okio.fakefilesystem.FakeFileSystem] can't be used here: `open/3,4` takes the
 * source/sink as a URL *string*, and round-tripping a bare path like "/parents.pl" through
 * [Url.file] and back goes through the JVM's real [java.io.File]/[java.net.URI] machinery
 * regardless of [LocalFileSystem.fileSystem] - which, on Windows, resolves a driveless absolute
 * path against the current drive (e.g. "C:\parents.pl"), so it would never match what a fake
 * filesystem stored under the driveless key.
 */
class OpenSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        val createdUrls = mutableListOf<Url>()

        fun setUp() {
            LocalFileSystem.fileSystem = platformFileSystem
        }

        fun tearDown() {
            createdUrls.forEach { LocalFileSystem.fileSystem.delete(it.toLocalPath(), mustExist = false) }
            createdUrls.clear()
        }

        fun tempFileUrl(prefix: String): Url {
            val name = "2p-kt-io-lib-test-open-$prefix-${Random.nextInt()}.tmp"
            val url = Url.file((FileSystem.SYSTEM_TEMPORARY_DIRECTORY / name).toString())
            createdUrls += url
            return url
        }

        fun solver() = PrologSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))

        beforeTest { setUp() }

        afterTest { tearDown() }

        test("open3-for-reading-then-get-char") {
            logicProgramming {
                val url = tempFileUrl("read")
                LocalFileSystem.fileSystem.write(url.toLocalPath()) { writeUtf8("qwerty") }

                val query =
                    "open"(url.toString(), "read", "S", logicListOf("alias"("mickey"))) and
                        ("get_char"("mickey", "C") and "close"("mickey"))
                val solution = solver().solve(query).toList().single()

                assertTrue(solution is Solution.Yes)
                assertEquals(Atom.of("q"), solution.valueOf("C"))
            }
        }

        test("open4-for-writing-then-put-char") {
            logicProgramming {
                val url = tempFileUrl("write")

                val query =
                    "open"(url.toString(), "write", "S", logicListOf("alias"("mickey"))) and
                        ("put_char"("mickey", "x") and "close"("mickey"))
                val solution = solver().solve(query).toList().single()
                assertTrue(solution is Solution.Yes)
                assertEquals("x", LocalFileSystem.fileSystem.read(url.toLocalPath()) { readUtf8() })
            }
        }

        test("open3-invalid-mode-is-domain-error") {
            logicProgramming {
                val url = tempFileUrl("invalid-mode")

                val query = "open"(url.toString(), "bogus_mode", "S")
                val solutions = solver().solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            DomainError.forArgument(
                                ctx,
                                Signature("open", 3),
                                DomainError.Expected.IO_MODE,
                                atomOf("bogus_mode"),
                                index = 1,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }

        test("open3-append-mode-adds-to-existing-content") {
            logicProgramming {
                val url = tempFileUrl("append")
                LocalFileSystem.fileSystem.write(url.toLocalPath()) { writeUtf8("ab") }

                val query =
                    "open"(url.toString(), "append", "S", logicListOf("alias"("mickey"))) and
                        ("put_char"("mickey", "c") and "close"("mickey"))
                val solution = solver().solve(query).toList().single()
                assertTrue(solution is Solution.Yes)
                assertEquals("abc", LocalFileSystem.fileSystem.read(url.toLocalPath()) { readUtf8() })
            }
        }

        test("open3-non-variable-stream-is-type-error") {
            logicProgramming {
                val url = tempFileUrl("non-var-stream")

                val query = "open"(url.toString(), "read", "already_bound")
                val solutions = solver().solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            TypeError.forArgument(
                                ctx,
                                Signature("open", 3),
                                TypeError.Expected.VARIABLE,
                                atomOf("already_bound"),
                                index = 2,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }

        test("open-on-missing-file-is-existence-error") {
            logicProgramming {
                val url = tempFileUrl("missing")
                for (query in listOf(
                    "open"(url.toString(), "read", "S"),
                    "open"(url.toString(), "read", "S", emptyLogicList),
                )) {
                    val solution = solver().solveOnce(query)
                    val error = assertIs<ExistenceError>((solution as? Solution.Halt)?.exception, "$solution")
                    assertEquals(ExistenceError.ObjectType.SOURCE_SINK, error.expectedObject)
                    assertEquals(atomOf(url.toString()), error.culprit)
                }
            }
        }
    })
