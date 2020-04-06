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

    private var _nodeRoot: File = File("")
    var nodeRoot: File
        get() = _nodeRoot
        set(value) {
            _nodeRoot = value
            _node = null
            onExtensionChanged.forEach { it(this) }
        }

    private var _nodeSetupTask: String? = null
    var nodeSetupTask: String?
        get() = _nodeSetupTask
        set(value) {
            _nodeSetupTask = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _jsCompileTask: String? = null
    var jsCompileTask: String?
        get() = _jsCompileTask
        set(value) {
            _jsCompileTask = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _packageJson: File = File("")
    var packageJson: File
        get() = _packageJson
        set(value) {
            _packageJson = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _token: String = ""
    var token: String
        get() = _token
        set(value) {
            _token = value
            onExtensionChanged.forEach { it(this) }
        }

    var registry = "registry.npmjs.org"
        get() = field
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _node: File? = null
    internal val node: File
        get() {
            if (_node == null) {
                _node = possibleNodePaths.map { nodeRoot.resolve(it) }.find { it.exists() }
            }
            return _node ?: File("")
        }

    private var _npm: File? = null
    internal val npm: File
        get() {
            if (_npm == null) {
                _npm = possibleNpmPaths.map { nodeRoot.resolve(it) }.find { it.exists() }
            }
            return _npm ?: File("")
        }

    internal val npmProject: File
        get() = packageJson.parentFile ?: File("")

    private val _liftingActions: MutableList<Action<PackageJson>> = mutableListOf()
    private val _rawLiftingActions: MutableList<Action<JsonObject>> = mutableListOf()

    internal val liftingActions: List<Action<PackageJson>>
        get() = _liftingActions.toList()

    internal val rawLiftingActions: List<Action<JsonObject>>
        get() = _rawLiftingActions.toList()

    fun liftPackageJson(action: Action<PackageJson>) {
        _liftingActions.add(action)
        onExtensionChanged.forEach { it(this) }
    }

    fun liftPackageJsonRaw(action: Action<JsonObject>) {
        _rawLiftingActions.add(action)
        onExtensionChanged.forEach { it(this) }
    }
}