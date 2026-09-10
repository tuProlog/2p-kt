package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

/**
 * Adds one builder per predicate of (roughly) the ISO Prolog standard library — plus a handful of well-known
 * de-facto standard ones (`assert`, `findall`, `member`, ...) — so common goals can be written as ordinary Kotlin
 * function/property calls instead of `structOf("member", item, list)`-style boilerplate:
 * ```kotlin
 * logicProgramming {
 *     findall("X", call("member".invoke("X", "List")), "Bag") // findall(X, call(member(X, List)), Bag)
 *     not(atom("X")) `if` (`var`("X"))                          // not(atom(X)) :- var(X)
 * }
 * ```
 * Each builder simply wraps its arguments (auto-[toTerm]-ed) into a [Struct] named after the predicate; none of
 * them actually run the predicate — that is `:dsl-solve`'s job, further up the DSL stack. A few
 * predicates not representable as legal Kotlin identifiers as-is are escaped with backticks (`` `catch` ``,
 * `` `throw` ``, `` `var` ``) or renamed (e.g. [naf] for `\+`/1).
 */
@Suppress("PropertyName", "unused", "FunctionName")
interface LogicProgrammingScopeWithPrologStandardLibrary<S : LogicProgrammingScopeWithPrologStandardLibrary<S>> :
    BaseLogicProgrammingScope<S> {
    /** The `at_end_of_stream`/0 [Atom]. */
    val at_end_of_stream: Atom
        get() = atomOf("at_end_of_stream")

    /** The `halt`/0 [Atom]. */
    val halt: Atom
        get() = atomOf("halt")

    /** The `nl`/0 [Atom]. */
    val nl: Atom
        get() = atomOf("nl")

    /** The `repeat`/0 [Atom]. */
    val repeat: Atom
        get() = atomOf("repeat")

    /** The cut (`!`/0) [Atom]. */
    val cut: Atom
        get() = atomOf("!")

    /** Builds an `at_end_of_stream`/1 [Struct]. */
    @JsName("at_end_of_stream1")
    fun at_end_of_stream(alias: Any): Struct = structOf("at_end_of_stream", alias.toTerm())

    /** Builds a `call`/1 [Struct]. */
    @JsName("call1")
    fun call(goal: Any): Struct = structOf("call", goal.toTerm())

    /** Builds a `catch`/3 [Struct]. */
    @JsName("catch3")
    fun `catch`(
        goal: Any,
        error: Any,
        continuation: Any,
    ): Struct = structOf("catch", goal.toTerm(), error.toTerm(), continuation.toTerm())

    /** Builds a `throw`/1 [Struct]. */
    @JsName("throw1")
    fun `throw`(error: Any): Struct = structOf("throw", error.toTerm())

    /** Builds a `not`/1 [Struct]. */
    @JsName("not1")
    fun not(goal: Any): Struct = structOf("not", goal.toTerm())

    /** Builds a `\+`/1 [Struct] (negation as failure). */
    @JsName("naf1")
    fun naf(goal: Any): Struct = structOf("\\+", goal.toTerm())

    /** Builds an `assert`/1 [Struct]. */
    @JsName("assert1")
    fun assert(clause: Any): Struct = structOf("assert", clause.toTerm())

    /** Builds an `asserta`/1 [Struct]. */
    @JsName("asserta1")
    fun asserta(clause: Any): Struct = structOf("asserta", clause.toTerm())

    /** Builds an `assertz`/1 [Struct]. */
    @JsName("assertz1")
    fun assertz(clause: Any): Struct = structOf("assertz", clause.toTerm())

    /** Builds an `arg`/3 [Struct]. */
    @JsName("arg3")
    fun arg(
        index: Any,
        compound: Any,
        argument: Any,
    ): Struct = structOf("arg", index.toTerm(), compound.toTerm(), argument.toTerm())

    /** Builds an `atom`/1 [Struct] (type-checking predicate, not to be confused with [Struct.functor]). */
    @JsName("atom1")
    fun atom(atom: Any): Struct = structOf("atom", atom.toTerm())

    /** Builds an `atomic`/1 [Struct]. */
    @JsName("atomic1")
    fun atomic(atomic: Any): Struct = structOf("atomic", atomic.toTerm())

    /** Builds a `between`/3 [Struct]. */
    @JsName("between3")
    fun between(
        min: Any,
        max: Any,
        number: Any,
    ): Struct = structOf("between", min.toTerm(), max.toTerm(), number.toTerm())

    /** Builds a `callable`/1 [Struct]. */
    @JsName("callable1")
    fun callable(goal: Any): Struct = structOf("callable", goal.toTerm())

    /** Builds a `compound`/1 [Struct]. */
    @JsName("compound1")
    fun compound(struct: Any): Struct = structOf("compound", struct.toTerm())

    /** Builds a `current_op`/3 [Struct]. */
    @JsName("current_op3")
    fun current_op(
        precedence: Any,
        specifier: Any,
        functor: Any,
    ): Struct = structOf("current_op", precedence.toTerm(), specifier.toTerm(), functor.toTerm())

    /** Builds an `op`/3 [Struct]. */
    @JsName("op3")
    fun op(
        precedence: Any,
        specifier: Any,
        functor: Any,
    ): Struct = structOf("op", precedence.toTerm(), specifier.toTerm(), functor.toTerm())

    /** Builds a `findall`/3 [Struct]. */
    @JsName("findall3")
    fun findall(
        template: Any,
        goal: Any,
        bag: Any,
    ): Struct = structOf("findall", template.toTerm(), goal.toTerm(), bag.toTerm())

    /** Builds a `float`/1 [Struct]. */
    @JsName("float1")
    fun float(number: Any): Struct = structOf("float", number.toTerm())

    /** Builds a `functor`/3 [Struct]. */
    @JsName("functor3")
    fun functor(
        struct: Any,
        functor: Any,
        arity: Any,
    ): Struct = structOf("functor", struct.toTerm(), functor.toTerm(), arity.toTerm())

    /** Builds a `ground`/1 [Struct]. */
    @JsName("ground1")
    fun ground(term: Any): Struct = structOf("ground", term.toTerm())

    /** Builds an `integer`/1 [Struct]. */
    @JsName("integer1")
    fun integer(number: Any): Struct = structOf("integer", number.toTerm())

    /** Builds a `natural`/1 [Struct]. */
    @JsName("natural")
    fun natural(number: Any): Struct = structOf("natural", number.toTerm())

    /** Builds a `nonvar`/1 [Struct]. */
    @JsName("nonvar1")
    fun nonvar(number: Any): Struct = structOf("nonvar", number.toTerm())

    /** Builds a `number`/1 [Struct]. */
    @JsName("number1")
    fun number(number: Any): Struct = structOf("number", number.toTerm())

    /** Builds a `var`/1 [Struct]. */
    @JsName("var1")
    fun `var`(term: Any): Struct = structOf("var", term.toTerm())

    /** Builds a `write`/1 [Struct]. */
    @JsName("write1")
    fun write(term: Any): Struct = structOf("write", term.toTerm())

    /** Builds a `=..`/2 [Struct] (univ). */
    @JsName("univ2")
    infix fun Any.univ(other: Any): Struct = structOf("=..", this.toTerm(), other.toTerm())

    /** Builds a `=`/2 [Struct]; same functor as [LogicProgrammingScopeWithOperators.equalsTo]. */
    @JsName("eq2")
    infix fun Any.eq(right: Any): Struct = structOf("=", this.toTerm(), right.toTerm())

    /** Builds a `\=`/2 [Struct]; same functor as [LogicProgrammingScopeWithOperators.notEqualsTo]. */
    @JsName("neq2")
    infix fun Any.neq(right: Any): Struct = structOf("\\=", this.toTerm(), right.toTerm())

    /** Builds a `==`/2 [Struct] (term structural equality). */
    @JsName("id2")
    infix fun Any.id(right: Any): Struct = structOf("==", this.toTerm(), right.toTerm())

    /** Builds a `\==`/2 [Struct] (term structural inequality). */
    @JsName("nid2")
    infix fun Any.nid(right: Any): Struct = structOf("\\==", this.toTerm(), right.toTerm())

    /** Builds a `=:=`/2 [Struct] (arithmetic equality). */
    @JsName("arithEq2")
    infix fun Any.arithEq(right: Any): Struct = structOf("=:=", this.toTerm(), right.toTerm())

    /** Builds a `=\=`/2 [Struct] (arithmetic inequality). */
    @JsName("arithNeq2")
    infix fun Any.arithNeq(right: Any): Struct = structOf("=\\=", this.toTerm(), right.toTerm())

    /** Builds a `member`/2 [Struct]. */
    @JsName("member2")
    fun member(
        item: Any,
        list: Any,
    ): Struct = structOf("member", item.toTerm(), list.toTerm())

    /** Builds a `retract`/1 [Struct]. */
    @JsName("retract1")
    fun retract(clause: Any): Struct = structOf("retract", clause.toTerm())

    /** Builds an `append`/3 [Struct]. */
    @JsName("append3")
    fun append(
        left: Any,
        right: Any,
        result: Any,
    ): Struct = structOf("append", left.toTerm(), right.toTerm(), result.toTerm())

    /** Builds a `retractall`/1 [Struct]. */
    @JsName("retractall1")
    fun retractall(clause: Any): Struct = structOf("retractall", clause.toTerm())

    /** Builds an `abolish`/1 [Struct]. */
    @JsName("abolish1")
    fun abolish(indicator: Any): Struct = structOf("abolish", indicator.toTerm())

    /** Builds an `atom_chars`/2 [Struct]. */
    @JsName("atom_chars2")
    fun atom_chars(
        atom: Any,
        chars: Any,
    ): Struct = structOf("atom_chars", atom.toTerm(), chars.toTerm())

    /** Builds an `atom_codes`/2 [Struct]. */
    @JsName("atom_codes2")
    fun atom_codes(
        atom: Any,
        codes: Any,
    ): Struct = structOf("atom_codes", atom.toTerm(), codes.toTerm())

    /** Builds an `atom_concat`/3 [Struct]. */
    @JsName("atom_concat3")
    fun atom_concat(
        first: Any,
        second: Any,
        result: Any,
    ): Struct = structOf("atom_concat", first.toTerm(), second.toTerm(), result.toTerm())

    /** Builds an `atom_length`/2 [Struct]. */
    @JsName("atom_length2")
    fun atom_length(
        atom: Any,
        length: Any,
    ): Struct = structOf("atom_length", atom.toTerm(), length.toTerm())

    /** Builds a `char_code`/2 [Struct]. */
    @JsName("char_code2")
    fun char_code(
        char: Any,
        code: Any,
    ): Struct = structOf("char_code", char.toTerm(), code.toTerm())

    /** Builds a `clause`/2 [Struct]; not to be confused with [MinimalLogicProgrammingScope.clause]. */
    @JsName("clause2")
    fun clause(
        head: Any,
        body: Any,
    ): Struct = structOf("clause", head.toTerm(), body.toTerm())

    /** Builds a `copy_term`/2 [Struct]. */
    @JsName("copy_term2")
    fun copy_term(
        term: Any,
        copy: Any,
    ): Struct = structOf("copy_term", term.toTerm(), copy.toTerm())

    /** Builds a `current_flag`/2 [Struct]. */
    @JsName("current_flag2")
    fun current_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("current_flag", name.toTerm(), value.toTerm())

    /** Builds a `current_prolog_flag`/2 [Struct]. Deprecated in favor of [current_flag]. */
    @Deprecated(
        "Despite current_prolog_flag/2 is a standard predicate, we suggest using current_flag/2",
        ReplaceWith("current_flag"),
    )
    @JsName("current_prolog_flag2")
    fun current_prolog_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("current_prolog_flag", name.toTerm(), value.toTerm())

    /** Builds a `sub_atom`/5 [Struct]. */
    @JsName("sub_atom5")
    fun sub_atom(
        atom: Any,
        before: Any,
        length: Any,
        after: Any,
        sub_atom: Any,
    ): Struct = structOf("sub_atom", atom.toTerm(), before.toTerm(), length.toTerm(), after.toTerm(), sub_atom.toTerm())

    /** Builds a `number_chars`/2 [Struct]. */
    @JsName("number_chars2")
    fun number_chars(
        first: Any,
        second: Any,
    ): Struct = structOf("number_chars", first.toTerm(), second.toTerm())

    /** Builds a `number_codes`/2 [Struct]. */
    @JsName("number_codes2")
    fun number_codes(
        first: Any,
        second: Any,
    ): Struct = structOf("number_codes", first.toTerm(), second.toTerm())

    /** Builds a `bagof`/3 [Struct]. */
    @JsName("bagof3")
    fun bagof(
        template: Any,
        goal: Any,
        bag: Any,
    ): Struct = structOf("bagof", template.toTerm(), goal.toTerm(), bag.toTerm())

    /** Builds a `setof`/3 [Struct]. */
    @JsName("setof3")
    fun setof(
        template: Any,
        goal: Any,
        bag: Any,
    ): Struct = structOf("setof", template.toTerm(), goal.toTerm(), bag.toTerm())

    /** Builds a `consult`/1 [Struct]. */
    @JsName("consult1")
    fun consult(url: Any): Struct = structOf("consult", url.toTerm())

    /** Builds a `set_flag`/2 [Struct]. */
    @JsName("set_flag2")
    fun set_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("set_flag", name.toTerm(), value.toTerm())

    /** Builds a `set_prolog_flag`/2 [Struct]. Deprecated in favor of [set_flag]. */
    @Deprecated(
        "Despite set_prolog_flag/2 is a standard predicate, we suggest using set_flag/2",
        ReplaceWith("set_flag"),
    )
    @JsName("set_prolog_flag2")
    fun set_prolog_flag(
        name: Any,
        value: Any,
    ): Struct = structOf("set_prolog_flag", name.toTerm(), value.toTerm())

    /** Builds a `dynamic`/1 [Struct]. */
    @JsName("dynamic1")
    fun dynamic(indicator: Any): Struct = structOf("dynamic", indicator.toTerm())

    /** Builds a `static`/1 [Struct]. */
    @JsName("static1")
    fun static(indicator: Any): Struct = structOf("static", indicator.toTerm())

    /** Builds a `solve`/1 [Struct]. */
    @JsName("solve1")
    fun solve(goal: Any): Struct = structOf("solve", goal.toTerm())

    /** Builds an `initialization`/1 [Struct]. */
    @JsName("initialization1")
    fun initialization(goal: Any): Struct = structOf("initialization", goal.toTerm())

    /** Builds a `load`/1 [Struct]. */
    @JsName("load1")
    fun load(url: Any): Struct = structOf("load", url.toTerm())

    /** Builds an `include`/1 [Struct]. */
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
