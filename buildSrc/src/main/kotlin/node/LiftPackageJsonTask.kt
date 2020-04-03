package node

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileReader
import java.io.FileWriter

open class LiftPackageJsonTask : DefaultTask() {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private var packageJson: PackageJson? = null
    private lateinit var packageJsonRaw: JsonObject

    internal var  liftingActions: List<Action<PackageJson>> = emptyList()
        @Internal
        get() = field
        set(value) {
            field = value
        }

    internal var rawLiftingActions: List<Action<JsonObject>> = emptyList()
        @Internal
        get() = field
        set(value) {
            field = value
        }

    @Internal
    lateinit var packageJsonFile: File

    @TaskAction
    fun lift() {
        if (!packageJsonFile.exists()) {
            error("File ${packageJsonFile.path} does not exist")
        }
        resolve()
        performLifting()
        save()
    }

    private fun resolve() {
        packageJsonRaw = gson.fromJson(FileReader(packageJsonFile), JsonObject::class.java)
        packageJson = try {
            PackageJson.fromJson(packageJsonRaw)
        } catch (_: Throwable) {
            System.err.println("Cannot parse $packageJsonFile as a data class, use raw lifting")
            null
        }
    }

    private fun performLifting() {
        if (packageJson == null) {
            rawLiftingActions.forEach { it.execute(packageJsonRaw) }
        } else {
            liftingActions.forEach { it.execute(packageJson!!) }
        }
    }

    private fun save() {
        FileWriter(packageJsonFile).use {
            gson.toJson(packageJson?.toJson() ?: packageJsonRaw, it)
        }
    }
}