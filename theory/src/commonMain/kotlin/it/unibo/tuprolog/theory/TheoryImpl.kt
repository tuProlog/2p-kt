package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term

//class TheoryImpl : Theory {
//
//    private val rete: ReteMap
//
//    override val clauses: List<Clause>
//    override val rules: List<Rule>
//
//    constructor(clauses: List<Clause>) {
//        this.clauses = clauses
//        this.rules = clauses.filterIsInstance<Rule>()
//    }
//
//    companion object {
//        private fun initialiseRete(rules: List<Rule>): ReteMap {
//            val rete = mutableMapOf<ReteKey, Clauses>()
//            for (rule in rules) {
//                with(rule.head) {
//                    rete[Triple(functor, arity, rule.args)]
//                }
//            }
//        }
//    }
//}