package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface WritingTermSerializer : TermSerializer, WritingSerializer<Term>