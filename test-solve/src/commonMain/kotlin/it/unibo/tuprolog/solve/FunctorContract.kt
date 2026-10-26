package it.unibo.tuprolog.solve

/** Kotest registration of the [TestFunctor] cases. */
fun functorContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestFunctor.prototype(solverFactory) }) {
    case("functor/fun-arity") { testFunArity() }
    case("functor/fun-arity-with-sub") { testFunArityWithSub() }
    case("functor/fun-arity-zero") { testFunArityZero() }
    case("functor/fun-mats") { testFunMats() }
    case("functor/fun-wrong-arity") { testFunWrongArity() }
    case("functor/fun-wrong-name") { testFunWrongName() }
    case("functor/fun-x-name-y-arity") { testFunXNameYArity() }
    case("functor/fun-dec-num") { testFunDecNum() }
    case("functor/fun-cons-of") { testFunConsOf() }
    case("functor/fun-empty-list") { testFunEmptyList() }
    case("functor/fun-xy-wrong-arity") { testFunXYWrongArity() }
    case("functor/fun-xn-arity") { testFunXNArity() }
    case("functor/fun-xa-arity") { testFunXAArity() }
    case("functor/fun-num-name") { testFunNumName() }
    case("functor/fun-foo-name") { testFunFooName() }
    case("functor/fun-flag") { testFunFlag() }
    case("functor/fun-negative-arity") { testFunNegativeArity() }
}
