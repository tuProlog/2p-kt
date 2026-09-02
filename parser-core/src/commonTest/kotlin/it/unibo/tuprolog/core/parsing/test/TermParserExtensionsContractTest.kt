package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parseAsAtom
import it.unibo.tuprolog.core.parsing.parseAsInteger
import it.unibo.tuprolog.core.parsing.parseAsReal
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.core.parsing.parseAsTerm
import it.unibo.tuprolog.core.parsing.parseAsVar
import kotlin.test.Test
import kotlin.test.assertIs

class TermParserExtensionsContractTest {
    @Test
    fun stringConvenienceFunctionsDelegateToTypedParserApi() {
        assertIs<Atom>("atom".parseAsAtom())
        assertIs<Var>("Variable".parseAsVar())
        assertIs<Integer>("42".parseAsInteger())
        assertIs<Real>("3.25".parseAsReal())
        assertIs<Struct>("f(a)".parseAsStruct())
        assertIs<Struct>("f(a)".parseAsTerm())
    }
}
