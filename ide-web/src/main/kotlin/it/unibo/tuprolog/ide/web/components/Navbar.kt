package it.unibo.tuprolog.ide.web.components

import csstype.AlignItems.Companion.center
import csstype.JustifyContent.Companion.spaceBetween
import csstype.Length
import csstype.NamedColor.Companion.green
import csstype.em
import emotion.react.css
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.utils.Themes
import it.unibo.tuprolog.ui.gui.PageName
import it.unibo.tuprolog.utils.io.JsFile
import it.unibo.tuprolog.utils.io.JsUrl
import js.uri.encodeURIComponent
import kotlinx.browser.localStorage
import mui.icons.material.Add
import mui.icons.material.Brightness4
import mui.icons.material.Brightness7
import mui.icons.material.DeleteForever
import mui.icons.material.Edit
import mui.icons.material.GetAppOutlined
import mui.icons.material.Info
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
import mui.material.SvgIconColor
import mui.material.Switch
import mui.material.TextField
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import org.w3c.dom.set
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.createRef
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.onChange
import react.useRequiredContext
import react.useState
import web.dom.document
import web.html.HTML
import web.html.HTMLInputElement
import web.html.InputType
import it.unibo.tuprolog.Info as TuPrologInfo

data class IDEStyle(
    val spacing: Int = DEFAULT_SPACING,
    val height: Double = DEFAULT_HEIGHT,
    val width: Double = DEFAULT_WIDTH,
    val paddingRight: Length = PADDING_RIGHT,
    val paddingLeft: Length = PADDING_LEFT,
    val marginRight: Length = MARGIN_RIGHT,
    val marginLeft: Length = MARGIN_LEFT
) {
    companion object {
        const val DEFAULT_SPACING = 3
        const val DEFAULT_HEIGHT = 56.0
        const val DEFAULT_WIDTH = 56.0
        val PADDING_RIGHT = 1.em
        val PADDING_LEFT = 1.em
        val MARGIN_RIGHT = 1.em
        val MARGIN_LEFT = 1.em
    }
}

val currentStyleConfig = IDEStyle()

// TODO move all config magic-numbers into separate file (even better a css file)

// const val MYHEIGH = 56.0
// const val MYWIDTH = 56.0

// TODO remove uppercase tab visualization name

