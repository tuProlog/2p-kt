package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class ClauseManipulationSpec :
    FunSpec({
        val directive = Directive.of(Atom.of("a"), Atom.of("b")) // :- a, b.

        val fact = Fact.of(Struct.of("f", Integer.ONE)) // f(1).

        val rule = Rule.of(fact.head, directive.body) // f(1) :- a, b.

        test("set-head") {
            val otherHead = Struct.of("f", Integer.of(2))
            assertEquals(
                Rule.of(otherHead, directive.body),
                directive.setHead(otherHead),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.setHead(otherHead),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.setHead(otherHead),
            )
        }

        test("set-head-functor") {
            val otherHead = Struct.of("g", Integer.ONE)
            assertEquals(
                directive,
                directive.setHeadFunctor("g"),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.setHeadFunctor("g"),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.setHeadFunctor("g"),
            )
        }

        test("set-head-args") {
            val otherHead = Struct.of("f", Atom.of("a"), Atom.of("b"))
            assertEquals(
                directive,
                directive.setHeadArgs(*otherHead.args.toTypedArray()),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.setHeadArgs(*otherHead.args.toTypedArray()),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.setHeadArgs(*otherHead.args.toTypedArray()),
            )
        }

        test("add-last-head-arg") {
            val otherHead = Struct.of("f", Integer.ONE, Atom.of("b"))
            assertEquals(
                directive,
                directive.addLastHeadArg(otherHead.args.last()),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.addLastHeadArg(otherHead.args.last()),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.addLastHeadArg(otherHead.args.last()),
            )
        }

        test("append-head-arg") {
            val otherHead = Struct.of("f", Integer.ONE, Atom.of("b"))
            assertEquals(
                directive,
                directive.appendHeadArg(otherHead.args.last()),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.appendHeadArg(otherHead.args.last()),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.appendHeadArg(otherHead.args.last()),
            )
        }

        test("add-first-head-arg") {
            val otherHead = Struct.of("f", Atom.of("b"), Integer.ONE)
            assertEquals(
                directive,
                directive.addFirstHeadArg(otherHead.args.first()),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.addFirstHeadArg(otherHead.args.first()),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.addFirstHeadArg(otherHead.args.first()),
            )
        }

        test("insert-head-arg") {
            val otherHead = Struct.of("f", Atom.of("b"), Integer.ONE)
            assertEquals(
                directive,
                directive.insertHeadArg(0, otherHead[0]),
            )
            assertEquals(
                Rule.of(otherHead, rule.body),
                rule.insertHeadArg(0, otherHead[0]),
            )
            assertEquals(
                Fact.of(otherHead),
                fact.insertHeadArg(0, otherHead[0]),
            )
        }

        test("head-inspection") {
            assertEquals(
                rule.head.args,
                rule.headArgs.toList(),
            )
            assertEquals(
                fact.head.args,
                fact.headArgs.toList(),
            )
            assertEquals(
                rule.head.arity,
                rule.headArity,
            )
            assertEquals(
                fact.head.arity,
                fact.headArity,
            )

            for (i in 0 until rule.headArity) {
                assertEquals(rule.head[i], rule.getHeadArg(0))
            }

            for (i in 0 until fact.headArity) {
                assertEquals(fact.head[i], fact.getHeadArg(0))
            }
        }

        test("set-body") {
            val otherBody = Tuple.of(Integer.ONE, Integer.of(2), Integer.of(3))
            assertEquals(
                Directive.of(otherBody),
                directive.setBody(otherBody),
            )
            assertEquals(
                Rule.of(rule.head, otherBody),
                rule.setBody(otherBody),
            )
            assertEquals(
                Rule.of(fact.head, otherBody),
                fact.setBody(otherBody),
            )
        }

        test("set-body-items1") {
            val otherBody = Var.of("X")
            assertEquals(
                Directive.of(otherBody),
                directive.setBodyItems(otherBody),
            )
            assertEquals(
                Rule.of(rule.head, otherBody),
                rule.setBodyItems(otherBody),
            )
            assertEquals(
                Rule.of(fact.head, otherBody),
                fact.setBodyItems(otherBody),
            )
        }

        test("set-body-items-n") {
            Scope.empty {
                val otherBody = tupleOf(varOf("X"), varOf("Y"))
                assertEquals(
                    directiveOf(otherBody),
                    directive.setBodyItems(varOf("X"), varOf("Y")),
                )
                assertEquals(
                    ruleOf(rule.head, otherBody),
                    rule.setBodyItems(varOf("X"), varOf("Y")),
                )
                assertEquals(
                    ruleOf(fact.head, otherBody),
                    fact.setBodyItems(varOf("X"), varOf("Y")),
                )
            }
        }

        test("add-last-body-item") {
            val otherBody = Tuple.of(Atom.of("a"), Atom.of("b"), Atom.of("c"))
            assertEquals(
                Directive.of(otherBody),
                directive.addLastBodyItem(otherBody.unfoldedSequence.last()),
            )
            assertEquals(
                Rule.of(rule.head, otherBody),
                rule.addLastBodyItem(otherBody.unfoldedSequence.last()),
            )
            assertEquals(
                Rule.of(fact.head, Truth.TRUE, otherBody.unfoldedSequence.last()),
                fact.addLastBodyItem(otherBody.unfoldedSequence.last()),
            )
        }

        test("append-body-item") {
            val otherBody = Tuple.of(Atom.of("a"), Atom.of("b"), Atom.of("c"))
            assertEquals(
                Directive.of(otherBody),
                directive.appendBodyItem(otherBody.unfoldedSequence.last()),
            )
            assertEquals(
                Rule.of(rule.head, otherBody),
                rule.appendBodyItem(otherBody.unfoldedSequence.last()),
            )
            assertEquals(
                Rule.of(fact.head, Truth.TRUE, otherBody.unfoldedSequence.last()),
                fact.appendBodyItem(otherBody.unfoldedSequence.last()),
            )
        }

        test("add-first-body-item") {
            val otherBody = Tuple.of(Atom.of("c"), Atom.of("a"), Atom.of("b"))
            assertEquals(
                Directive.of(otherBody),
                directive.addFirstBodyItem(otherBody.left),
            )
            assertEquals(
                Rule.of(rule.head, otherBody),
                rule.addFirstBodyItem(otherBody.left),
            )
            assertEquals(
                Rule.of(fact.head, otherBody.left, Truth.TRUE),
                fact.addFirstBodyItem(otherBody.left),
            )
        }

        test("insert-body-item") {
            val otherBody = Tuple.of(Atom.of("c"), Atom.of("a"), Atom.of("b"))
            assertEquals(
                Directive.of(otherBody),
                directive.insertBodyItem(0, otherBody.left),
            )
            assertEquals(
                Rule.of(rule.head, otherBody),
                rule.insertBodyItem(0, otherBody.left),
            )
            assertEquals(
                Rule.of(fact.head, otherBody.left, Truth.TRUE),
                fact.insertBodyItem(0, otherBody.left),
            )
        }

        test("body-inspection") {
            assertEquals(
                directive.bodyAsTuple?.toList(),
                directive.bodyItems.toList(),
            )
            assertEquals(
                rule.bodyAsTuple?.toList(),
                rule.bodyItems.toList(),
            )
            assertEquals(
                fact.body,
                fact.bodyItems.single(),
            )
            assertEquals(
                directive.body.asTuple()?.size,
                directive.bodySize,
            )
            assertEquals(
                rule.body.asTuple()?.size,
                rule.bodySize,
            )
            assertEquals(
                1,
                fact.bodySize,
            )

            for (i in 0 until directive.bodySize) {
                assertEquals(
                    directive.bodyAsTuple
                        ?.unfoldedSequence
                        ?.drop(i)
                        ?.first(),
                    directive.getBodyItem(i),
                )
            }

            for (i in 0 until rule.bodySize) {
                assertEquals(
                    rule.bodyAsTuple
                        ?.unfoldedSequence
                        ?.drop(i)
                        ?.first(),
                    rule.getBodyItem(i),
                )
            }

            assertEquals(Truth.TRUE, fact.getBodyItem(0))
        }
    })
