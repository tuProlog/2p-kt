package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import java.math.BigDecimal
import java.math.BigInteger
import it.unibo.tuprolog.core.Integer as LogicInteger

@Suppress("TooManyFunctions")
internal class JvmTermDeobjectifier : TermDeobjectifier {
    private val scope: Scope = Scope.empty()

    override fun deobjectify(`object`: Any): Term =
        when (`object`) {
            is Boolean -> deobjectifyBoolean(`object`)
            is Number -> deobjectifyNumber(`object`)
            is String -> deobjectifyString(`object`)
            is Map<*, *> -> deobjectifyMap(`object`)
            else -> throw DeobjectificationException(`object`)
        }

    override fun deobjectifyMany(`object`: Any): Iterable<Term> =
        when (`object`) {
            is List<*> -> `object`.map { deobjectify(it ?: throw DeobjectificationException(`object`)) }
            else -> throw DeobjectificationException(`object`)
        }

    private fun deobjectifyMap(value: Map<*, *>): Term =
        when {
            value.containsKey("var") -> deobjectifyVariable(value)
            value.containsKey("fun") && value.containsKey("args") -> deobjectifyStructure(value)
            value.containsKey("list") -> deobjectifyList(value)
            value.containsKey("block") -> deobjectifyBlock(value)
            value.containsKey("tuple") -> deobjectifyTuple(value)
            value.containsKey("integer") -> deobjectifyInteger(value)
            value.containsKey("real") -> deobjectifyReal(value)
            value.containsKey("head") || value.containsKey("body") -> deobjectifyClause(value)
            value.containsKey("set") -> deobjectifyBlock(value, "set")
            else -> throw DeobjectificationException(value)
        }

    private fun deobjectifyReal(value: Map<*, *>): Term =
        when (val actualValue = value["real"]) {
            is String -> scope.realOf(actualValue)
            is Number -> deobjectifyNumber(actualValue) as? Real ?: throw DeobjectificationException(value)
            else -> throw DeobjectificationException(value)
        }

    private fun deobjectifyInteger(value: Map<*, *>): Term =
        when (val actualValue = value["integer"]) {
            is String -> scope.intOf(actualValue)
            is Number -> deobjectifyNumber(actualValue) as? LogicInteger ?: throw DeobjectificationException(value)
            else -> throw DeobjectificationException(value)
        }

    private fun deobjectifyClause(value: Map<*, *>): Term {
        val head = value["head"]?.let { deobjectify(it) as? Struct } ?: throw DeobjectificationException(value)
        val body = value["body"]?.let { deobjectify(it) }
        return if (body == null) {
            scope.factOf(head)
        } else {
            scope.clauseOf(head, body)
        }
    }

    private fun deobjectifyList(value: Map<*, *>): Term {
        val items = value["list"] as? List<*> ?: throw DeobjectificationException(value)
        val last = value["tail"]
        return scope.logicListFrom(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
            last = last?.let { deobjectify(it) } ?: scope.emptyLogicList,
        )
    }

    private fun deobjectifyTuple(value: Map<*, *>): Term {
        val items = value["tuple"] as? List<*> ?: throw DeobjectificationException(value)
        return scope.tupleOf(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
        )
    }

    private fun deobjectifyBlock(
        value: Map<*, *>,
        name: String = "block",
    ): Term {
        val items = value[name] as? List<*> ?: throw DeobjectificationException(value)
        return scope.blockOf(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
        )
    }

    @Suppress("ThrowsCount")
    private fun deobjectifyStructure(value: Map<*, *>): Term {
        val name = value["fun"] as? String ?: throw DeobjectificationException(value)
        val args = value["args"] as? List<*> ?: throw DeobjectificationException(value)
        return scope.structOf(
            name,
            args.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
        )
    }

    private fun deobjectifyVariable(value: Map<*, *>): Term {
        val name = value["var"] as? String ?: throw DeobjectificationException(value)
        return if (name == Var.ANONYMOUS_NAME) {
            scope.anonymous()
        } else {
            scope.varOf(name)
        }
    }

    private fun deobjectifyString(value: String): Term = scope.atomOf(value)

    private fun deobjectifyNumber(value: Number): Term =
        when (value) {
            is Int -> scope.intOf(value)
            is Long -> scope.intOf(value)
            is Double -> scope.realOf(value.toString())
            is Byte -> scope.intOf(value)
            is Short -> scope.intOf(value)
            is Float -> scope.realOf(value.toString())
            is BigInteger -> scope.intOf(value.toString())
            is BigDecimal -> scope.realOf(value.toString())
            else -> throw DeobjectificationException(value)
        }

    private fun deobjectifyBoolean(value: Boolean): Term = scope.truthOf(value)
}
