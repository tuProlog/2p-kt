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
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Covers `open/3,4` (Stream selection and control), against an in-memory [FakeFileSystem] as done
 * by [TestLocalFileSystem] and [TestOpenLocalFile].
 */
class TestOpen {
    private lateinit var fake: FakeFileSystem
    private val ctx = DummyInstances.executionContext

    @BeforeTest
    fun setUp() {
        fake = FakeFileSystem()
        LocalFileSystem.fileSystem = fake
    }

    @AfterTest
    fun tearDown() {
        LocalFileSystem.fileSystem = platformFileSystem
    }

    private fun solver() = ClassicSolverFactory.solverWithDefaultBuiltins(otherLibraries = Runtime.of(IOLib))

    @Test
    fun testOpen3ForReadingThenGetChar() {
        logicProgramming {
            val path = "/parents.pl".toPath()
            fake.write(path) { writeUtf8("qwerty") }
            val url = Url.file(path.toString()).toString()

            val query = "open"(url, "read", "S", logicListOf("alias"("mickey"))) and "get_char"("mickey", "C")
            val solution = solver().solve(query).toList().single()

            assertTrue(solution is Solution.Yes)
            assertEquals(Atom.of("q"), solution.valueOf("C"))
        }
    }

    @Test
    fun testOpen4ForWritingThenPutChar() {
        logicProgramming {
            val path = "/written.txt".toPath()
            val url = Url.file(path.toString()).toString()

            val query =
                "open"(url, "write", "S", logicListOf("alias"("mickey"))) and
                    ("put_char"("mickey", "x") and "close"("mickey"))
            val solution = solver().solve(query).toList().single()
            assertTrue(solution is Solution.Yes)
            assertEquals("x", fake.read(path) { readUtf8() })
        }
    }

    @Test
    fun testOpen3InvalidModeIsDomainError() {
        logicProgramming {
            val path = "/parents.pl".toPath()
            fake.write(path) { writeUtf8("qwerty") }
            val url = Url.file(path.toString()).toString()

            val query = "open"(url, "bogus_mode", "S")
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
            val path = "/appended.txt".toPath()
            fake.write(path) { writeUtf8("ab") }
            val url = Url.file(path.toString()).toString()

            val query =
                "open"(url, "append", "S", logicListOf("alias"("mickey"))) and
                    ("put_char"("mickey", "c") and "close"("mickey"))
            val solution = solver().solve(query).toList().single()
            assertTrue(solution is Solution.Yes)
            assertEquals("abc", fake.read(path) { readUtf8() })
        }
    }

    @Test
    fun testOpen3NonVariableStreamIsTypeError() {
        logicProgramming {
            val path = "/parents.pl".toPath()
            fake.write(path) { writeUtf8("qwerty") }
            val url = Url.file(path.toString()).toString()

            val query = "open"(url, "read", "already_bound")
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
            val url = Url.file("/does/not/exist.pl").toString()
            val query = "open"(url, "read", "S")
            assertFailsWith<Throwable> { solver().solve(query).toList() }
        }
    }
}
