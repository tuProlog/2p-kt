package node

import com.google.gson.JsonObject
import org.gradle.api.Action
import java.io.File

open class NpmPublishExtension {

    companion object {
        private val isWindows: Boolean
            get() = File.separatorChar == '\\'

        private val npmScriptSubpath = "node_modules/npm/bin/npm-cli.js"

        private val possibleNodePaths: Sequence<String> =
            sequenceOf("bin/node", "node").let { paths ->
                if (isWindows) {
                    paths.map { "$it.exe" } + paths
                } else {
                    paths
                }
            }

        private val possibleNpmPaths: Sequence<String> =
            sequenceOf("lib/", "").map { it + npmScriptSubpath }
    }

    internal var onExtensionChanged: MutableList<NpmPublishExtension.() -> Unit> = mutableListOf()

    var nodeRoot: File = File("")
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    var nodeSetupTask: String? = null
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    var jsCompileTask: String? = null
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    var packageJson: File = File("")
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    var token: String  = ""
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    var registry = "registry.npmjs.org"
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    internal val node: File
        get() = possibleNodePaths.map { nodeRoot.resolve(it) }.firstOrNull { it.exists() } ?: File("")

    internal val npm: File
        get() = possibleNpmPaths.map { nodeRoot.resolve(it) }.firstOrNull { it.exists() } ?: File("")

    internal val npmProject: File
        get() = packageJson.parentFile ?: File("")

    var jsSourcesDir: File = File("build/")
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    private val _packageJsonliftingActions: MutableList<Action<PackageJson>> = mutableListOf()
    private val _packageJsonRawLiftingActions: MutableList<Action<JsonObject>> = mutableListOf()
    private val _jsLiftingActions: MutableList<FileLineTransformer> = mutableListOf()

    internal val packageJsonLiftingActions: List<Action<PackageJson>>
        get() = _packageJsonliftingActions.toList()

    internal val packageJsonRawLiftingActions: List<Action<JsonObject>>
        get() = _packageJsonRawLiftingActions.toList()

    internal val jsSourcesLiftingActions: List<FileLineTransformer>
        get() = _jsLiftingActions.toList()

    fun liftPackageJson(action: Action<PackageJson>) {
        _packageJsonliftingActions.add(action)
        onExtensionChanged.forEach { it(this) }
    }

    fun liftPackageJsonRaw(action: Action<JsonObject>) {
        _packageJsonRawLiftingActions.add(action)
        onExtensionChanged.forEach { it(this) }
    }

    fun liftJsSources(lineTransformer: FileLineTransformer) {
        _jsLiftingActions.add(lineTransformer)
        onExtensionChanged.forEach { it(this) }
    }
}