package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface TermReadingDeserializer : TermDeserializer, ReadingDeserializer<Term> {

}