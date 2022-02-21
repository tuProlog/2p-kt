import io.javalin.Javalin
import org.gradle.api.Task

abstract class MockServiceExtension(val startMockTask: Task, val stopMockTask: Task) {
    private var server: Javalin? = null

    @Suppress("NOTHING_TO_INLINE")
    private inline fun errorIfAlreadyStarted() {
        if (server != null) {
            error("Server already started")
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var port: Int = 8080
        set(value) {
            errorIfAlreadyStarted()
            field = value
        }

    private var setup: Javalin.() -> Unit = {}
        set(value) {
            errorIfAlreadyStarted()
            field = value
        }

    fun start() {
        errorIfAlreadyStarted()
        server = Javalin.create {
            it.enableDevLogging()
        }.also {
            it.setup()
        }.start(port)
    }

    fun stop() {
        server.let {
            require(it != null) { "Server has not started yet" }
            it.stop()
        }
        server = null
    }

    fun routes(action: Javalin.() -> Unit) {
        setup = action
    }
}