val NavBar = FC<Props> {
    var isDialogOpen by useState(false)
    var isDialogRenameOpen by useState(false)
    var changeFileNameErrorInput by useState(false)
    var newFileName by useState("")

    val uploadInputRef = createRef<HTMLInputElement>()
    val newFileNameInputRef = createRef<HTMLInputElement>()

    var theme by useRequiredContext(ThemeContext)

    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(currentStyleConfig.spacing)

        sx {
            justifyContent = spaceBetween
            alignItems = center
        }

        div {
            img {
                src = "https://raw.githubusercontent.com/tuProlog/2p-kt/master/.img/logo.svg"
                height = currentStyleConfig.height

                css {
                    paddingRight = currentStyleConfig.paddingRight
                    paddingLeft = currentStyleConfig.paddingLeft
                }
            }
        }

        div {
            Fab {
                Add()
                color = FabColor.primary
                variant = extended
                onClick = {
                    TuPrologController.application.newPage()
                }
                Typography {
                    +"Add"
                }
                sx {
                    marginRight = currentStyleConfig.marginRight
                    marginLeft = currentStyleConfig.marginLeft
                }
            }
            Fab {
                Edit()
                color = FabColor.secondary
                variant = extended
                onClick = {
                    //  newFileName = editorSelectedTab
                    //  changeFileNameErrorInput = false
                    isDialogRenameOpen = true
                }
                Typography {
                    +"Rename"
                }
                sx {
                    marginRight = currentStyleConfig.marginRight
                    marginLeft = currentStyleConfig.marginLeft
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
                        val filePath = it.target.files?.get(0)?.name
                        if (filePath != null) {
                            // TODO set localstorage item Expiry Time (https://www.sohamkamani.com/javascript/localstorage-with-ttl-expiry/)
                            localStorage[filePath] = it1
                            val url = JsUrl(protocol = "file", host = "localhost", path = filePath)
                            val jsFile = JsFile(url)
                            TuPrologController.application.load(jsFile)
                        }
                        // TODO allow multiple uploads of the same file (following line is not working as intended)
                        // it.target.value = ""
                    }
                }
            }
            Button {
                startIcon = UploadFileOutlined.create()
                variant = outlined
                color = ButtonColor.success
                onClick = {
                    uploadInputRef.current?.click()
                }
                Typography {
                    +"Upload"
                }
                sx {
                    marginRight = currentStyleConfig.marginRight
                    marginLeft = currentStyleConfig.marginLeft
                }
            }
            Button {
                startIcon = GetAppOutlined.create()
                variant = outlined
                onClick = {
                    // real save
                    val editorText = TuPrologController.application.currentPage?.theory ?: ""
                    val fileName = TuPrologController.application.currentPage?.id?.name ?: "UNDEFINED.pl"
                    val elem = document.createElement(HTML.a)
                    elem.setAttribute(
                        "href",
                        "data:text/plain;charset=utf-8," + encodeURIComponent(
                            editorText
                        )
                    )
                    elem.setAttribute(
                        "download",
                        fileName
                    )
                    elem.click()

                    // :gui save
                    val url = JsUrl(protocol = "file", host = "localhost", path = fileName)
                    val jsFile = JsFile(url)
                    TuPrologController.application.currentPage?.save(jsFile)
                }
                Typography {
                    +"Download"
                }
                sx {
                    marginRight = currentStyleConfig.marginRight
                    marginLeft = currentStyleConfig.marginLeft
                    color = green
                }
            }
            Button {
                startIcon = DeleteForever.create()
                variant = outlined
                color = ButtonColor.error
                onClick = {
                    // find the deletable tab panel index
                    val index =
                        TuPrologController.application.pages.indexOfFirst {
                            it.id == TuPrologController.application.currentPage?.id
                        }
                    TuPrologController.application.currentPage?.close()

                    // select new ide
                    TuPrologController.application.select(
                        TuPrologController.application.pages.elementAt(
                            if (index == 0) {
                                index
                            } else {
                                index - 1
                            }
                        )
                    )
                }
                Typography {
                    +"Delete"
                }
                sx {
                    marginRight = currentStyleConfig.marginRight
                    marginLeft = currentStyleConfig.marginLeft
                }
            }
            Button {
                startIcon = Info.create()
                variant = contained
                onClick = {
                    isDialogOpen = true
                }
                color = ButtonColor.info
            }

            Switch {
                icon = Brightness7.create {
                    color = SvgIconColor.info
                }
                checkedIcon = Brightness4.create {
                    color = SvgIconColor.info
                }
                checked = (theme == Themes.Dark)

                onChange = { _, checked ->
                    theme = if (checked) Themes.Dark else Themes.Light
                }
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
                    p {
                        +"TupKTWeb version: 0.1"
                    }
                    p {
                        +"TuProlog 2p-kt version: ${TuPrologInfo.VERSION}"
                    }
                    p {
                        +"2p-kt GitHub repo: "
                        a {
                            href = "https://github.com/tuProlog/2p-kt"
                            +"https://github.com/tuProlog/2p-kt"
                        }
                    }
                    p {
                        +"Developed by: Alberto Donati & Fabio Muratori"
                    }
                }
                DialogActions {
                    Button {
                        onClick = {
                            isDialogOpen = false
                        }
                        +"Close"
                    }
                }
            }
        }
        if (TuPrologController.application.currentPage != null) {
            Dialog {
                open = isDialogRenameOpen
                onClose = { _, _ -> isDialogRenameOpen = false }
                DialogTitle {
                    +"Rename editor"
                }
                DialogContent {
                    DialogContentText {
                        //  +"Write here the new name for $currentPage"
                        val currentName = TuPrologController.application.currentPage?.id?.name
                        +"Write here the new name for $currentName"
                    }
                    TextField {
                        autoFocus = true
                        inputRef = newFileNameInputRef
                        fullWidth = true
                        error = changeFileNameErrorInput
                        label = ReactNode("New file name")
                        helperText = ReactNode("File name must end with .pl or .txt")
                        // defaultValue = currentPage
                        defaultValue = TuPrologController.application.currentPage?.id?.name
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
                            TuPrologController.application.currentPage?.id = PageName(newFileName)
                        }
                        +"Confirm"
                    }
                }
            }
        }
    }
}
