package it.unibo.tuprolog.solve

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Iterator<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) {
    while (hasNext()) {
        action(next(), hasNext())
    }
}

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Iterable<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) =
    iterator().forEachWithLookahead(action)

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Sequence<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) =
    iterator().forEachWithLookahead(action)
