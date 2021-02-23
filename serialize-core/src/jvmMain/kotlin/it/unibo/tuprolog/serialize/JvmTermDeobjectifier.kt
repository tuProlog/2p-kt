package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import java.math.BigDecimal
import java.math.BigInteger
import it.unibo.tuprolog.core.Integer as LogicInteger

internal class JvmTermDeobjectifier : TermDeobjectifier {

    private val scope: Scope = Scope.empty()

    override fun deobjectify(`object`: Any): Term {
        return when (`object`) {
            is Boolean -> deobjectifyBoolean(`object`)
            is Number -> deobjectifyNumber(`object`)
            is String -> deobjectifyString(`object`)
            is Map<*, *> -> deobjectifyMap(`object`)
            else -> throw DeobjectificationException(`object`)
        }
    }

    override fun deobjectifyMany(`object`: Any): Iterable<Term> {
        return when (`object`) {
            is List<*> -> `object`.map { deobjectify(it ?: throw DeobjectificationException(`object`)) }
            else -> throw DeobjectificationException(`object`)
        }
    }

    private fun deobjectifyMap(value: Map<*, *>): Term {
        return when {
            value.containsKey("var") -> deobjectifyVariable(value)
            value.containsKey("fun") && value.containsKey("args") -> deobjectifyStructure(value)
            value.containsKey("list") -> deobjectifyList(value)
            value.containsKey("set") -> deobjectifySet(value)
            value.containsKey("tuple") -> deobjectifyTuple(value)
            value.containsKey("integer") -> deobjectifyInteger(value)
            value.containsKey("real") -> deobjectifyReal(value)
            value.containsKey("head") || value.containsKey("body") -> deobjectifyClause(value)
            else -> throw DeobjectificationException(value)
        }
    }

    private fun deobjectifyReal(value: Map<*, *>): Term {
        return when (val actualValue = value["real"]) {
            is String -> scope.realOf(actualValue)
            is Number -> deobjectifyNumber(actualValue) as? Real ?: throw DeobjectificationException(value)
            else -> throw DeobjectificationException(value)
        }
    }

    private fun deobjectifyInteger(value: Map<*, *>): Term {
        return when (val actualValue = value["integer"]) {
            is String -> scope.intOf(actualValue)
            is Number -> deobjectifyNumber(actualValue) as? LogicInteger ?: throw DeobjectificationException(value)
            else -> throw DeobjectificationException(value)
        }
    }

    private fun deobjectifyClause(value: Map<*, *>): Term {
        val head = value["head"]?.let {
            deobjectify(it) as? Struct ?: throw DeobjectificationException(value)
        }
        val body = value["body"]?.let { deobjectify(it) }
        return if (body == null) {
            scope.factOf(head!!)
        } else {
            scope.clauseOf(head, body)
        }
    }

    private fun deobjectifyList(value: Map<*, *>): Term {
        val items = value["list"] as? List<*> ?: throw DeobjectificationException(value)
        val last = value["tail"]
        return scope.listFrom(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
            last = last?.let { deobjectify(it) } ?: scope.emptyList
        )
    }

    private fun deobjectifyTuple(value: Map<*, *>): Term {
        val items = value["tuple"] as? List<*> ?: throw DeobjectificationException(value)
        return scope.tupleOf(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            }
        )
    }

    private fun deobjectifySet(value: Map<*, *>): Term {
        val items = value["set"] as? List<*> ?: throw DeobjectificationException(value)
        return scope.setOf(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            }
        )
    }

    private fun deobjectifyStructure(value: Map<*, *>): Term {
        val name = value["fun"] as? String ?: throw DeobjectificationException(value)
        val args = value["args"] as? List<*> ?: throw DeobjectificationException(value)
        return scope.structOf(
            name,
            args.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            }
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

    private fun deobjectifyString(value: String): Term {
        return scope.atomOf(value)
    }

    private fun deobjectifyNumber(value: Number): Term {
        return when (value) {
            is Int -> scope.numOf(value)
            is Long -> scope.numOf(value)
            is Double -> scope.numOf(value)
            is Byte -> scope.numOf(value)
            is Short -> scope.numOf(value)
            is Float -> scope.numOf(value)
            is BigInteger -> LogicInteger.of(value.toString())
            is BigDecimal -> Real.of(value.toString())
            else -> throw DeobjectificationException(value)
        }
    }

    private fun deobjectifyBoolean(value: Boolean): Term {
        return scope.truthOf(value)
    }
}
