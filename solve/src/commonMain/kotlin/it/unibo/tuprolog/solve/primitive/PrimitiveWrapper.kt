package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.NOT_LESS_THAN_ZERO
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.OPERATOR_SPECIFIER
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.WELL_FORMED_LIST
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.PRIVATE_PROCEDURE
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.STATIC_PROCEDURE
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError.Limit.MAX_ARITY
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.ATOM
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.ATOMIC
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.CHARACTER
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.INTEGER
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.LIST
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.PREDICATE_INDICATOR
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.MaxArity
import org.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.List as LogicList

/**
 * Wrapper class for [Primitive] implementation
 *
 * @author Enrico
 * @author Giovanni
 */
abstract class PrimitiveWrapper<C : ExecutionContext> : AbstractWrapper<Primitive> {

    constructor(signature: Signature) : super(signature)
    constructor(name: String, arity: Int, vararg: Boolean = false) : super(name, arity, vararg)

    /** The function expressing the implementation of the primitive, without any check for application to correct signature */
    protected abstract fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response>

    /** Checked primitive implementation */
    @Suppress("UNCHECKED_CAST")
    final override val wrappedImplementation: Primitive =
        primitiveOf(signature, ::uncheckedImplementation as Primitive)

    companion object {

        // TODO: 16/01/2020 test the three "wrap" functions

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Signature] and a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(signature: Signature, primitive: Primitive): PrimitiveWrapper<C> =
            FromFunction(signature, primitive)

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(
            name: String,
            arity: Int,
            vararg: Boolean,
            primitive: Primitive
        ): PrimitiveWrapper<C> =
            FromFunction(name, arity, vararg, primitive)

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(name: String, arity: Int, primitive: Primitive): PrimitiveWrapper<C> =
            wrap(name, arity, false, primitive)

        /** Private class to support the wrap methods, without using the object literal notation */
        private class FromFunction<C : ExecutionContext>(
            signature: Signature,
            private val uncheckedPrimitive: Primitive
        ) : PrimitiveWrapper<C>(signature) {

            constructor(name: String, arity: Int, vararg: Boolean = false, uncheckedPrimitive: Primitive) :
                this(Signature(name, arity, vararg), uncheckedPrimitive)

            override fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response> =
                uncheckedPrimitive(request)
        }

        private fun ensurerVisitor(context: ExecutionContext, procedure: Signature): TermVisitor<TypeError?> =
            object : TermVisitor<TypeError?> {
                override fun defaultValue(term: Term): Nothing? = null

                override fun visit(term: Struct) = when {
                    term.functor in Clause.notableFunctors && term.arity == 2 -> {
                        term.argsSequence.map { it.accept(this) }.filterNotNull().firstOrNull()
                    }
                    else -> defaultValue(term)
                }

                override fun visit(term: Numeric): TypeError {
                    return TypeError.forGoal(context, procedure, TypeError.Expected.CALLABLE, term)
                }
            }

        fun <C : ExecutionContext> Solve.Request<C>.checkTermIsRecursivelyCallable(term: Term): TypeError? =
            term.accept(ensurerVisitor(context, signature))

        /** Utility function to ensure that all arguments of Solve.Request are instantiated and *not* (still) Variables */
        fun <C : ExecutionContext> Solve.Request<C>.ensuringAllArgumentsAreInstantiated(): Solve.Request<C> =
            arguments.withIndex().firstOrNull { it.value is Var }?.let {
                ensureIsInstantiated(it.value, it.index)
            } ?: this

        private fun <C : ExecutionContext> Solve.Request<C>.ensureIsInstantiated(
            term: Term?,
            index: Int
        ): Solve.Request<C> =
            (term as? Var)?.let {
                throw InstantiationError.forArgument(
                    context,
                    signature,
                    it,
                    index
                )
            } ?: this

        fun <C : ExecutionContext> Solve.Request<C>.ensuringProcedureHasPermission(
            signature: Signature?,
            operation: PermissionError.Operation
        ): Solve.Request<C> {
            if (signature != null) {
                if (context.libraries.hasProtected(signature)) {
                    throw PermissionError.of(
                        context,
                        this.signature,
                        operation,
                        PRIVATE_PROCEDURE,
                        signature.toIndicator()
                    )
                }
                if (signature.toIndicator() in context.staticKb) {
                    throw PermissionError.of(
                        context,
                        this.signature,
                        operation,
                        STATIC_PROCEDURE,
                        signature.toIndicator()
                    )
                }
            }
            return this
        }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringClauseProcedureHasPermission(
            clause: Clause,
            operation: PermissionError.Operation
        ): Solve.Request<C> {
            val headSignature: Signature? = clause.head?.extractSignature()
            return ensuringProcedureHasPermission(headSignature, operation)
        }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsWellFormedIndicator(index: Int): Solve.Request<C> {
            ensuringArgumentIsInstantiated(index)
            when (val candidate = arguments[index]) {
                is Indicator -> {
                    val (name, arity) = candidate
                    when {
                        name is Var -> throw InstantiationError.forArgument(context, signature, name, index)
                        arity is Var -> throw InstantiationError.forArgument(context, signature, arity, index)
                        name !is Atom -> throw TypeError.forArgument(context, signature, ATOM, name, index)
                        arity !is Integer -> throw TypeError.forArgument(context, signature, INTEGER, arity, index)
                        arity.value < BigInteger.ZERO ->
                            throw DomainError.forArgument(context, signature, NOT_LESS_THAN_ZERO, arity, index)
                        context.flags[MaxArity]?.castTo<Integer>()?.value?.let { arity.value > it } ?: false ->
                            throw RepresentationError.of(context, signature, MAX_ARITY)
                        else -> return this
                    }
                }
                else -> throw TypeError.forArgument(context, signature, PREDICATE_INDICATOR, candidate, index)
            }
        }

