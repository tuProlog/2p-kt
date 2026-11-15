package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ClauseManipulationSpec :
    FunSpec({
        val directive = Goal.of(Atom.of("a"), Atom.of("b")) // :- a, b.

        val fact = Fact.of(Struct.of("f", Integer.ONE)) // f(1).

        val rule = Rule.of(fact.head, directive.body) // f(1) :- a, b.

        test("set-head") {
            val otherHead = Struct.of("f", Integer.of(2))
            directive.setHead(otherHead) shouldBe Rule.of(otherHead, directive.body)
            rule.setHead(otherHead) shouldBe Rule.of(otherHead, rule.body)
            fact.setHead(otherHead) shouldBe Fact.of(otherHead)
        }

        test("set-head-functor") {
            val otherHead = Struct.of("g", Integer.ONE)
            directive.setHeadFunctor("g") shouldBe directive
            rule.setHeadFunctor("g") shouldBe Rule.of(otherHead, rule.body)
            fact.setHeadFunctor("g") shouldBe Fact.of(otherHead)
        }

        test("set-head-args") {
            val otherHead = Struct.of("f", Atom.of("a"), Atom.of("b"))
            directive.setHeadArgs(*otherHead.args.toTypedArray()) shouldBe directive
            rule.setHeadArgs(*otherHead.args.toTypedArray()) shouldBe Rule.of(otherHead, rule.body)
            fact.setHeadArgs(*otherHead.args.toTypedArray()) shouldBe Fact.of(otherHead)
        }

        test("add-last-head-arg") {
            val otherHead = Struct.of("f", Integer.ONE, Atom.of("b"))
            directive.addLastHeadArg(otherHead.args.last()) shouldBe directive
            rule.addLastHeadArg(otherHead.args.last()) shouldBe Rule.of(otherHead, rule.body)
            fact.addLastHeadArg(otherHead.args.last()) shouldBe Fact.of(otherHead)
        }

        test("append-head-arg") {
            val otherHead = Struct.of("f", Integer.ONE, Atom.of("b"))
            directive.appendHeadArg(otherHead.args.last()) shouldBe directive
            rule.appendHeadArg(otherHead.args.last()) shouldBe Rule.of(otherHead, rule.body)
            fact.appendHeadArg(otherHead.args.last()) shouldBe Fact.of(otherHead)
        }

        test("add-first-head-arg") {
            val otherHead = Struct.of("f", Atom.of("b"), Integer.ONE)
            directive.addFirstHeadArg(otherHead.args.first()) shouldBe directive
            rule.addFirstHeadArg(otherHead.args.first()) shouldBe Rule.of(otherHead, rule.body)
            fact.addFirstHeadArg(otherHead.args.first()) shouldBe Fact.of(otherHead)
        }

        test("insert-head-arg") {
            val otherHead = Struct.of("f", Atom.of("b"), Integer.ONE)
            directive.insertHeadArg(0, otherHead[0]) shouldBe directive
            rule.insertHeadArg(0, otherHead[0]) shouldBe Rule.of(otherHead, rule.body)
            fact.insertHeadArg(0, otherHead[0]) shouldBe Fact.of(otherHead)
        }

        test("head-inspection") {
            rule.headArgs.toList() shouldBe rule.head.args
            fact.headArgs.toList() shouldBe fact.head.args
            rule.headArity shouldBe rule.head.arity
            fact.headArity shouldBe fact.head.arity

            for (i in 0 until rule.headArity) {
                rule.getHeadArg(i) shouldBe rule.head[i]
            }

            for (i in 0 until fact.headArity) {
                fact.getHeadArg(i) shouldBe fact.head[i]
            }
        }

        test("set-body") {
            val otherBody = Tuple.of(Integer.ONE, Integer.of(2), Integer.of(3))
            directive.setBody(otherBody) shouldBe Goal.of(otherBody)
            rule.setBody(otherBody) shouldBe Rule.of(rule.head, otherBody)
            fact.setBody(otherBody) shouldBe Rule.of(fact.head, otherBody)
        }

        test("set-body-items1") {
            val otherBody = Var.of("X")
            directive.setBodyItems(otherBody) shouldBe Goal.of(otherBody)
            rule.setBodyItems(otherBody) shouldBe Rule.of(rule.head, otherBody)
            fact.setBodyItems(otherBody) shouldBe Rule.of(fact.head, otherBody)
        }

        test("set-body-items-n") {
            Scope.empty {
                val otherBody = tupleOf(varOf("X"), varOf("Y"))
                directive.setBodyItems(varOf("X"), varOf("Y")) shouldBe goalOf(otherBody)
                rule.setBodyItems(varOf("X"), varOf("Y")) shouldBe ruleOf(rule.head, otherBody)
                fact.setBodyItems(varOf("X"), varOf("Y")) shouldBe ruleOf(fact.head, otherBody)
            }
        }

        test("add-last-body-item") {
            val otherBody = Tuple.of(Atom.of("a"), Atom.of("b"), Atom.of("c"))
            directive.addLastBodyItem(otherBody.unfoldedSequence.last()) shouldBe Goal.of(otherBody)
            rule.addLastBodyItem(otherBody.unfoldedSequence.last()) shouldBe Rule.of(rule.head, otherBody)
            fact.addLastBodyItem(otherBody.unfoldedSequence.last()) shouldBe
                Rule.of(fact.head, Truth.TRUE, otherBody.unfoldedSequence.last())
        }

        test("append-body-item") {
            val otherBody = Tuple.of(Atom.of("a"), Atom.of("b"), Atom.of("c"))
            directive.appendBodyItem(otherBody.unfoldedSequence.last()) shouldBe Goal.of(otherBody)
            rule.appendBodyItem(otherBody.unfoldedSequence.last()) shouldBe Rule.of(rule.head, otherBody)
            fact.appendBodyItem(otherBody.unfoldedSequence.last()) shouldBe
                Rule.of(fact.head, Truth.TRUE, otherBody.unfoldedSequence.last())
        }

        test("add-first-body-item") {
            val otherBody = Tuple.of(Atom.of("c"), Atom.of("a"), Atom.of("b"))
            directive.addFirstBodyItem(otherBody.left) shouldBe Goal.of(otherBody)
            rule.addFirstBodyItem(otherBody.left) shouldBe Rule.of(rule.head, otherBody)
            fact.addFirstBodyItem(otherBody.left) shouldBe Rule.of(fact.head, otherBody.left, Truth.TRUE)
        }

        test("insert-body-item") {
            val otherBody = Tuple.of(Atom.of("c"), Atom.of("a"), Atom.of("b"))
            directive.insertBodyItem(0, otherBody.left) shouldBe Goal.of(otherBody)
            rule.insertBodyItem(0, otherBody.left) shouldBe Rule.of(rule.head, otherBody)
            fact.insertBodyItem(0, otherBody.left) shouldBe Rule.of(fact.head, otherBody.left, Truth.TRUE)
        }

        test("body-inspection") {
            directive.bodyItems.toList() shouldBe directive.bodyAsTuple?.toList()
            rule.bodyItems.toList() shouldBe rule.bodyAsTuple?.toList()
            fact.bodyItems.single() shouldBe fact.body
            directive.bodySize shouldBe directive.body.asTuple()?.size
            rule.bodySize shouldBe rule.body.asTuple()?.size
            fact.bodySize shouldBe 1

            for (i in 0 until directive.bodySize) {
                directive.getBodyItem(i) shouldBe (
                    directive.bodyAsTuple
                        ?.unfoldedSequence
                        ?.drop(i)
                        ?.first()
                )
            }

            for (i in 0 until rule.bodySize) {
                rule.getBodyItem(i) shouldBe (
                    rule.bodyAsTuple
                        ?.unfoldedSequence
                        ?.drop(i)
                        ?.first()
                )
            }

            fact.getBodyItem(0) shouldBe Truth.TRUE
        }
    })
