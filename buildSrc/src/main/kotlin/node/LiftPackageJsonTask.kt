package node

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import warn
import java.io.File
import java.io.FileReader
import java.io.FileWriter

open class LiftPackageJsonTask : DefaultTask() {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val liftingActions: MutableList<Action<PackageJson>> = mutableListOf()
    private val rawLiftingActions: MutableList<Action<JsonObject>> = mutableListOf()
    private var packageJson: PackageJson? = null
    private lateinit var packageJsonRaw: JsonObject

    @InputFile
    lateinit var packageJsonFile: File

    val outputFile: File
        @OutputFile
        get() = packageJsonFile


    init {
        group = "nodejs"
        doFirst {
            resolve()
        }
        doLast {
            performLifting()
            save()
        }
    }

    private fun resolve() {
        if (!packageJsonFile.exists()) {
            error("File ${packageJsonFile.path} does not exist")
        }
        packageJsonRaw = gson.fromJson(FileReader(packageJsonFile), JsonObject::class.java)
        packageJson = try {
            PackageJson.fromJson(packageJsonRaw)
        } catch (_: Throwable) {
            warn("Cannot parse $packageJsonFile as a data class, use raw lifting")
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

    fun lift(action: Action<PackageJson>) {
        liftingActions.add(action)
    }

    fun liftRaw(action: Action<JsonObject>) {
        rawLiftingActions.add(action)
    }
}