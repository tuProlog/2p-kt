package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface ReadingTermDeserializer : TermDeserializer, ReadingDeserializer<Term>