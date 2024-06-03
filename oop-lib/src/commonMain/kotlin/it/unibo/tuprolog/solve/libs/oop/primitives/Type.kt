package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.safeName

class Type(oopContext: OOPContext) :
    BinaryRelation.Functional<ExecutionContext>("type"), OOPContext by oopContext {
    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        catchingOopExceptions {
            when {
                first is Var && second is Var -> {
                    ensuringArgumentIsInstantiated(0)
                    Substitution.failed()
                }
                first is Var -> {
                    ensuringArgumentIsAtom(1)
                    ensuringArgumentIsTypeRefOrAlias(1)
                    mgu(first, Atom.of((second as TypeRef).value.safeName))
                }
                second is Var -> {
                    ensuringArgumentIsAtom(0)
                    mgu(termFactory.typeRef((first as Atom).value), second)
                }
                else -> {
                    ensuringArgumentIsAtom(0)
                    Substitution.failed()
                }
            }
        }
}
