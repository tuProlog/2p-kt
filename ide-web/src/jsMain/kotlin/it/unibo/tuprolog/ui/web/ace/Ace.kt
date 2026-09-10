package it.unibo.tuprolog.ui.web.ace

import org.w3c.dom.Element

/**
 * Entry point of the `ace-builds` package (https://ace.c9.io/), adapted from
 * https://github.com/daemontus/kotlin-ace-wrapper for the current `ace-builds` distribution.
 *
 * Ace's own module system predates ES/CommonJS modules and assumes a single shared global `ace` object
 * populated by loading its core script once; bundling it through webpack (as the legacy wrapper's AMD-style
 * `@JsModule` externals did) fights that assumption. Instead, `ace.js` and `theme-github.js` (vendored from
 * `ace-builds/src-min-noconflict`, see build.gradle.kts) are loaded as plain global `<script>` tags before
 * `ide-web.js` (see index.html), so this binds to the `window.ace` global those scripts create rather than
 * importing a JS module.
 */
@JsName("ace")
external object Ace {
    fun edit(element: Element): AceEditor
}
