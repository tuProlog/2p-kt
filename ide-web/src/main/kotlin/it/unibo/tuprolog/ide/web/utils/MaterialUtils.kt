package it.unibo.tuprolog.ide.web.utils

import mui.material.GridProps
import mui.material.InputBaseProps
import mui.material.TextFieldProps

inline var GridProps.xs: Any?
    get() = asDynamic().xs
    set(value) {
        asDynamic().xs = value
    }

inline var TextFieldProps.InputProps: InputBaseProps
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().InputProps = value
    }
