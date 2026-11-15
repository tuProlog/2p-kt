package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities

/** Clauses whose head is a [Pattern], e.g. the metarule `P(X) :- Q(X)`. */
class PatternClauseSpec :
    FunSpec({
        val a = Atom.of("a")
        val b = Atom.of("b")

        test("rule-of/pattern-head") {
            val p = Var.of("P")
            val head = Pattern.of(p, Var.of("X"))
            val rule = Rule.of(head, Struct.of("q", a))
            rule.head shouldBeSameInstanceAs head
            rule.isRule.shouldBeTrue()
            rule.isWellFormed.shouldBeTrue()
            rule.headArity shouldBe 1
        }

        test("fact-of/pattern-head") {
            val head = Pattern.of(Var.of("P"), a)
            val fact = Fact.of(head)
            fact.head shouldBeSameInstanceAs head
            fact.body shouldBe Truth.TRUE
            Rule.of(head, Truth.TRUE).shouldBeInstanceOf<Fact>()
        }

        test("clause-of/pattern-head") {
            Clause.of(Pattern.of(Var.of("P"), a), b).shouldBeInstanceOf<Rule>()
        }

        test("scope/rule-and-fact-of-pattern-head") {
            Scope.empty {
                val rule = ruleOf(patternOf(varOf("P"), varOf("X")), patternOf(varOf("Q"), varOf("X")))
                rule.head.castToPattern().functor shouldBeSameInstanceAs varOf("P")
                factOf(patternOf(varOf("P"), a)).shouldBeInstanceOf<Fact>()
            }
        }

        test("struct-of/recognises-pattern-headed-rule") {
            val head = Pattern.of(Var.of("P"), a)
            val clause = Struct.of(Clause.FUNCTOR, head, b)
            clause.shouldBeInstanceOf<Rule>()
            clause.head shouldBe head
            Compound.of(Clause.FUNCTOR, head, b).shouldBeInstanceOf<Rule>()
        }

        test("is-clause/pattern-headed-structure") {
            Struct.of(Clause.FUNCTOR, Pattern.of(Var.of("P"), a), b).isClause.shouldBeTrue()
        }

        test("set-head-functor/concretises-pattern-head") {
            val x = Var.of("X")
            val rule = Rule.of(Pattern.of(Var.of("P"), x), b)
            val updated = rule.setHeadFunctor("p")
            updated.head.shouldBeInstanceOf<Struct>()
            assertEqualities(Struct.of("p", x), updated.head)
            updated.body shouldBe b
        }

        test("set-head-args/keeps-pattern-head") {
            val p = Var.of("P")
            val rule = Rule.of(Pattern.of(p, a), b)
            val updated = rule.setHeadArgs(b, a)
            updated.head.castToPattern().functor shouldBeSameInstanceAs p
            updated.head.args shouldBe listOf(b, a)
            Fact.of(Pattern.of(p, a)).setHeadArgs(b).shouldBeInstanceOf<Fact>()
        }

        test("head-arg-insertions/keep-pattern-head") {
            val p = Var.of("P")
            val rule = Rule.of(Pattern.of(p, a), b)
            rule.addFirstHeadArg(b).head.args shouldBe listOf(b, a)
            rule.addLastHeadArg(b).head.args shouldBe listOf(a, b)
            rule.appendHeadArg(b).head.args shouldBe listOf(a, b)
            rule.insertHeadArg(0, b).head.args shouldBe listOf(b, a)
            rule
                .addLastHeadArg(b)
                .head
                .castToPattern()
                .functor shouldBeSameInstanceAs p
        }

        test("set-head/accepts-pattern") {
            val head = Pattern.of(Var.of("P"), a)
            Rule.of(Struct.of("p", a), b).setHead(head).head shouldBeSameInstanceAs head
        }

        test("apply/binding-head-functor-yields-concrete-rule") {
            val p = Var.of("P")
            val x = Var.of("X")
            val rule = Rule.of(Pattern.of(p, x), Struct.of("q", x))
            val applied = rule.apply(Substitution.of(p, Atom.of("r")))
            applied.shouldBeInstanceOf<Rule>()
            assertEqualities(Struct.of("r", x), applied.head)
        }

        test("fresh-copy/shares-renamed-functor-between-head-and-body") {
            val p = Var.of("P")
            val x = Var.of("X")
            val rule = Rule.of(Pattern.of(p, x), Pattern.of(p, Struct.of("s", x)))
            val copy = rule.freshCopy()
            val headFunctor = copy.head.castToPattern().functor
            val bodyFunctor = copy.body.castToPattern().functor
            headFunctor shouldNotBe p
            headFunctor shouldBeSameInstanceAs bodyFunctor
            copy.head[0] shouldBeSameInstanceAs copy.body.castToPattern()[0].castToStruct()[0]
        }

        test("format/metarule") {
            val p = Var.of("P")
            val q = Var.of("Q")
            val x = Var.of("X")
            TermFormatter.readable().format(Rule.of(Pattern.of(p, x), Pattern.of(q, x))) shouldBe "P(X) :- Q(X)"
        }
    })
