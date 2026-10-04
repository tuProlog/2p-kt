package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetPersistent : AbstractSetData("persistent") {
    override val help: String =
        """
        `set_persistent(+Key, ?Value)`
        
        Stores `Value` under atom `Key` in the persistent custom-data store of the current execution context. A non-ground `Key` raises an instantiation error, any other non-atom `Key` a type error.

        **Examples**

        ```prolog
        ?- set_persistent(counter, 1).
        yes.

        ?- get_persistent(counter, X).
        X = 1.

        ?- set_persistent(f(x), 1).
        throws error(type_error(atom, f(x)), _).
        ```
        """.trimIndent()

    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setPersistentData(key, value)
    }
}
