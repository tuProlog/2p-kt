package it.unibo.tuprolog.core

import it.unibo.tuprolog.utils.itemWiseEquals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BigListTest {

    companion object {
        private const val MAX = 200_000
    }

    private val nums = 0..MAX

    private val X = Var.of("X")

    private val list1 by lazy {
        List.of((nums).map { Integer.of(it) })
    }

    private val list2 by lazy {
        List.from((nums).map { Integer.of(it) }, last = X)
    }

    private val IntRange.size: Int get() = last - first + 1

    @Test
    fun bigListsContainsALotOfItems() {
        assertTrue {
            itemWiseEquals(nums.asSequence().map { Integer.of(it) } + EmptyList(), list1.unfoldedSequence)
        }
        assertTrue { itemWiseEquals(nums.asSequence().map { Integer.of(it) }, list1.toSequence()) }

        assertTrue { itemWiseEquals(nums.asSequence().map { Integer.of(it) } + X, list2.unfoldedSequence) }
        assertTrue { itemWiseEquals(nums.asSequence().map { Integer.of(it) } + X, list2.toSequence()) }
    }

    @Test
    fun bigListsCanBeRepresentedAsLongStrins() {
        assertEquals(nums.joinToString(", ", "[", "]"), list1.toString())
        assertEquals(nums.joinToString(", ", "[", " | $X]"), list2.toString())
    }

    @Test
    fun bigListsCanBeCompared() {
        val otherList1 = list1.freshCopy()
        assertEquals(list1.hashCode(), otherList1.hashCode())
        assertEquals(list1, otherList1)

        val otherList2 = list2.freshCopy()
        assertNotEquals(list2.hashCode(), otherList2.hashCode())
        assertNotEquals(list2, otherList2)
    }

    @Test
    fun bigListsSupportCountingViaSizeProperty() {
        assertEquals(nums.size, list1.size)
        assertEquals(nums.size + 1, list2.size)
    }

    @Test
    fun bigListsSupportGroundCheck() {
        assertTrue { list1.isGround }
        assertFalse { list2.isGround }
    }

    @Test
    fun bigListsSupportApplicationOfSubstitutions1() {
        assertTrue {
            itemWiseEquals(
                (nums.asSequence() + nums.asSequence()).map { Integer.of(it) },
                list2[Substitution.unifier(X, list1)].castTo<List>().toSequence()
            )
        }
    }

    @Test
    fun bigListsSupportApplicationOfSubstitutions2() {
        val n = MAX / 2
        val half1 = (0 until n).asSequence().map { Integer.of(it) }
        val half2 = (n + 1..MAX).asSequence().map { Integer.of(it) }
        val list = List.of(half1 + X + half2)
        assertTrue {
            itemWiseEquals(
                nums.asSequence().map { Integer.of(it) },
                list.apply(Substitution.unifier(X, Integer.of(n))).castTo<List>().toSequence()
            )
        }
    }
}
