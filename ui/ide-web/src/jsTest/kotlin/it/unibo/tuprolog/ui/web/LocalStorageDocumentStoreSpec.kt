package it.unibo.tuprolog.ui.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class LocalStorageDocumentStoreSpec :
    FunSpec({
        test("a document name round-trips through its storage key") {
            val key = documentNameToStorageKey("family-tree.pl")
            storageKeyToDocumentName(key) shouldBe "family-tree.pl"
        }

        test("a key without the expected prefix is not a document name") {
            storageKeyToDocumentName("unrelated-key").shouldBeNull()
        }

        test("a written document can be read back and later deleted") {
            LocalStorageDocumentStore.write("scratch.pl", "p(1).")
            LocalStorageDocumentStore.read("scratch.pl") shouldBe "p(1)."
            LocalStorageDocumentStore.list().contains("scratch.pl") shouldBe true

            LocalStorageDocumentStore.delete("scratch.pl")
            LocalStorageDocumentStore.read("scratch.pl").shouldBeNull()
            LocalStorageDocumentStore.list().contains("scratch.pl") shouldBe false
        }
    })
