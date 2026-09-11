package it.unibo.tuprolog.ui.web

import kotlinx.browser.localStorage

private const val KEY_PREFIX = "tuprolog-fs:"

/** The `localStorage` provider id used for [it.unibo.tuprolog.ui.gui.model.DocumentOrigin.providerId]. */
internal const val LOCAL_STORAGE_PROVIDER: String = "local-storage"

internal fun documentNameToStorageKey(name: String): String = "$KEY_PREFIX$name"

internal fun storageKeyToDocumentName(key: String): String? =
    key
        .takeIf {
            it.startsWith(KEY_PREFIX)
        }?.removePrefix(KEY_PREFIX)

/** Simulates a filesystem of named Prolog documents using the browser's `localStorage`. */
object LocalStorageDocumentStore {
    fun list(): List<String> =
        (0 until localStorage.length)
            .mapNotNull { localStorage.key(it) }
            .mapNotNull(::storageKeyToDocumentName)
            .sorted()

    fun read(name: String): String? = localStorage.getItem(documentNameToStorageKey(name))

    fun write(
        name: String,
        text: String,
    ) = localStorage.setItem(documentNameToStorageKey(name), text)

    fun delete(name: String) = localStorage.removeItem(documentNameToStorageKey(name))
}
