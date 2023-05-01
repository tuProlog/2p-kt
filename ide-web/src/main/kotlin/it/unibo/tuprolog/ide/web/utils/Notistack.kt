@file:JsModule("notistack")
@file:JsNonModule

package it.unibo.tuprolog.ide.web.utils

import mui.material.AlertColor
import mui.material.SnackbarOrigin
import mui.material.SnackbarOriginVertical
import react.*

//@Suppress("UnsafeCastFromDynamic")
@JsName("SnackbarProvider")
external val SnackbarProvider: ComponentClass<SnackbarProviderProps>

external interface SnackbarProviderProps : PropsWithChildren {
    var maxSnack: Int
    var dense: Boolean
    var variant: AlertColor
    var anchorOrigin: SnackbarOrigin
}

external fun useSnackbar(): SnackbarManager

external interface SnackbarKey {
    val key: Any
}
external interface SnackbarOptions {
    var anchorOrigin: SnackbarOrigin
    var variant: AlertColor
}

external interface SnackbarManager {
    val enqueueSnackbar: (message: String, options: SnackbarOptions?) -> SnackbarKey
}
