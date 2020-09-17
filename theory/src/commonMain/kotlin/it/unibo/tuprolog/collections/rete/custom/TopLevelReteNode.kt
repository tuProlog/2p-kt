package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.core.Clause

/**Interface aimed at marking a particular member of the [ReteTree] as capable of enough
 * foreseeing to take strong decisions upon the actions to be performed on the subtrees it branches on
 */
internal interface TopLevelReteNode : ReteNode {

    /**Retracts the first matching occurrence of the given [Clause] from this [ReteTree]*/
    fun retractFirst(clause: Clause): Sequence<Clause>
}
