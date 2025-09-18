package it.unibo.tuprolog.solve.channel.impl

internal class InputChannelFromString(
    private val string: String,
) : AbstractInputChannel<String>() {
    private val iterator by lazy {
        string
            .lineSequence()
            .flatMap { it.toCharArray().asSequence() + sequenceOf('\n') }
            .map { "$it" }
            .iterator()
    }

    override fun readActually(): String? = if (iterator.hasNext()) iterator.next() else null
}
