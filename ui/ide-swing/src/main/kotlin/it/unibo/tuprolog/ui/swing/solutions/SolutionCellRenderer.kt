package it.unibo.tuprolog.ui.swing.solutions

import it.unibo.tuprolog.ui.swing.Icons
import java.awt.Component
import java.awt.Font
import javax.swing.Icon
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

/**
 * Renders each [SolutionNodeData] kind in the Solutions tree with its own icon and, for the ellipsis row, an
 * italic font.
 */
internal class SolutionCellRenderer : DefaultTreeCellRenderer() {
    // Some look-and-feels (observed with Aqua on macOS) paint a JTree's open/closed/leaf icon by calling the
    // renderer's own getOpenIcon()/getClosedIcon()/getLeafIcon() bean getters instead of (only) using the icon
    // set on the component getTreeCellRendererComponent() returns, silently substituting their native
    // folder/document glyphs otherwise (see https://stackoverflow.com/a/38994868). Overriding those getters to
    // echo back whatever icon was just computed for the current row keeps both painting paths in sync.
    //
    // Crucially, this field is never null: some look-and-feels' TreeUI query these getters once, at UI-install
    // time, before this renderer has ever rendered a real row - if that first read returns null, they read it
    // as "this tree has no icons at all" and stop reserving any icon space for the tree's entire lifetime, no
    // matter what a later row's getTreeCellRendererComponent() call sets `icon` to. Defaulting (and falling
    // back, for rows with no icon of their own) to a same-sized blank icon instead of null keeps row layout
    // consistent - and icons visible - regardless of which row a given look-and-feel happens to measure first.
    private var currentIcon: Icon = BLANK_TREE_ICON

    /** Picks this row's label, icon, and font from the [SolutionNodeData] its node wraps. */
    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean,
    ): Component {
        val data = (value as? DefaultMutableTreeNode)?.userObject
        val label =
            when (data) {
                is SolutionNodeData.QueryNode -> "?- ${data.query}"
                is SolutionNodeData.ResultNode -> data.label
                is SolutionNodeData.DetailNode -> data.text
                SolutionNodeData.EllipsisNode -> "…"
                else -> value.toString()
            }
        // Computed before the super call so that DefaultTreeCellRenderer's own internal
        // setIcon(getLeafIcon()/getOpenIcon()/getClosedIcon()) already sees this row's icon too, rather than
        // relying solely on the explicit `icon = currentIcon` below to correct it afterwards.
        currentIcon =
            when (data) {
                is SolutionNodeData.QueryNode -> Icons.QUERY
                is SolutionNodeData.ResultNode -> resultIcon(data.kind)
                // Detail rows (a binding, a stack trace line) and the ellipsis are indented plain text with
                // no icon of their own.
                else -> BLANK_TREE_ICON
            }
        super.getTreeCellRendererComponent(tree, label, selected, expanded, leaf, row, hasFocus)
        leafIcon = currentIcon
        openIcon = currentIcon
        closedIcon = currentIcon
        icon = currentIcon
        font = font.deriveFont(if (data is SolutionNodeData.EllipsisNode) Font.ITALIC else Font.PLAIN)
        return this
    }

    /** Echoes back [currentIcon] regardless of leaf/open/closed state - see the field's own comment for why. */
    override fun getLeafIcon(): Icon = currentIcon

    /** @see getLeafIcon */
    override fun getOpenIcon(): Icon = currentIcon

    /** @see getLeafIcon */
    override fun getClosedIcon(): Icon = currentIcon

    private fun resultIcon(kind: ResultKind): Icon =
        when (kind) {
            ResultKind.YES -> Icons.YES_SOLUTION
            ResultKind.NO -> Icons.NO_SOLUTION
            ResultKind.HALT -> Icons.HALT_SOLUTION
        }

    private companion object {
        val BLANK_TREE_ICON: Icon = BlankIcon(Icons.QUERY.iconWidth, Icons.QUERY.iconHeight)
    }
}
