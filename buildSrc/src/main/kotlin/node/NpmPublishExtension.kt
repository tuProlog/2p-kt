package node

import com.google.gson.JsonObject
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import java.io.File

open class NpmPublishExtension(objects: ObjectFactory) {

    companion object {

        const val NAME = "npmPublishing"

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

    val nodeRoot: Property<File> = objects.property()

    val nodeSetupTask: Property<String> = objects.property()

    val jsCompileTask: Property<String> = objects.property()

    val packageJson: Property<File> = objects.property()

    val token: Property<String>  = objects.property()

    val registry: Property<String>  = objects.property(String::class.java).also {
        it.set("registry.npmjs.org")
    }

    val node: Provider<File> = nodeRoot.map { nodeRoot ->
        possibleNodePaths.map { nodeRoot.resolve(it) }.first { it.exists() }
    }

    val npm: Provider<File> = nodeRoot.map { nodeRoot ->
        possibleNpmPaths.map { nodeRoot.resolve(it) }.first { it.exists() }
    }

    val npmProject: Provider<File> = packageJson.map { it.parentFile }

    val jsSourcesDir: Property<File>  = objects.property(File::class.java).also {
        File("build/")
    }


    internal val packageJsonLiftingActions = objects.listProperty<Action<PackageJson>>()

    internal val packageJsonRawLiftingActions = objects.listProperty<Action<JsonObject>>()

    internal val jsSourcesLiftingActions = objects.listProperty<FileLineTransformer>()

    fun liftPackageJson(action: Action<PackageJson>) {
        with(packageJsonLiftingActions) {
            add(action)
        }
    }

    fun liftPackageJsonRaw(action: Action<JsonObject>) {
        with(packageJsonRawLiftingActions) {
            add(action)
        }
    }

    fun liftJsSources(lineTransformer: FileLineTransformer) {
        with(jsSourcesLiftingActions) {
            add(lineTransformer)
        }
    }
}