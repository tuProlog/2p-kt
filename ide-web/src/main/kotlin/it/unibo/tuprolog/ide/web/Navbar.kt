package it.unibo.tuprolog.ide.web

import AddEditorTab
import DownloadTheory
import OnFileLoad
import RemoveEditorTab
import RenameEditor
import csstype.AlignItems.Companion.center
import csstype.JustifyContent.Companion.spaceBetween
import csstype.NamedColor.Companion.green
import csstype.NamedColor.Companion.red
import csstype.em
import emotion.react.css
import mui.icons.material.Add
import mui.icons.material.Info
import mui.icons.material.DeleteForever
import mui.icons.material.Edit
import mui.icons.material.GetAppOutlined
import mui.icons.material.UploadFileOutlined
import mui.material.Button
import mui.material.ButtonColor
import mui.material.ButtonVariant.Companion.contained
import mui.material.ButtonVariant.Companion.outlined
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogContentText
import mui.material.DialogTitle
import mui.material.Fab
import mui.material.FabColor
import mui.material.FabVariant.Companion.extended
import mui.material.Stack
import mui.material.StackDirection
import mui.material.TextField
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.onChange
import react.redux.useDispatch
import react.redux.useSelector
import web.html.HTMLInputElement
import web.html.InputType
import State
import it.unibo.tuprolog.ide.web.utils.errorSnackbar
import it.unibo.tuprolog.ide.web.utils.successSnackbar
import it.unibo.tuprolog.ide.web.utils.useSnackbar
import mui.material.Alert
import mui.material.AlertColor
import mui.material.Snackbar
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.createRef
import react.useState
import redux.RAction

const val MYSPACING = 3
const val MYHEIGHT = 56.0
const val MYWIDTH = 56.0

