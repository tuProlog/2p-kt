package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface TermWritingSerializer : TermSerializer, WritingSerializer<Term> {

}