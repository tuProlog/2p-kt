package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetDurable : AbstractSetData("durable") {
    override val help: String =
        """
        `set_durable(+Key, ?Value)`
        
        Stores `Value` under atom `Key` in the durable custom-data store of the current execution context. A non-ground `Key` raises an instantiation error, any other non-atom `Key` a type error.

        **Examples**

        ```prolog
        ?- set_durable(counter, 1), get_durable(counter, X).
        X = 1.

        ?- set_durable(f(x), 1).
        throws error(type_error(atom, f(x)), _).
        ```
        """.trimIndent()

    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setDurableData(key, value)
    }
}
