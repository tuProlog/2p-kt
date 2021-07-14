package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Library.Companion.toMapEnsuringNoDuplicates
import it.unibo.tuprolog.solve.libs.io.primitives.AtEndOfStream0
import it.unibo.tuprolog.solve.libs.io.primitives.AtEndOfStream1
import it.unibo.tuprolog.solve.libs.io.primitives.CharConversion
import it.unibo.tuprolog.solve.libs.io.primitives.Close1
import it.unibo.tuprolog.solve.libs.io.primitives.Close2
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import it.unibo.tuprolog.solve.libs.io.primitives.CurrentCharConversion
import it.unibo.tuprolog.solve.libs.io.primitives.CurrentInput
import it.unibo.tuprolog.solve.libs.io.primitives.CurrentOutput
import it.unibo.tuprolog.solve.libs.io.primitives.FlushOutput
import it.unibo.tuprolog.solve.libs.io.primitives.GetByte1
import it.unibo.tuprolog.solve.libs.io.primitives.GetByte2
import it.unibo.tuprolog.solve.libs.io.primitives.GetChar1
import it.unibo.tuprolog.solve.libs.io.primitives.GetChar2
import it.unibo.tuprolog.solve.libs.io.primitives.GetCode1
import it.unibo.tuprolog.solve.libs.io.primitives.GetCode2
import it.unibo.tuprolog.solve.libs.io.primitives.Nl1
import it.unibo.tuprolog.solve.libs.io.primitives.Open3
import it.unibo.tuprolog.solve.libs.io.primitives.Open4
import it.unibo.tuprolog.solve.libs.io.primitives.PeekByte1
import it.unibo.tuprolog.solve.libs.io.primitives.PeekByte2
import it.unibo.tuprolog.solve.libs.io.primitives.PeekChar1
import it.unibo.tuprolog.solve.libs.io.primitives.PeekChar2
import it.unibo.tuprolog.solve.libs.io.primitives.PeekCode1
import it.unibo.tuprolog.solve.libs.io.primitives.PeekCode2
import it.unibo.tuprolog.solve.libs.io.primitives.PutByte1
import it.unibo.tuprolog.solve.libs.io.primitives.PutByte2
import it.unibo.tuprolog.solve.libs.io.primitives.PutChar1
import it.unibo.tuprolog.solve.libs.io.primitives.PutChar2
import it.unibo.tuprolog.solve.libs.io.primitives.PutCode1
import it.unibo.tuprolog.solve.libs.io.primitives.PutCode2
import it.unibo.tuprolog.solve.libs.io.primitives.Read1
import it.unibo.tuprolog.solve.libs.io.primitives.Read2
import it.unibo.tuprolog.solve.libs.io.primitives.ReadTerm2
import it.unibo.tuprolog.solve.libs.io.primitives.ReadTerm3
import it.unibo.tuprolog.solve.libs.io.primitives.SetInput
import it.unibo.tuprolog.solve.libs.io.primitives.SetOutput
import it.unibo.tuprolog.solve.libs.io.primitives.SetTheory
import it.unibo.tuprolog.solve.libs.io.primitives.StreamProperty
import it.unibo.tuprolog.solve.libs.io.primitives.Write2
import it.unibo.tuprolog.solve.libs.io.primitives.WriteCanonical1
import it.unibo.tuprolog.solve.libs.io.primitives.WriteCanonical2
import it.unibo.tuprolog.solve.libs.io.primitives.WriteEq1
import it.unibo.tuprolog.solve.libs.io.primitives.WriteEq2
import it.unibo.tuprolog.solve.libs.io.primitives.WriteTerm2
import it.unibo.tuprolog.solve.libs.io.primitives.WriteTerm3
import it.unibo.tuprolog.theory.Theory

object IOLib : AliasedLibrary by
Library.aliased(
    operatorSet = OperatorSet(),
    theory = Theory.empty(),
    functions = emptyMap(),
    primitives = sequenceOf(
        AtEndOfStream0,
        AtEndOfStream1,
        CharConversion,
        Close1,
        Close2,
        Consult,
        CurrentCharConversion,
        CurrentInput,
        CurrentOutput,
        FlushOutput,
        GetByte1,
        GetByte2,
        GetChar1,
        GetChar2,
        GetCode1,
        GetCode2,
        Nl1,
        Open3,
        Open4,
        PeekByte1,
        PeekByte2,
        PeekChar1,
        PeekChar2,
        PeekCode1,
        PeekCode2,
        PutByte1,
        PutByte2,
        PutChar1,
        PutChar2,
        PutCode1,
        PutCode2,
        Read1,
        Read2,
        ReadTerm2,
        ReadTerm3,
        SetInput,
        SetOutput,
        SetTheory,
        StreamProperty,
        Write2,
        WriteCanonical1,
        WriteCanonical2,
        WriteEq1,
        WriteEq2,
        WriteTerm2,
        WriteTerm3
    ).map { it.descriptionPair }.toMapEnsuringNoDuplicates(),
    alias = "prolog.io"
)
