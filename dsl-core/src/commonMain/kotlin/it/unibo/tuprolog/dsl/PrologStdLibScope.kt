package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

@Suppress("PropertyName", "unused", "FunctionName")
interface PrologStdLibScope : PrologAwareScope {

    val at_end_of_stream: Atom
        get() = atomOf("at_end_of_stream")

    val halt: Atom
        get() = atomOf("halt")

    val nl: Atom
        get() = atomOf("nl")

    val repeat: Atom
        get() = atomOf("repeat")

    @JsName("cut")
    val cut: Atom
        get() = atomOf("!")

    fun at_end_of_stream(alias: Any): Struct =
        structOf("at_end_of_stream", alias.toTerm())

    fun call(goal: Any): Struct =
        structOf("call", goal.toTerm())

    fun `catch`(goal: Any, error: Any, continuation: Any): Struct =
        structOf("catch", goal.toTerm(), error.toTerm(), continuation.toTerm())

    fun `throw`(error: Any): Struct =
        structOf("throw", error.toTerm())

    fun not(goal: Any): Struct =
        structOf("not", goal.toTerm())

    fun naf(goal: Any): Struct =
        structOf("\\+", goal.toTerm())

    fun assert(clause: Any): Struct =
        structOf("assert", clause.toTerm())

    fun asserta(clause: Any): Struct =
        structOf("asserta", clause.toTerm())

    fun assertz(clause: Any): Struct =
        structOf("assertz", clause.toTerm())

    fun arg(index: Any, compound: Any, argument: Any): Struct =
        structOf("arg", index.toTerm(), compound.toTerm(), argument.toTerm())

    fun atom(atom: Any): Struct =
        structOf("atom", atom.toTerm())

    fun atomic(atomic: Any): Struct =
        structOf("atomic", atomic.toTerm())

    fun between(min: Any, max: Any, number: Any): Struct =
        structOf("between", min.toTerm(), max.toTerm(), number.toTerm())

    fun callable(goal: Any): Struct =
        structOf("callable", goal.toTerm())

    fun compound(struct: Any): Struct =
        structOf("compound", struct.toTerm())

    fun current_op(precedence: Any, specifier: Any, functor: Any): Struct =
        structOf("current_op", precedence.toTerm(), specifier.toTerm(), functor.toTerm())

    fun op(precedence: Any, specifier: Any, functor: Any): Struct =
        structOf("op", precedence.toTerm(), specifier.toTerm(), functor.toTerm())

    fun findall(template: Any, goal: Any, bag: Any): Struct =
        structOf("findall", template.toTerm(), goal.toTerm(), bag.toTerm())

    fun float(number: Any): Struct =
        structOf("float", number.toTerm())

    fun functor(struct: Any, functor: Any, arity: Any): Struct =
        structOf("functor", struct.toTerm(), functor.toTerm(), arity.toTerm())

    fun ground(term: Any): Struct =
        structOf("ground", term.toTerm())

    fun integer(number: Any): Struct =
        structOf("integer", number.toTerm())

    fun natural(number: Any): Struct =
        structOf("natural", number.toTerm())

    fun nonvar(number: Any): Struct =
        structOf("nonvar", number.toTerm())

    fun number(number: Any): Struct =
        structOf("number", number.toTerm())

    fun `var`(term: Any): Struct =
        structOf("var", term.toTerm())

    fun write(term: Any): Struct =
        structOf("write", term.toTerm())

    infix fun Any.unify(right: Any): Struct =
        structOf("=", this.toTerm(), right.toTerm())

    infix fun Any.nunify(right: Any): Struct =
        structOf("\\=", this.toTerm(), right.toTerm())

    @JsName("anyUniv")
    infix fun Any.univ(other: Any): Struct = structOf("=..", this.toTerm(), other.toTerm())

    @JsName("eq")
    infix fun Any.`=`(right: Any): Struct =
        structOf("=", this.toTerm(), right.toTerm())

    @JsName("neq")
    infix fun Any.`!=`(right: Any): Struct =
        structOf("\\=", this.toTerm(), right.toTerm())

    @JsName("id")
    infix fun Any.`==`(right: Any): Struct =
        structOf("==", this.toTerm(), right.toTerm())

    @JsName("nid")
    infix fun Any.`!==`(right: Any): Struct =
        structOf("\\==", this.toTerm(), right.toTerm())

    @JsName("arithEq")
    infix fun Any.`===`(right: Any): Struct =
        structOf("=:=", this.toTerm(), right.toTerm())

    @JsName("arithNeq")
    infix fun Any.`=!=`(right: Any): Struct =
        structOf("=\\=", this.toTerm(), right.toTerm())

    fun member(item: Any, list: Any): Struct =
        structOf("member", item.toTerm(), list.toTerm())

    fun retract(clause: Any): Struct =
        structOf("retract", clause.toTerm())

    fun append(left: Any, right: Any, result: Any): Struct =
        structOf("append", left.toTerm(), right.toTerm(), result.toTerm())

    fun retractall(clause: Any): Struct =
        structOf("retractall", clause.toTerm())

    fun abolish(indicator: Any): Struct =
        structOf("abolish", indicator.toTerm())

    fun atom_chars(atom: Any, chars: Any): Struct =
        structOf("atom_chars", atom.toTerm(), chars.toTerm())

    fun atom_codes(atom: Any, codes: Any): Struct =
        structOf("atom_codes", atom.toTerm(), codes.toTerm())

    fun atom_concat(first: Any, second: Any, result: Any): Struct =
        structOf("atom_concat", first.toTerm(), second.toTerm(), result.toTerm())

    fun atom_length(atom: Any, length: Any): Struct =
        structOf("atom_length", atom.toTerm(), length.toTerm())

    fun char_code(char: Any, code: Any): Struct =
        structOf("char_code", char.toTerm(), code.toTerm())

    fun clause(head: Any, body: Any): Struct =
        structOf("clause", head.toTerm(), body.toTerm())

    fun copy_term(term: Any, copy: Any): Struct =
        structOf("copy_term", term.toTerm(), copy.toTerm())

    fun current_prolog_flag(name: Any, value: Any): Struct =
        structOf("current_prolog_flag", name.toTerm(), value.toTerm())

//    bagof/3
//    char_code/2
//    close/1
//    close/2
//    copy_term/2
//    current_input/1
//    current_output/1
//    current_predicate/1
//    current_prolog_flag/2
//    flush_output/0
//    get_byte/1
//    get_byte/2
//    get_char/1
//    get_char/2
//    get_code/1
//    get_code/2
//    number_chars/2
//    number_codes/2
//    once/1
//    open/3
//    open/4
//    peek_byte/1
//    peek_byte/2
//    peek_char/1
//    peek_char/2
//    peek_code/1
//    peek_code/2
//    put_byte/1
//    put_byte/2
//    put_char/1
//    put_char/2
//    put_code/1
//    put_code/2
//    read/1
//    read/2
//    read_term/2
//    read_term/3
//    repeat/1
//    set_input/1
//    set_output/1
//    set_prolog_flag/2
//    set_stream_position/2
//    setof/3
//    stream_property/2
//    sub_atom/5
//    unify_with_occurs_check/2
//    write_canonical/1
//    write_canonical/2
//    write_term/2
//    write_term/3
//    writeq/1
//    writeq/2
}