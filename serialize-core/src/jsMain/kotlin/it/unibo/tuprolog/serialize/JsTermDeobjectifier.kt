package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

@Suppress("USELESS_CAST")
class JsTermDeobjectifier : TermDeobjectifier<dynamic> {

    private val scope: Scope = Scope.empty()

    override fun deobjectify(`object`: dynamic): Term {
        return when (`object`) {
            is Boolean -> deobjectifyBoolean(`object`)
            is Int, Long, Short, Byte, Float, Double -> deobjectifyNumber(`object`)
            is String -> deobjectifyString(`object`)
            else -> deobjectifyObj(`object`)
        }
    }

    private fun deobjectifyObj(value: dynamic): Term {
        return when {
            hasProperty(value, "var") -> deobjectifyVariable(value)
            hasProperty(value, "fun") && hasProperty(value, "args") -> deobjectifyStructure(value)
            hasProperty(value, "list") -> deobjectifyList(value)
            hasProperty(value, "set") -> deobjectifySet(value)
            hasProperty(value, "tuple") -> deobjectifyTuple(value)
            hasProperty(value, "integer") -> deobjectifyInteger(value)
            hasProperty(value, "real") -> deobjectifyReal(value)
            hasProperty(value, "head") || hasProperty(value, "body") -> deobjectifyClause(value)
            else -> throw DeobjectificationException(value)
        }
    }

    private fun deobjectifyReal(value: dynamic): Term {
        return when (val actualValue = value["real"]) {
            is String -> scope.realOf(actualValue as String)
            else -> deobjectifyNumber(actualValue)
        }
    }

    private fun deobjectifyInteger(value: dynamic): Term {
        return when (val actualValue = value["integer"]) {
            is String -> scope.intOf(actualValue as String)
            else -> deobjectifyNumber(actualValue)
        }
    }

    private fun deobjectifyClause(value: dynamic): Term {
        var head = value["head"]
        if (head != null) {
            head = deobjectify(head) as? Struct ?: throw DeobjectificationException(value)
        }
        var body = value["body"]
        if (body != null) {
            body = deobjectify(body)
        }
        return if (body == null) {
            scope.factOf(head)
        } else {
            scope.clauseOf(head, body)
        }
    }

    private fun deobjectifyList(value: dynamic): Term {
        val items = value["list"] as? Array<dynamic> ?: throw DeobjectificationException(value)
        val last = value["last"]
        return scope.listFrom(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
            last = if (last != null) deobjectify(last) else null
        )
    }

    private fun deobjectifyTuple(value: dynamic): Term {
        val items = value["tuple"] as? Array<*> ?: throw DeobjectificationException(value)
        return scope.tupleOf(items.map {
            deobjectify(it ?: throw DeobjectificationException(value))
        })
    }

    private fun deobjectifySet(value: dynamic): Term {
        val items = value["set"] as? Array<*> ?: throw DeobjectificationException(value)
        return scope.setOf(items.map {
            deobjectify(it ?: throw DeobjectificationException(value))
        })
    }

    private fun deobjectifyStructure(value: dynamic): Term {
        val name = value["fun"] as? String ?: throw DeobjectificationException(value)
        val args = value["args"] as? Array<*> ?: throw DeobjectificationException(value)
        return scope.structOf(name, args.map {
            deobjectify(it ?: throw DeobjectificationException(value))
        })
    }

    private fun deobjectifyVariable(value: dynamic): Term {
        val name = value["var"] as? String ?: throw DeobjectificationException(value)
        return if (name == Var.ANONYMOUS_VAR_NAME) {
            scope.anonymous()
        } else {
            scope.varOf(name)
        }
    }

    private fun deobjectifyString(value: String): Term {
        return scope.atomOf(value)
    }

    private fun deobjectifyBoolean(value: Boolean): Term {
        return scope.truthOf(value)
    }

    private fun deobjectifyNumber(value: dynamic): Term {
        return when (value) {
            is Int -> scope.numOf(value as Int)
            is Long -> scope.numOf(value as Long)
            is Double -> scope.numOf(value as Double)
            is Byte -> scope.numOf(value as Byte)
            is Short -> scope.numOf(value as Short)
            is Float -> scope.numOf(value as Float)
            else -> throw DeobjectificationException(value)
        }
    }

}