//TODO far visualizzare le tab con il nome non tutto maiuscolo
val NavBar = FC<Props> {
    var isDialogOpen by useState(false)
    var isDialogRenameOpen by useState(false)
    var newFileName by useState("")
    val uploadInputRef = createRef<HTMLInputElement>()
    val newFileNameInputRef = createRef<HTMLInputElement>()
    var changeFileNameErrorInput by useState(false)
    val editorSelectedTab = useSelector<State, String> { s -> s.tuProlog.editorSelectedTab }

    var actionResultSeverity by useState<AlertColor>(AlertColor.info)
    var actionResultMessage by useState("undefined")
    var isMessageOpen by useState(false)

    val dispatcher = useDispatch<RAction, Nothing>()

//    val enqueueSnackbar = useSnackbar().enqueueSnackbar

    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(MYSPACING)

        sx {
            justifyContent = spaceBetween
            alignItems = center
        }

        div {
            img {
                src = "https://raw.githubusercontent.com/tuProlog/2p-kt/master/.img/logo.svg"
                height = MYHEIGHT
                width = MYWIDTH
                css {
                    paddingRight = 1.em
                    paddingLeft = 1.em
                }
            }
        }

        div {

            Fab {
                Add()
                color = FabColor.primary
                variant = extended
                onClick = {
                    dispatcher(
                        AddEditorTab("") { error ->
                            if (error) {
                                actionResultSeverity = AlertColor.error
                                actionResultMessage = "Unable to open a new tab"
                                isMessageOpen = true
                            }
//                            enqueueSnackbar("This is a success message!", successSnackbar)
//                            enqueueSnackbar("This is an error message!", errorSnackbar)
                        }
                    )

                }
                Typography {
                    +"Add"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                }
            }

            Fab {
                Edit()
                color = FabColor.secondary
                variant = extended
                onClick = {
                    newFileName = editorSelectedTab
                    changeFileNameErrorInput = false
                    isDialogRenameOpen = true
                }
                Typography {
                    +"Rename"
                }
                sx {
                    marginRight = 10.em
                    marginLeft = 1.em
                }
            }

            input {
                type = InputType.file
                ref = uploadInputRef
                hidden = true
                accept = ".pl, .txt"
                multiple = false
                onChange = {
                    it.target.files?.get(0)?.text()?.then { it1 ->
                        val fileName = it.target.files?.get(0)?.name
                        if (fileName == null ) {
                            actionResultSeverity = AlertColor.error
                            actionResultMessage = "Unable to load file. File has no name."
                            isMessageOpen = true
                        } else {
                            dispatcher(OnFileLoad(fileName, it1) { error ->
                                if (error) {
                                    actionResultSeverity = AlertColor.error
                                    actionResultMessage = "Unable to load file. File already exists."
                                    isMessageOpen = true
                                }
                            })
                        }
                    }
                }
            }
            Button {
                startIcon = UploadFileOutlined.create()
                variant = outlined
                onClick = { uploadInputRef.current?.click() }
                Typography {
                    +"Upload"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                    color = green
                }
            }
            Button {
                startIcon = GetAppOutlined.create()
                variant = outlined
                onClick = {
                    dispatcher(DownloadTheory { error ->
                        if (error) {
                            actionResultSeverity = AlertColor.error
                            actionResultMessage = "Unable to download theory. No theory specified."
                            isMessageOpen = true
                        }
                    })
                }
                Typography {
                    +"Download"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                    color = green
                }
            }
            Button {
                startIcon = DeleteForever.create()
                variant = outlined
                onClick = {
                    dispatcher(RemoveEditorTab { error ->
                        if (error) {
                            actionResultSeverity = AlertColor.error
                            actionResultMessage = "Unable to remove editor tab."
                            isMessageOpen = true
                        }
                    })
                }
                Typography {
                    +"Remove"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                    color = red
                }
            }
            Button {
                startIcon = Info.create()
                variant = contained
                onClick = {
                    isDialogOpen = true
                }
                Typography {
                    +"About"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                }
                color = ButtonColor.info
            }
        }

        Snackbar {
            open = isMessageOpen
            autoHideDuration = 6000

            Alert {
                onClose={ isMessageOpen = false }
                severity=actionResultSeverity
                + actionResultMessage
            }
        }

        Dialog {
            open = isDialogOpen
            onClose = { _, _ -> isDialogOpen = false }

            DialogTitle {
                +"About"
            }
            DialogContent {
                DialogContentText {
                    +"TupKTWeb versione 0.1"
                }
                DialogActions {
                    Button {
                        onClick = { isDialogOpen = false }
                        +"Close"
                    }
                }
            }
        }

        Dialog {
            open = isDialogRenameOpen
            onClose = { _, _ -> isDialogRenameOpen = false }
            DialogTitle {
                +"Rename editor"
            }
            DialogContent {
                DialogContentText {
                    +"Write here the new name for $editorSelectedTab"
                }
                TextField {
                    autoFocus = true
                    inputRef = newFileNameInputRef
                    fullWidth = true
                    error = changeFileNameErrorInput
                    label = ReactNode("New file name")
                    helperText = ReactNode("File name must end with .pl or .txt")
                    defaultValue = editorSelectedTab
                    onChange = {
                        newFileNameInputRef.current?.let { it1 ->
                            newFileName = it1.value
                            changeFileNameErrorInput = !(it1.value.matches(Regex("\\w+(\\.pl|\\.txt)\$")))
                        }
                    }
                }
            }
            DialogActions {
                Button {
                    onClick = {
                        isDialogRenameOpen = false
                    }
                    +"Cancel"
                }
                Button {
                    disabled = changeFileNameErrorInput
                    onClick = {
                        isDialogRenameOpen = false
                        dispatcher(RenameEditor(newFileName) { error ->
                            if (error) {
                                actionResultSeverity = AlertColor.error
                                actionResultMessage = "Unable to rename current editor tab."
                            }
                        })
                    }
                    +"Confirm"
                }
            }
        }
    }
}
