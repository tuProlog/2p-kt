package it.unibo.tuprolog.ide.web.utils

import mui.material.GridProps
import mui.material.InputBaseProps
import mui.material.TextFieldProps

//TODO Remove when it will be implemented in MUI wrappers
inline var GridProps.xs: Int
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().xs = value
    }

inline var TextFieldProps.InputProps: InputBaseProps
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().InputProps = value
    }
