package it.unibo.tuprolog.ui.web

import kotlinx.browser.localStorage

private const val KEY_PREFIX = "tuprolog-fs:"

/** The `localStorage` provider id used for [it.unibo.tuprolog.ui.gui.model.DocumentOrigin.providerId]. */
internal const val LOCAL_STORAGE_PROVIDER: String = "local-storage"

/** The `localStorage` key a document named [name] is stored under. */
internal fun documentNameToStorageKey(name: String): String = "$KEY_PREFIX$name"

/** The document name [key] was stored under, or `null` if [key] isn't one of this store's own keys. */
internal fun storageKeyToDocumentName(key: String): String? =
    key
        .takeIf {
            it.startsWith(KEY_PREFIX)
        }?.removePrefix(KEY_PREFIX)

/** Simulates a filesystem of named Prolog documents using the browser's `localStorage`. */
object LocalStorageDocumentStore {
    /** Every document name currently stored, sorted alphabetically. */
    fun list(): List<String> =
        (0 until localStorage.length)
            .mapNotNull { localStorage.key(it) }
            .mapNotNull(::storageKeyToDocumentName)
            .sorted()

    /** The stored text of document [name], or `null` if none is stored under that name. */
    fun read(name: String): String? = localStorage.getItem(documentNameToStorageKey(name))

    /** Stores [text] as the content of document [name], replacing whatever was there before. */
    fun write(
        name: String,
        text: String,
    ) = localStorage.setItem(documentNameToStorageKey(name), text)

    /** Removes document [name] from the store, if present. */
    fun delete(name: String) = localStorage.removeItem(documentNameToStorageKey(name))
}
