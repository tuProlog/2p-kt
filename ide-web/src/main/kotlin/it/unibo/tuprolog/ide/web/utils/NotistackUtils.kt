package it.unibo.tuprolog.ide.web.utils

import mui.material.SnackbarOrigin
import mui.material.SnackbarOriginHorizontal
import mui.material.SnackbarOriginVertical
import mui.material.AlertColor

val snackbarPosition = object: SnackbarOrigin {
    override var horizontal = SnackbarOriginHorizontal.right
    override var vertical = SnackbarOriginVertical.top
}

val successSnackbar = object: SnackbarOptions {
    override var anchorOrigin = snackbarPosition
    override var variant = AlertColor.success

}

val errorSnackbar = object: SnackbarOptions {
    override var anchorOrigin = snackbarPosition
    override var variant = AlertColor.error
}


