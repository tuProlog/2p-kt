package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.library.Library

/** Kotest registration of the [TestSolverConstruction] cases. */
inline fun <reified T : Solver, reified MT : MutableSolver> solverConstructionContract(
    factory: SolverFactory,
    defaultBuiltins: Library,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestSolverConstruction.prototype<T, MT>(factory, defaultBuiltins) }) {
    case("solver-construction/creating-empty-solver") { testCreatingEmptySolver() }
    case("solver-construction/creating-custom-solver") { testCreatingCustomSolver() }
    case("solver-construction/creating-solver-with-default-builtins") { testCreatingSolverWithDefaultBuiltins() }
    case("solver-construction/creating-custom-solver-with-default-builtins") {
        testCreatingCustomSolverWithDefaultBuiltins()
    }
    case("solver-construction/creating-empty-mutable-solver") { testCreatingEmptyMutableSolver() }
    case("solver-construction/creating-custom-mutable-solver") { testCreatingCustomMutableSolver() }
    case("solver-construction/creating-mutable-solver-with-default-builtins") {
        testCreatingMutableSolverWithDefaultBuiltins()
    }
    case("solver-construction/creating-custom-mutable-solver-with-default-builtins") {
        testCreatingCustomMutableSolverWithDefaultBuiltins()
    }
    case("solver-construction/building-empty-solver") { testBuildingEmptySolver() }
    case("solver-construction/building-custom-solver") { testBuildingCustomSolver() }
    case("solver-construction/building-solver-with-default-builtins") { testBuildingSolverWithDefaultBuiltins() }
    case("solver-construction/building-custom-solver-with-default-builtins") {
        testBuildingCustomSolverWithDefaultBuiltins()
    }
    case("solver-construction/building-empty-mutable-solver") { testBuildingEmptyMutableSolver() }
    case("solver-construction/building-custom-mutable-solver") { testBuildingCustomMutableSolver() }
    case("solver-construction/building-mutable-solver-with-default-builtins") {
        testBuildingMutableSolverWithDefaultBuiltins()
    }
    case("solver-construction/building-custom-mutable-solver-with-default-builtins") {
        testBuildingCustomMutableSolverWithDefaultBuiltins()
    }
}
