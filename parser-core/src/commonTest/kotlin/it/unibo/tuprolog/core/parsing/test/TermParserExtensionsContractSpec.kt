package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
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

class TermParserExtensionsContractSpec :
    FunSpec({
        test("string-convenience-functions-delegate-to-typed-parser-api") {
            "atom".parseAsAtom().shouldBeInstanceOf<Atom>()
            "Variable".parseAsVar().shouldBeInstanceOf<Var>()
            "42".parseAsInteger().shouldBeInstanceOf<Integer>()
            "3.25".parseAsReal().shouldBeInstanceOf<Real>()
            "f(a)".parseAsStruct().shouldBeInstanceOf<Struct>()
            "f(a)".parseAsTerm().shouldBeInstanceOf<Struct>()
        }
    })
