package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

interface ReadingTheoryDeserializer : TheoryDeserializer, ReadingDeserializer<Theory>