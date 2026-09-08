@file:JvmName("ComparisonUtils")

package it.unibo.tuprolog.core

import kotlin.jvm.JvmName

/**
 * Compares [string1] to [string2] lexicographically, independently of the current locale (used, in
 * particular, by [TermComparator.AtomComparator] and [TermComparator.VarComparator], so that term ordering
 * does not vary across platforms/locales).
 */
expect fun compareStringsLocaleIndependently(
    string1: String,
    string2: String,
): Int
