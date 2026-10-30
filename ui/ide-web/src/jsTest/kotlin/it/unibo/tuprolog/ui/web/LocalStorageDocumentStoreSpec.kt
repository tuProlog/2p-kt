package it.unibo.tuprolog.ui.web

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalStorageDocumentStoreSpec :
    FunSpec({
        test("a document name round-trips through its storage key") {
            val key = documentNameToStorageKey("family-tree.pl")
            assertEquals("family-tree.pl", storageKeyToDocumentName(key))
        }

        test("a key without the expected prefix is not a document name") {
            assertNull(storageKeyToDocumentName("unrelated-key"))
        }

        test("a written document can be read back and later deleted") {
            LocalStorageDocumentStore.write("scratch.pl", "p(1).")
            assertEquals("p(1).", LocalStorageDocumentStore.read("scratch.pl"))
            assertEquals(true, LocalStorageDocumentStore.list().contains("scratch.pl"))

            LocalStorageDocumentStore.delete("scratch.pl")
            assertNull(LocalStorageDocumentStore.read("scratch.pl"))
            assertEquals(false, LocalStorageDocumentStore.list().contains("scratch.pl"))
        }
    })
