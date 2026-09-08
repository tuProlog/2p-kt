package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.library.Library.Companion.toMapEnsuringNoDuplicates
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
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
import it.unibo.tuprolog.solve.primitive.Primitive

/**
 * The `:io-lib` [it.unibo.tuprolog.solve.library.Library], contributing the ISO Prolog stream/I/O predicates:
 * stream management (`open/3,4`, `close/1,2`, `current_input/1`, `current_output/1`, `set_input/1`, `set_output/1`,
 * `stream_property/2`, `at_end_of_stream/0,1`, `flush_output/1`), character/code-level reading and writing
 * (`get_char/1,2`, `get_code/1,2`, `peek_char/1,2`, `peek_code/1,2`, `put_char/1,2`, `put_code/1,2`, `nl/1`), term
 * I/O (`read/1,2`, `read_term/2,3`, `write/2`, `writeq/1,2`, `write_canonical/1,2`, `write_term/2,3`), plus the
 * tuProlog-specific `consult/1` and `set_theory/1` for loading a theory from a [it.unibo.tuprolog.solve.libs.io.Url].
 *
 * Every predicate is implemented against [it.unibo.tuprolog.solve.channel.Channel] (see
 * [it.unibo.tuprolog.solve.channel.InputChannel] and [it.unibo.tuprolog.solve.channel.OutputChannel]), so the same
 * predicate set works uniformly whether a stream backs a JVM `File`, an in-memory string, or (on JS) a fetched URL.
 * Byte-oriented predicates (`get_byte/1,2`, `put_byte/1,2`, `peek_byte/1,2`) and the character-conversion table
 * (`char_conversion/2`, `current_char_conversion/2`) are registered, per ISO, but are not actually supported by this
 * implementation: invoking them always raises a [it.unibo.tuprolog.solve.exception.error.SystemError].
 *
 * Load it into a [it.unibo.tuprolog.solve.library.Runtime] alongside other libraries, e.g.
 * `Runtime.of(IOLib, OOPLib)`, as done by the `:repl` and `:ide` modules.
 *
 * @see it.unibo.tuprolog.solve.library.Library
 */
object IOLib : AbstractLibrary() {
    override val alias: String
        get() = "prolog.io"

    /** The [Signature]-indexed map of every ISO/tuProlog I/O [Primitive] contributed by this library. */
    override val primitives: Map<Signature, Primitive>
        get() =
            listOf(
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
                WriteTerm3,
            ).map { it.descriptionPair }.toMapEnsuringNoDuplicates()
}
