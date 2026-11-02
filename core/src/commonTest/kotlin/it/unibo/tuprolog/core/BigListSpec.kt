package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.utils.itemWiseEquals

class BigListSpec :
    FunSpec({
        val nums = 0..MAX

        @Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
        val X = Var.of("X")

        val list1 by lazy {
            List.of((nums).map { Integer.of(it) })
        }

        val list2 by lazy {
            List.from((nums).map { Integer.of(it) }, last = X)
        }

        test("big-lists-contains-a-lot-of-items") {
            itemWiseEquals(
                nums.asSequence().map { Integer.of(it) } + EmptyList(),
                list1.unfoldedSequence,
            ).shouldBeTrue()
            itemWiseEquals(nums.asSequence().map { Integer.of(it) }, list1.toSequence()).shouldBeTrue()

            itemWiseEquals(nums.asSequence().map { Integer.of(it) } + X, list2.unfoldedSequence).shouldBeTrue()
            itemWiseEquals(nums.asSequence().map { Integer.of(it) } + X, list2.toSequence()).shouldBeTrue()
        }

        test("big-lists-can-be-represented-as-long-strins") {
            list1.toString() shouldBe nums.joinToString(", ", "[", "]")
            list2.toString() shouldBe nums.joinToString(", ", "[", " | $X]")
        }

        test("big-lists-can-be-compared") {
            val otherList1 = list1.freshCopy()
            otherList1.hashCode() shouldBe list1.hashCode()
            otherList1 shouldBe list1

            val otherList2 = list2.freshCopy()
            otherList2.hashCode() shouldNotBe list2.hashCode()
            otherList2 shouldNotBe list2
        }

        test("big-lists-support-counting-via-size-property") {
            list1.size shouldBe nums.size
            list2.size shouldBe (nums.size + 1)
        }

        test("big-lists-support-ground-check") {
            list1.isGround.shouldBeTrue()
            list2.isGround.shouldBeFalse()
        }

        test("big-lists-support-application-of-substitutions1") {
            (
                run {
                    itemWiseEquals(
                        (nums.asSequence() + nums.asSequence()).map { Integer.of(it) },
                        list2[Substitution.unifier(X, list1)].castToList().toSequence(),
                    )
                }
            ).shouldBeTrue()
        }

        test("big-lists-support-application-of-substitutions2") {
            val n = MAX / 2
            val half1 = (0 until n).asSequence().map { Integer.of(it) }
            val half2 = (n + 1..MAX).asSequence().map { Integer.of(it) }
            val list = List.of(half1 + X + half2)
            (
                run {
                    itemWiseEquals(
                        nums.asSequence().map { Integer.of(it) },
                        list.apply(Substitution.unifier(X, Integer.of(n))).castToList().toSequence(),
                    )
                }
            ).shouldBeTrue()
        }
    })

private const val MAX = 200_000

private val IntRange.size: Int get() = last - first + 1
