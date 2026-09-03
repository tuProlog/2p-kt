package it.unibo.tuprolog.solve.libs.io

import okio.FileSystem
import okio.Path
import okio.Sink
import okio.Source

/**
 * The [FileSystem] used for local files in production: [FileSystem.SYSTEM] on JVM,
 * `NodeJsFileSystem` on JS/Node.
 */
internal expect val platformFileSystem: FileSystem

/**
 * Internal seam through which `io-lib` accesses the local file system, defaulting to
 * [platformFileSystem] but swappable (e.g. with Okio's `FakeFileSystem`) by tests.
 */
internal object LocalFileSystem {
    var fileSystem: FileSystem = platformFileSystem

    fun source(path: Path): Source = fileSystem.source(path)

    fun sink(
        path: Path,
        mustCreate: Boolean = false,
    ): Sink = fileSystem.sink(path, mustCreate)

    fun appendingSink(
        path: Path,
        mustExist: Boolean = false,
    ): Sink = fileSystem.appendingSink(path, mustExist)

    fun exists(path: Path): Boolean = fileSystem.exists(path)
}
