import it.unibo.tuprolog.serialize.Instances
import it.unibo.tuprolog.serialize.MimeType
import it.unibo.tuprolog.serialize.TheorySerializer

fun main() {
    println(TheorySerializer.of(MimeType.Json).serialize(Instances.commonRules))
}
