package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

private data class Tagged(
    override val tags: Map<String, Any> = emptyMap(),
) : Taggable<Tagged> {
    override fun replaceTags(tags: Map<String, Any>): Tagged = Tagged(tags)
}

private val Source = TagKey<String>("source")

class TagKeySpec :
    FunSpec({
        test("typed-get-and-set") {
            val tagged = Tagged().withTag(Source, "file.pl")
            tagged[Source] shouldBe "file.pl"
            tagged.tags shouldBe mapOf("source" to "file.pl")
            Tagged()[Source].shouldBeNull()
        }
    })
