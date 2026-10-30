package it.unibo.tuprolog.solve.libs.io

object ExampleUrls {
    /** Set by `io-lib/build.gradle.kts`, which starts the mock service on a free port around the test tasks. */
    private val PORT = checkNotNull(env("MOCK_SERVICE_PORT")) { "MOCK_SERVICE_PORT is unset: run the tests via Gradle" }

    private val BASE_URL = "http://localhost:$PORT"

    val PARENTS = "$BASE_URL/parents.pl"

    val WRONG_PARENTS = "$BASE_URL/parents-wrong.pl"

    val HELLO = "$BASE_URL/hello"

    val RANDOM_BIN = "$BASE_URL/random.bin"
}
