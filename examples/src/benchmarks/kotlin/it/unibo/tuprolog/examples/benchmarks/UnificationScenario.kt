package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.List as LogicList

private const val ARITY = 1_000

// the equation-based engine is cubic on these: larger sizes take minutes per operation
private const val VARIABLES = 200
private const val DEPTH = 1_000
private const val LENGTH = 10_000
private const val VARIABLE_PERIOD = 100

private val a = Atom.of("a")
private val b = Atom.of("b")

private fun atoms(n: Int): List<Term> = List(n) { Atom.of("a$it") }

private fun variables(
    n: Int,
    prefix: String = "X",
): List<Var> = List(n) { Var.of("$prefix$it") }

private fun f(args: List<Term>): Struct = Struct.of("f", args)

private fun nested(leaf: Term): Term = (0 until DEPTH).fold(leaf) { term, _ -> Struct.of("f", term) }

/** A list of [LENGTH] atoms, with a fresh variable every [VARIABLE_PERIOD] elements if [withVariables]. */
private fun bigList(withVariables: Boolean = false): LogicList =
    LogicList.of(List(LENGTH) { if (withVariables && it % VARIABLE_PERIOD == 0) Var.anonymous() else Atom.of("a$it") })

private fun mgu(
    lhs: Term,
    rhs: Term,
) = UnificationTask { unificator, occursCheck -> unificator.mgu(lhs, rhs, occursCheck) }

private fun merge(
    first: Substitution,
    second: Substitution,
) = UnificationTask { unificator, occursCheck -> unificator.merge(first, second, occursCheck) }

/** A substitution binding each of [vars] to the corresponding item of [values]. */
private fun bind(
    vars: List<Var>,
    values: List<Term>,
) = Substitution.of(vars.zip(values))

/** The unification benchmark scenarios (#1020), by JMH `scenario` parameter value. Terms are built by [task]. */
enum class UnificationScenario(
    val id: String,
    val task: () -> UnificationTask,
) {
    GROUND_EQUAL("ground-equal", { mgu(f(atoms(ARITY)), f(atoms(ARITY))) }),
    EARLY_MISMATCH("early-mismatch", { mgu(f(listOf(a) + atoms(ARITY)), f(listOf(b) + atoms(ARITY))) }),
    LATE_MISMATCH("late-mismatch", { mgu(f(atoms(ARITY) + a), f(atoms(ARITY) + b)) }),
    MANY_VARIABLES("many-variables", { mgu(f(variables(VARIABLES)), f(atoms(VARIABLES))) }),
    VARIABLE_CHAIN("variable-chain", {
        val vars = variables(VARIABLES + 1)
        mgu(f(vars), f(vars.drop(1) + a))
    }),
    REPEATED_VARIABLE("repeated-variable", { mgu(f(List(VARIABLES) { Var.of("X") }), f(List(VARIABLES) { a })) }),
    REPEATED_VARIABLE_FAILING("repeated-variable-failing", {
        val x = Var.of("X")
        mgu(f(List(VARIABLES) { x }), f(List(VARIABLES - 1) { a } + b))
    }),
    DEEP_TERMS("deep-terms", { mgu(nested(Var.of("X")), nested(a)) }),
    LIST_EQUAL("list-equal", { mgu(bigList(), bigList()) }),
    LIST_WITH_VARIABLES("list-with-variables", { mgu(bigList(withVariables = true), bigList()) }),
    LIST_EARLY_MISMATCH("list-early-mismatch", {
        mgu(
            LogicList.of(listOf(b) + bigList().toList()),
            LogicList.of(
                listOf(a) + bigList().toList(),
            ),
        )
    }),
    LIST_LATE_MISMATCH("list-late-mismatch", {
        mgu(
            LogicList.of(bigList().toList() + a),
            LogicList.of(
                bigList().toList() + b,
            ),
        )
    }),
    PARTIAL_LIST("partial-list", {
        mgu(
            LogicList.from(bigList().toList(), Var.of("T")),
            LogicList.of(
                bigList().toList() + a + b,
            ),
        )
    }),
    OCCURS_NEAR_START("occurs-near-start", {
        val x = Var.of("X")
        mgu(x, f(listOf(x) + atoms(ARITY)))
    }),
    OCCURS_NEAR_END("occurs-near-end", {
        val x = Var.of("X")
        mgu(x, f(atoms(ARITY) + x))
    }),
    OCCURS_NOWHERE("occurs-nowhere", { mgu(Var.of("X"), f(atoms(ARITY))) }),
    SELF_REFERENCE("self-reference", {
        val x = Var.of("X")
        mgu(x, Struct.of("f", x))
    }),
    MERGE_DISJOINT("merge-disjoint", {
        merge(bind(variables(VARIABLES, "X"), atoms(VARIABLES)), bind(variables(VARIABLES, "Y"), atoms(VARIABLES)))
    }),
    MERGE_COMPATIBLE("merge-compatible", {
        val xs = variables(VARIABLES)
        val ys = variables(VARIABLES, "Y")
        merge(bind(xs, ys.map { Struct.of("g", it) }), bind(xs, atoms(VARIABLES).map { Struct.of("g", it) }))
    }),
    MERGE_INCOMPATIBLE("merge-incompatible", {
        val xs = variables(VARIABLES)
        merge(bind(xs, atoms(VARIABLES)), bind(xs, atoms(VARIABLES - 1) + b))
    }),
    MERGE_CHAIN("merge-chain", {
        val xs = variables(VARIABLES + 1)
        merge(bind(xs.dropLast(1), xs.drop(1)), Substitution.of(xs.last(), a))
    }),
    ;

    companion object {
        /** The [UnificationScenario] whose [id] is [id]. */
        fun byId(id: String): UnificationScenario = entries.first { it.id == id }
    }
}
