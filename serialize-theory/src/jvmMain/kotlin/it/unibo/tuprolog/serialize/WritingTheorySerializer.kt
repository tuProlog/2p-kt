package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

interface WritingTheorySerializer : TheorySerializer, WritingSerializer<Theory> {
}