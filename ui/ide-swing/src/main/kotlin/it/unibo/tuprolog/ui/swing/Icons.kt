package it.unibo.tuprolog.ui.swing

import java.awt.Image
import javax.swing.Icon
import javax.swing.ImageIcon

/** Icon size used in menu items and lower tabs. */
private const val MENU_ICON_SIZE = 16

/** Icon size used in the main toolbar buttons. */
private const val TOOLBAR_ICON_SIZE = 24

/** Icon size used inside the solutions tree (query/result/detail rows). */
private const val TREE_ICON_SIZE = 12

/** Loads `/icons/$name.png` (see `wireIdeIconsResource` in buildSrc) and scales it to a [size]x[size] icon. */
private fun bitmapIcon(
    name: String,
    size: Int,
): Icon {
    val resource =
        requireNotNull(Icons::class.java.getResource("/icons/$name.png")) {
            "missing bundled icon resource /icons/$name.png"
        }
    val image = ImageIcon(resource).image.getScaledInstance(size, size, Image.SCALE_SMOOTH)
    return ImageIcon(image)
}

/**
 * One icon per ide-swing (and ide-plp-swing) functionality - menu items, toolbar buttons, and lower tabs -
 * bundled under `.img/ide-icons` and shipped via buildSrc's `wireIdeIconsResource`.
 */
object Icons {
    // File menu
    val NEW: Icon = bitmapIcon("new", MENU_ICON_SIZE)
    val NEW_FROM_TEMPLATE: Icon = bitmapIcon("new-from-template", MENU_ICON_SIZE)
    val OPEN: Icon = bitmapIcon("open", MENU_ICON_SIZE)
    val CLOSE_PAGE: Icon = bitmapIcon("close-page", MENU_ICON_SIZE)
    val SAVE: Icon = bitmapIcon("save", MENU_ICON_SIZE)
    val SAVE_AS: Icon = bitmapIcon("save-as", MENU_ICON_SIZE)
    val RELOAD: Icon = bitmapIcon("reload", MENU_ICON_SIZE)
    val FILE_PROPERTIES: Icon = bitmapIcon("file-properties", MENU_ICON_SIZE)
    val QUIT: Icon = bitmapIcon("quit", MENU_ICON_SIZE)

    // Edit menu
    val CUT: Icon = bitmapIcon("cut", MENU_ICON_SIZE)
    val COPY: Icon = bitmapIcon("copy", MENU_ICON_SIZE)
    val PASTE: Icon = bitmapIcon("paste", MENU_ICON_SIZE)
    val SELECT_ALL: Icon = bitmapIcon("select-all", MENU_ICON_SIZE)

    // Search menu
    val FIND: Icon = bitmapIcon("find", MENU_ICON_SIZE)
    val REPLACE: Icon = bitmapIcon("replace", MENU_ICON_SIZE)
    val GO_TO_LINE: Icon = bitmapIcon("go-to-line", MENU_ICON_SIZE)

    // Help menu
    val ABOUT: Icon = bitmapIcon("about", MENU_ICON_SIZE)
    val REPORT_ISSUE: Icon = bitmapIcon("report-issue", MENU_ICON_SIZE)

    // Toolbar buttons
    val SOLVE: Icon = bitmapIcon("solve", TOOLBAR_ICON_SIZE)
    val SOLVE_10: Icon = bitmapIcon("solve10", TOOLBAR_ICON_SIZE)
    val SOLVE_100: Icon = bitmapIcon("solve100", TOOLBAR_ICON_SIZE)
    val SOLVE_ALL: Icon = bitmapIcon("solve-all", TOOLBAR_ICON_SIZE)
    val STOP: Icon = bitmapIcon("stop", TOOLBAR_ICON_SIZE)
    val RESET: Icon = bitmapIcon("reload", TOOLBAR_ICON_SIZE)
    val CLEAR: Icon = bitmapIcon("clear", TOOLBAR_ICON_SIZE)

    // Lower tabs
    val STDIN: Icon = bitmapIcon("input", MENU_ICON_SIZE)
    val STDOUT: Icon = bitmapIcon("output", MENU_ICON_SIZE)
    val STDERR: Icon = bitmapIcon("errors", MENU_ICON_SIZE)
    val WARNINGS: Icon = bitmapIcon("warnings", MENU_ICON_SIZE)
    val DIAGNOSTICS: Icon = bitmapIcon("diagnostics", MENU_ICON_SIZE)
    val OPERATORS: Icon = bitmapIcon("operators", MENU_ICON_SIZE)
    val FLAGS: Icon = bitmapIcon("flags", MENU_ICON_SIZE)
    val LIBRARIES: Icon = bitmapIcon("libraries", MENU_ICON_SIZE)
    val STATIC_KB: Icon = bitmapIcon("static-kb", MENU_ICON_SIZE)
    val DYNAMIC_KB: Icon = bitmapIcon("dynamic-kb", MENU_ICON_SIZE)
    val SOLUTIONS: Icon = bitmapIcon("solutions", MENU_ICON_SIZE)

    // Solutions tree rows
    val QUERY: Icon = bitmapIcon("query", TREE_ICON_SIZE)
    val YES_SOLUTION: Icon = bitmapIcon("yes-solution", TREE_ICON_SIZE)
    val NO_SOLUTION: Icon = bitmapIcon("no-solution", TREE_ICON_SIZE)
    val HALT_SOLUTION: Icon = bitmapIcon("halt-solution", TREE_ICON_SIZE)
}
