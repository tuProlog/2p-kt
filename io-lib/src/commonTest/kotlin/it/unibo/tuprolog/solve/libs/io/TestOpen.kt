package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.library.Runtime
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
class TestOpen {
    private val ctx = DummyInstances.executionContext
    private val createdUrls = mutableListOf<Url>()

    @BeforeTest
    fun setUp() {
        LocalFileSystem.fileSystem = platformFileSystem
    }

    @AfterTest
    fun tearDown() {
        createdUrls.forEach { LocalFileSystem.fileSystem.delete(it.toLocalPath(), mustExist = false) }
        createdUrls.clear()
    }

    private fun tempFileUrl(prefix: String): Url {
        val name = "2p-kt-io-lib-test-open-$prefix-${Random.nextInt()}.tmp"
        val url = Url.file((FileSystem.SYSTEM_TEMPORARY_DIRECTORY / name).toString())
        createdUrls += url
        return url
    }

    private fun solver() = ClassicSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))

    @Test
    fun testOpen3ForReadingThenGetChar() {
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

    @Test
    fun testOpen4ForWritingThenPutChar() {
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

    @Test
    fun testOpen3InvalidModeIsDomainError() {
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

    @Test
    fun testOpen3AppendModeAddsToExistingContent() {
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

    @Test
    fun testOpen3NonVariableStreamIsTypeError() {
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

    @Test
    fun testOpen3OnMissingFileCrashesInsteadOfRaisingAnExistenceError() {
        // Known gap: Url.openInputChannel() does not catch the underlying file-system exception, so
        // it escapes as a raw platform exception (okio.FileNotFoundException on the JVM, this
        // library's own IOException on JS/Node) instead of the ISO
        // existence_error(source_sink, Source_sink) the standard mandates. This test pins the
        // current (non-compliant) behavior rather than silently masking it, without pinning the
        // exact (platform-dependent) exception type.
        logicProgramming {
            val url = tempFileUrl("missing")
            val query = "open"(url.toString(), "read", "S")
            assertFailsWith<Throwable> { solver().solve(query).toList() }
        }
    }
}
