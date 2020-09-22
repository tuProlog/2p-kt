import it.unibo.tuprolog.serialize.MimeType
import it.unibo.tuprolog.serialize.TheorySerializer
import it.unibo.tuprolog.solve.stdlib.CommonRules

fun main() {
    println(TheorySerializer.of(MimeType.Json).serialize(CommonRules.theory))
}
