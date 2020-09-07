package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.rule.*
import it.unibo.tuprolog.theory.Theory

object CommonRules {
    val wrappers: Sequence<RuleWrapper<ExecutionContext>> = sequenceOf(
        Not,
        Arrow,
        Semicolon.If.Then,
        Semicolon.If.Else,
        Semicolon.Or.Left,
        Semicolon.Or.Right,
        Member.Base,
        Member.Recursive,
        Append.Base,
        Append.Recursive,
        Once
    )

    val theory: Theory
        get() = Theory.indexedOf(wrappers.map { it.wrappedImplementation })
}