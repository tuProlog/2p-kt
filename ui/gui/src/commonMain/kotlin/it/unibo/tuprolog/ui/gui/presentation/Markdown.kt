package it.unibo.tuprolog.ui.gui.presentation

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

private val flavour = CommonMarkFlavourDescriptor()

/** Renders [markdown] (CommonMark) as an HTML `<body>` fragment. */
fun markdownToHtml(markdown: String): String =
    HtmlGenerator(markdown, MarkdownParser(flavour).buildMarkdownTreeFromString(markdown), flavour).generateHtml()

/**
 * A one-line plain-text preview of [markdown]: its first paragraph that isn't just a code span (stdlib docs open
 * with a `` `usage(?Template)` `` line), stripped of backticks and truncated to [maxLength] with `…`.
 */
fun documentationPreview(
    markdown: String,
    maxLength: Int = 80,
): String {
    val paragraphs = markdown.split(Regex("\\n\\s*\\n")).map { it.trim() }.filter { it.isNotEmpty() }
    val paragraph = paragraphs.firstOrNull { !Regex("^`[^`]*`$").matches(it) } ?: paragraphs.firstOrNull() ?: ""
    val text = paragraph.replace("`", "").replace(Regex("\\s+"), " ")
    return if (text.length <= maxLength) text else text.take(maxLength - 1).trimEnd() + "…"
}