        fun <C : ExecutionContext> Solve.Request<C>.notImplemented(
            message: String = "Primitive for ${signature.name}/${signature.arity} is not implemented, yet"
        ): Solve.Response = throw SystemError.forUncaughtException(context, NotImplementedError(message))

        fun <C : ExecutionContext> Solve.Request<C>.notSupported(
            message: String = "Operation ${signature.name}/${signature.arity} is not supported"
        ): Solve.Response = throw SystemError.forUncaughtException(context, IllegalStateException(message))

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsWellFormedClause(index: Int): Solve.Request<C> {
            ensuringArgumentIsInstantiated(index)
            ensuringArgumentIsStruct(index)
            val candidate = arguments[index]
            when {
                candidate is Clause -> {
                    if (!candidate.isWellFormed) {
                        throw DomainError.forArgument(context, signature, DomainError.Expected.CLAUSE, candidate, index)
                    }
                    return this
                }
                candidate is Struct && candidate.functor == Clause.FUNCTOR && candidate.arity == 2 ->
                    throw DomainError.forArgument(context, signature, DomainError.Expected.CLAUSE, candidate, index)
                candidate is Struct -> return this
                else -> throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, candidate, index)
            }
        }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsInstantiated(index: Int): Solve.Request<C> =
            ensureIsInstantiated(arguments[index], index)

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsNumeric(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Numeric -> throw TypeError.forArgument(context, signature, TypeError.Expected.NUMBER, arg, index)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsStruct(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Struct -> throw TypeError.forArgument(context, signature, TypeError.Expected.CALLABLE, arg, index)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsCallable(index: Int): Solve.Request<C> =
            ensuringArgumentIsStruct(index)

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsVariable(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Var -> throw TypeError.forArgument(
                    context,
                    signature,
                    TypeError.Expected.VARIABLE,
                    arg,
                    index
                )
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsCompound(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Struct, is Atom -> throw TypeError.forArgument(
                    context,
                    signature,
                    TypeError.Expected.COMPOUND,
                    arg,
                    index
                )
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsAtom(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Atom -> throw TypeError.forArgument(context, signature, ATOM, arg, index)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsConstant(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Constant -> throw TypeError.forArgument(context, signature, ATOMIC, arg, index)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsGround(index: Int): Solve.Request<C> =
            arguments[index].let {
                when {
                    !it.isGround -> {
                        throw InstantiationError.forArgument(context, signature, it.variables.first(), index)
                    }
                    else -> this
                }
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsChar(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                is Atom -> {
                    val string = arg.value
                    when {
                        string.length == 1 -> this
                        else -> throw TypeError.forArgument(context, signature, CHARACTER, arg, index)
                    }
                }
                else -> {
                    throw TypeError.forArgument(context, signature, CHARACTER, arg, index)
                }
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsSpecifier(index: Int): Solve.Request<C> =
            arguments[index].let { arg ->
                when {
                    arg !is Atom -> throw DomainError.forArgument(context, signature, OPERATOR_SPECIFIER, arg, index)
                    else -> {
                        try {
                            Specifier.fromTerm(arg)
                            this
                        } catch (e: IllegalArgumentException) {
                            throw DomainError.forArgument(context, signature, OPERATOR_SPECIFIER, arg, index)
                        }
                    }
                }
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsInteger(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Integer -> throw TypeError.forArgument(context, signature, INTEGER, arg, index)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsList(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is LogicList -> throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, arg, index)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsArity(index: Int): Solve.Request<C> =
            ensuringArgumentIsNonNegativeInteger(index).run {
                val arity = arguments[index] as Integer
                context.flags[MaxArity]?.castTo<Integer>()?.value?.let {
                    if (arity.intValue > it) {
                        throw RepresentationError.of(context, signature, MAX_ARITY)
                    }
                }
                return this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsNonNegativeInteger(index: Int): Solve.Request<C> =
            ensuringArgumentIsInteger(index)
                .arguments[index].let { arg ->
                when {
                    arg !is Integer || arg.intValue < BigInteger.ZERO -> throw DomainError.forArgument(
                        context,
                        signature,
                        NOT_LESS_THAN_ZERO,
                        arg,
                        index
                    )
                    else -> this
                }
            }

        private val MIN_CHAR = BigInteger.of(Char.MIN_VALUE.code)
        private val MAX_CHAR = BigInteger.of(Char.MAX_VALUE.code)

        fun Integer.isCharacterCode(): Boolean = intValue !in MIN_CHAR..MAX_CHAR

        fun <C : ExecutionContext> Solve.Request<C>.ensuringTermIsCharCode(term: Term): Solve.Request<C> =
            when {
                term !is Integer || term.isCharacterCode() ->
                    throw RepresentationError.of(context, signature, RepresentationError.Limit.CHARACTER_CODE)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringTermIsWellFormedList(term: Term): Solve.Request<C> =
            when {
                term !is LogicList || !term.isWellFormed -> throw DomainError.forTerm(context, WELL_FORMED_LIST, term)
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsWellFormedList(index: Int): Solve.Request<C> =
            when (val term = arguments[index]) {
                is LogicList -> when {
                    term.isWellFormed -> this
                    else -> throw DomainError.forArgument(context, signature, WELL_FORMED_LIST, term, index)
                }
                else -> throw TypeError.forArgument(context, signature, LIST, term, index)
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsCharCode(index: Int): Solve.Request<C> =
            ensuringArgumentIsInteger(index).ensuringTermIsCharCode(arguments[index])
    }
}
