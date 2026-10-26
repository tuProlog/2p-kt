package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.utils.itemWiseEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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
            assertTrue {
                itemWiseEquals(nums.asSequence().map { Integer.of(it) } + EmptyList(), list1.unfoldedSequence)
            }
            assertTrue { itemWiseEquals(nums.asSequence().map { Integer.of(it) }, list1.toSequence()) }

            assertTrue { itemWiseEquals(nums.asSequence().map { Integer.of(it) } + X, list2.unfoldedSequence) }
            assertTrue { itemWiseEquals(nums.asSequence().map { Integer.of(it) } + X, list2.toSequence()) }
        }

        test("big-lists-can-be-represented-as-long-strins") {
            assertEquals(nums.joinToString(", ", "[", "]"), list1.toString())
            assertEquals(nums.joinToString(", ", "[", " | $X]"), list2.toString())
        }

        test("big-lists-can-be-compared") {
            val otherList1 = list1.freshCopy()
            assertEquals(list1.hashCode(), otherList1.hashCode())
            assertEquals(list1, otherList1)

            val otherList2 = list2.freshCopy()
            assertNotEquals(list2.hashCode(), otherList2.hashCode())
            assertNotEquals(list2, otherList2)
        }

        test("big-lists-support-counting-via-size-property") {
            assertEquals(nums.size, list1.size)
            assertEquals(nums.size + 1, list2.size)
        }

        test("big-lists-support-ground-check") {
            assertTrue { list1.isGround }
            assertFalse { list2.isGround }
        }

        test("big-lists-support-application-of-substitutions1") {
            assertTrue {
                itemWiseEquals(
                    (nums.asSequence() + nums.asSequence()).map { Integer.of(it) },
                    list2[Substitution.unifier(X, list1)].castToList().toSequence(),
                )
            }
        }

        test("big-lists-support-application-of-substitutions2") {
            val n = MAX / 2
            val half1 = (0 until n).asSequence().map { Integer.of(it) }
            val half2 = (n + 1..MAX).asSequence().map { Integer.of(it) }
            val list = List.of(half1 + X + half2)
            assertTrue {
                itemWiseEquals(
                    nums.asSequence().map { Integer.of(it) },
                    list.apply(Substitution.unifier(X, Integer.of(n))).castToList().toSequence(),
                )
            }
        }
    })

private const val MAX = 200_000

private val IntRange.size: Int get() = last - first + 1
