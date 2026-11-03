import it.unibo.tuprolog.serialize.MimeType
import it.unibo.tuprolog.serialize.TheorySerializer
import it.unibo.tuprolog.serialize.theoryCases

/** Prints every test theory in YAML, e.g. to write the expected text of a new case. */
fun main() {
    theoryCases.forEach {
        println("# ${it.id}")
        println(TheorySerializer.of(MimeType.Yaml).serialize(it.theory()))
    }
}
