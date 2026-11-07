package it.unibo.tuprolog.examples.benchmarks

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests

private data class BenchmarkSetupCase(
    val id: String,
    val setup: () -> Unit,
)

private val setupCases =
    listOf(
        BenchmarkSetupCase("prolog/failing-search") {
            PrologSolverBenchmark().apply { workload = "failing-search" }.setUp()
        },
        BenchmarkSetupCase("prolog/tiny") {
            PrologSolverBenchmark().apply { workload = "tiny" }.setUp()
        },
        BenchmarkSetupCase("unificator/worklist/failing-search") {
            UnificatorSolverBenchmark().apply { workload = "failing-search" }.setUp()
        },
        BenchmarkSetupCase("unificator/equational/failing-search") {
            UnificatorSolverBenchmark()
                .apply {
                    workload = "failing-search"
                    engine = "equational"
                }.setUp()
        },
        BenchmarkSetupCase("unificator/tiny") {
            UnificatorSolverBenchmark().apply { workload = "tiny" }.setUp()
        },
    )

class BenchmarkSetupSpec :
    FunSpec({
        withTests(nameFn = { it.id }, setupCases) { case ->
            case.setup()
        }
    })
