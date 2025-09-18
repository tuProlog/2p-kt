package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

@Suppress("USELESS_CAST", "TooManyFunctions")
internal class JsTermDeobjectifier : TermDeobjectifier {
    private val scope: Scope = Scope.empty()

    override fun deobjectify(`object`: Any): Term =
        when (`object`) {
            is Boolean -> deobjectifyBoolean(`object`)
            is Int, Long, Short, Byte, Float, Double -> deobjectifyNumber(`object`)
            is String -> deobjectifyString(`object`)
            else -> deobjectifyObj(`object`)
        }

    override fun deobjectifyMany(`object`: Any): Iterable<Term> =
        when (`object`) {
            is Array<*> -> `object`.map { deobjectify(it ?: throw DeobjectificationException(`object`)) }
            else -> throw DeobjectificationException(`object`)
        }

    private fun deobjectifyObj(value: dynamic): Term =
        when {
            hasProperty(value, "var") -> deobjectifyVariable(value)
            hasProperty(value, "fun") && hasProperty(value, "args") -> deobjectifyStructure(value)
            hasProperty(value, "list") -> deobjectifyList(value)
            hasProperty(value, "block") -> deobjectifyBlock(value)
            hasProperty(value, "tuple") -> deobjectifyTuple(value)
            hasProperty(value, "integer") -> deobjectifyInteger(value)
            hasProperty(value, "real") -> deobjectifyReal(value)
            hasProperty(value, "head") || hasProperty(value, "body") -> deobjectifyClause(value)
            hasProperty(value, "set") -> deobjectifyBlock(value, "set")
            else -> throw DeobjectificationException(value)
        }

    private fun deobjectifyReal(value: dynamic): Term =
        when (val actualValue = value["real"]) {
            is String -> scope.realOf(actualValue as String)
            is Double -> scope.realOf(actualValue as Double)
            else -> throw DeobjectificationException(value)
        }

    private fun deobjectifyInteger(value: dynamic): Term =
        when (val actualValue = value["integer"]) {
            is String -> scope.intOf(actualValue as String)
            is Int -> scope.intOf(actualValue as Int)
            else -> throw DeobjectificationException(value)
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
        val last = value["tail"]
        return scope.logicListFrom(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
            last = if (last != null) deobjectify(last) else scope.emptyLogicList,
        )
    }

    private fun deobjectifyTuple(value: dynamic): Term {
        val items = value["tuple"] as? Array<*> ?: throw DeobjectificationException(value)
        return scope.tupleOf(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
        )
    }

    private fun deobjectifyBlock(
        value: dynamic,
        name: String = "block",
    ): Term {
        val items = value[name] as? Array<*> ?: throw DeobjectificationException(value)
        return scope.blockOf(
            items.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
        )
    }

    @Suppress("UseCheckOrError", "ThrowsCount")
    private fun deobjectifyStructure(value: dynamic): Term {
        val name = value["fun"] as? String ?: throw DeobjectificationException(value)
        val args = value["args"] as? Array<*> ?: throw DeobjectificationException(value)
        return scope.structOf(
            name,
            args.map {
                deobjectify(it ?: throw DeobjectificationException(value))
            },
        )
    }

    private fun deobjectifyVariable(value: dynamic): Term {
        val name = value["var"] as? String ?: throw DeobjectificationException(value)
        return if (name == Var.ANONYMOUS_NAME) {
            scope.anonymous()
        } else {
            scope.varOf(name)
        }
    }

    private fun deobjectifyString(value: String): Term = scope.atomOf(value)

    private fun deobjectifyBoolean(value: Boolean): Term = scope.truthOf(value)

    private fun deobjectifyNumber(value: dynamic): Term = scope.numOf(value.toString())
}
