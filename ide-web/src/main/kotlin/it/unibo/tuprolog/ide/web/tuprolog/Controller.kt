// TEST 1
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.theory.parsing.ClausesParser

// TEST 2
// import it.unibo.tuprolog.core.parsing.parseAsStruct

// TEST 3
//import it.unibo.tuprolog.core.Struct
//import it.unibo.tuprolog.core.Atom
//import it.unibo.tuprolog.core.Var
//import it.unibo.tuprolog.core.Fact
//import it.unibo.tuprolog.solve.Solver
//import it.unibo.tuprolog.solve.Solution
//import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
//import it.unibo.tuprolog.theory.Theory

fun testTuprolog(myTheory: String, myQuery: String) {
    console.log("Test tuProlog")

    // TEST 1
    val stringTheory = myTheory.trimIndent()
    val clauseReader = ClausesParser.withDefaultOperators()
    val theory = clauseReader.parseTheory(stringTheory)
    val solver = ClassicSolverFactory.mutableSolverWithDefaultBuiltins(staticKb = theory)
    val queryParser = TermParser.withDefaultOperators()
    val query = queryParser.parseStruct(myQuery.trimIndent())
    for (solution in solver.solve(query)) {
        if (solution.isYes)
            console.log("YES")
        else
            console.log("NO")

        if (solution.isYes) {
            console.log("SOLUTION", solution)
            console.log("QUERY", solution.query)
            console.log("SUBSTITUTION", solution.substitution)

            val value = solution.substitution.getByName("X")
//            val valueAsBigInteger = value?.asInteger()?.value
//            val actualValue = valueAsBigInteger?.toInt()
            console.log("RESULT", value)
        }
    }

    // ERROR
    /*
    Compiled with problems:X
    ERROR in ../../node_modules/antlr4/src/antlr4/CharStreams.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in
    'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    ERROR in ../../node_modules/antlr4/src/antlr4/FileStream.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in
    'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    */

    // TEST 2
    /*
    val stringTheory = "increment(A, B, C) :- C is A + B."
    val parsedTheory = stringTheory.parseAsStruct()
    console.log(parsedTheory)
    val stringQuery = "increment(1,2,X)."
    val parsedQuery = stringQuery.parseAsStruct()
    console.log(parsedQuery)
    */
    // ERROR
    /*
    Compiled with problems:X
    ERROR in ../../node_modules/antlr4/src/antlr4/CharStreams.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in
    'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    ERROR in ../../node_modules/antlr4/src/antlr4/FileStream.js 7:11-24
    Module not found: Error: Can't resolve 'fs' in
    'C:\Users\Fabio\Desktop\ise_workspace\2p-kt\build\js\node_modules\antlr4\src\antlr4'
    */

    // TEST 3
    /*
    val prolog = ClassicSolverFactory.solverWithDefaultBuiltins(
        staticKb = Theory.of(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c")))
        )
    )
    val goal = Struct.of("f", Var.of("X"))
    val solutions: List<Solution> = prolog.solveList(goal)
    console.log(solutions.size) // 3
    console.log(solutions) // [Yes(query=f(X_2), substitution={X_2=a}), Yes(query=f(X_2), substitution={X_2=b})
    Yes(query=f(X_2), substitution={X_2=c})]
    for (solution in solutions) {
        console.log(solution.query) // f(X_2), f(X_2), f(X_2)
        console.log(solution.isYes) // true, true, true
        console.log(solution.substitution) // {X_2=a}, {X_2=b}, {X_2=c}
    }
    */
    // ERROR
    /*
    "IllegalStateException", "No viable implementation for SolverFactory in
    [./2p-solve-classic:it.unibo.tuprolog.solve.classic.ClassicSolverFactory,
    2p-solve-classic:it.unibo.tuprolog.solve.classic.ClassicSolverFactory]"
    */
}
