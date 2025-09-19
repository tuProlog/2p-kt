package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

@Suppress("PropertyName", "unused", "FunctionName")
interface LogicProgrammingScopeWithPrologStandardLibrary<S : LogicProgrammingScopeWithPrologStandardLibrary<S>> :
    BaseLogicProgrammingScope<S> {
    val at_end_of_stream: Atom
        get() = atomOf("at_end_of_stream")

    val halt: Atom
        get() = atomOf("halt")

    val nl: Atom
        get() = atomOf("nl")

    val repeat: Atom
        get() = atomOf("repeat")

    val cut: Atom
        get() = atomOf("!")

    @JsName("at_end_of_stream1")
    fun at_end_of_stream(alias: Any): Struct = structOf("at_end_of_stream", alias.toTerm())

    @JsName("call1")
    fun call(goal: Any): Struct = structOf("call", goal.toTerm())

    @JsName("catch3")
    fun `catch`(
        goal: Any,
        error: Any,
        continuation: Any,
    ): Struct = structOf("catch", goal.toTerm(), error.toTerm(), continuation.toTerm())

    @JsName("throw1")
    fun `throw`(error: Any): Struct = structOf("throw", error.toTerm())

    @JsName("not1")
    fun not(goal: Any): Struct = structOf("not", goal.toTerm())

    @JsName("naf1")
    fun naf(goal: Any): Struct = structOf("\\+", goal.toTerm())

    @JsName("assert1")
    fun assert(clause: Any): Struct = structOf("assert", clause.toTerm())

    @JsName("asserta1")
    fun asserta(clause: Any): Struct = structOf("asserta", clause.toTerm())

    @JsName("assertz1")
    fun assertz(clause: Any): Struct = structOf("assertz", clause.toTerm())

    @JsName("arg3")
    fun arg(
        index: Any,
        compound: Any,
        argument: Any,
    ): Struct = structOf("arg", index.toTerm(), compound.toTerm(), argument.toTerm())

    @JsName("atom1")
    fun atom(atom: Any): Struct = structOf("atom", atom.toTerm())

    @JsName("atomic1")
    fun atomic(atomic: Any): Struct = structOf("atomic", atomic.toTerm())

    @JsName("between3")
    fun between(
        min: Any,
        max: Any,
        number: Any,
    ): Struct = structOf("between", min.toTerm(), max.toTerm(), number.toTerm())

    @JsName("callable1")
    fun callable(goal: Any): Struct = structOf("callable", goal.toTerm())

    @JsName("compound1")
    fun compound(struct: Any): Struct = structOf("compound", struct.toTerm())

    @JsName("current_op3")
    fun current_op(
        precedence: Any,
        specifier: Any,
        functor: Any,
    ): Struct = structOf("current_op", precedence.toTerm(), specifier.toTerm(), functor.toTerm())

    @JsName("op3")
    fun op(
        precedence: Any,
        specifier: Any,
        functor: Any,
    ): Struct = structOf("op", precedence.toTerm(), specifier.toTerm(), functor.toTerm())

    @JsName("findall3")
    fun findall(
        template: Any,
        goal: Any,
        bag: Any,
    ): Struct = structOf("findall", template.toTerm(), goal.toTerm(), bag.toTerm())

    @JsName("float1")
    fun float(number: Any): Struct = structOf("float", number.toTerm())

    @JsName("functor3")
    fun functor(
        struct: Any,
        functor: Any,
        arity: Any,
    ): Struct = structOf("functor", struct.toTerm(), functor.toTerm(), arity.toTerm())

    @JsName("ground1")
    fun ground(term: Any): Struct = structOf("ground", term.toTerm())

    @JsName("integer1")
    fun integer(number: Any): Struct = structOf("integer", number.toTerm())

    @JsName("natural")
    fun natural(number: Any): Struct = structOf("natural", number.toTerm())

    @JsName("nonvar1")
    fun nonvar(number: Any): Struct = structOf("nonvar", number.toTerm())

    @JsName("number1")
    fun number(number: Any): Struct = structOf("number", number.toTerm())

    @JsName("var1")
    fun `var`(term: Any): Struct = structOf("var", term.toTerm())

    @JsName("write1")
    fun write(term: Any): Struct = structOf("write", term.toTerm())

    @JsName("univ2")
    infix fun Any.univ(other: Any): Struct = structOf("=..", this.toTerm(), other.toTerm())

    @JsName("eq2")
    infix fun Any.eq(right: Any): Struct = structOf("=", this.toTerm(), right.toTerm())

    @JsName("neq2")
    infix fun Any.neq(right: Any): Struct = structOf("\\=", this.toTerm(), right.toTerm())

    @JsName("id2")
    infix fun Any.id(right: Any): Struct = structOf("==", this.toTerm(), right.toTerm())

    @JsName("nid2")
    infix fun Any.nid(right: Any): Struct = structOf("\\==", this.toTerm(), right.toTerm())

    @JsName("arithEq2")
    infix fun Any.arithEq(right: Any): Struct = structOf("=:=", this.toTerm(), right.toTerm())

    @JsName("arithNeq2")
    infix fun Any.arithNeq(right: Any): Struct = structOf("=\\=", this.toTerm(), right.toTerm())

    @JsName("member2")
    fun member(
        item: Any,
        list: Any,
    ): Struct = structOf("member", item.toTerm(), list.toTerm())

    @JsName("retract1")
    fun retract(clause: Any): Struct = structOf("retract", clause.toTerm())

    @JsName("append3")
    fun append(
        left: Any,
        right: Any,
        result: Any,
    ): Struct = structOf("append", left.toTerm(), right.toTerm(), result.toTerm())

    @JsName("retractall1")
    fun retractall(clause: Any): Struct = structOf("retractall", clause.toTerm())

    @JsName("abolish1")
    fun abolish(indicator: Any): Struct = structOf("abolish", indicator.toTerm())

    @JsName("atom_chars2")
    fun atom_chars(
        atom: Any,
        chars: Any,
    ): Struct = structOf("atom_chars", atom.toTerm(), chars.toTerm())

    @JsName("atom_codes2")
    fun atom_codes(
        atom: Any,
        codes: Any,
    ): Struct = structOf("atom_codes", atom.toTerm(), codes.toTerm())

    @JsName("atom_concat3")
    fun atom_concat(
        first: Any,
        second: Any,
        result: Any,
    ): Struct = structOf("atom_concat", first.toTerm(), second.toTerm(), result.toTerm())

    @JsName("atom_length2")
    fun atom_length(
        atom: Any,
        length: Any,
    ): Struct = structOf("atom_length", atom.toTerm(), length.toTerm())

    @JsName("char_code2")
    fun char_code(
        char: Any,
        code: Any,
    ): Struct = structOf("char_code", char.toTerm(), code.toTerm())

    @JsName("clause2")
    fun clause(
        head: Any,
        body: Any,
    ): Struct = structOf("clause", head.toTerm(), body.toTerm())

    @JsName("copy_term2")
    fun copy_term(
        term: Any,
        copy: Any,
    ): Struct = structOf("copy_term", term.toTerm(), copy.toTerm())

    @JsName("current_flag2")
    fun current_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("current_flag", name.toTerm(), value.toTerm())

    @Deprecated(
        "Despite current_prolog_flag/2 is a standard predicate, we suggest using current_flag/2",
        ReplaceWith("current_flag"),
    )
    @JsName("current_prolog_flag2")
    fun current_prolog_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("current_prolog_flag", name.toTerm(), value.toTerm())

    @JsName("sub_atom5")
    fun sub_atom(
        atom: Any,
        before: Any,
        length: Any,
        after: Any,
        sub_atom: Any,
    ): Struct = structOf("sub_atom", atom.toTerm(), before.toTerm(), length.toTerm(), after.toTerm(), sub_atom.toTerm())

    @JsName("number_chars2")
    fun number_chars(
        first: Any,
        second: Any,
    ): Struct = structOf("number_chars", first.toTerm(), second.toTerm())

    @JsName("number_codes2")
    fun number_codes(
        first: Any,
        second: Any,
    ): Struct = structOf("number_codes", first.toTerm(), second.toTerm())

    @JsName("bagof3")
    fun bagof(
        template: Any,
        goal: Any,
        bag: Any,
    ): Struct = structOf("bagof", template.toTerm(), goal.toTerm(), bag.toTerm())

    @JsName("setof3")
    fun setof(
        template: Any,
        goal: Any,
        bag: Any,
    ): Struct = structOf("setof", template.toTerm(), goal.toTerm(), bag.toTerm())

    @JsName("consult1")
    fun consult(url: Any): Struct = structOf("consult", url.toTerm())

    @JsName("set_flag2")
    fun set_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("set_flag", name.toTerm(), value.toTerm())

    @Deprecated(
        "Despite set_prolog_flag/2 is a standard predicate, we suggest using set_flag/2",
        ReplaceWith("set_flag"),
    )
    @JsName("set_prolog_flag2")
    fun set_prolog_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("set_prolog_flag", name.toTerm(), value.toTerm())

    @JsName("dynamic1")
    fun dynamic(indicator: Any): Struct = structOf("dynamic", indicator.toTerm())

    @JsName("static1")
    fun static(indicator: Any): Struct = structOf("static", indicator.toTerm())

    @JsName("solve1")
    fun solve(goal: Any): Struct = structOf("solve", goal.toTerm())

    @JsName("initialization1")
    fun initialization(goal: Any): Struct = structOf("initialization", goal.toTerm())

    @JsName("load1")
    fun load(url: Any): Struct = structOf("load", url.toTerm())

    @JsName("include1")
    fun include(url: Any): Struct = structOf("include", url.toTerm())

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
//    set_stream_position/2
//    stream_property/2
//    unify_with_occurs_check/2
//    write_canonical/1
//    write_canonical/2
//    write_term/2
//    write_term/3
//    writeq/1
//    writeq/2
}
