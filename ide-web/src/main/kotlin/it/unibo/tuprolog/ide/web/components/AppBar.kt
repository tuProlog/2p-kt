package it.unibo.tuprolog.ide.web.components

import csstype.Color
import csstype.Display
import csstype.Length
import csstype.PropertyName.Companion.display
import csstype.em
import csstype.number
import csstype.px
import emotion.react.css
import it.unibo.tuprolog.ide.web.redux.AppState
import it.unibo.tuprolog.ide.web.redux.PageWrapper
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
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.Button
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogContentText
import mui.material.DialogTitle
import mui.material.IconButton
import mui.material.Size
import mui.material.Switch
import mui.material.TextField
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.w3c.dom.set
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.createRef
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.onChange
import react.redux.useSelector
import react.useRequiredContext
import react.useState
import web.dom.document
import web.html.HTML
import web.html.HTMLInputElement
import web.html.InputType
import it.unibo.tuprolog.Info as TuPrologInfo

data class IDEStyle2(
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
        const val DEFAULT_HEIGHT = 48.0
        const val DEFAULT_WIDTH = 48.0
        val PADDING_RIGHT = 1.em
        val PADDING_LEFT = 1.em
        val MARGIN_RIGHT = 1.em
        val MARGIN_LEFT = 1.em
    }
}

val currentStyleConfig2 = IDEStyle2()


val Appbar = FC<Props> {
    var isDialogOpen by useState(false)
    var isDialogRenameOpen by useState(false)
    var changeFileNameErrorInput by useState(false)
    var newFileName by useState("")

    val uploadInputRef = createRef<HTMLInputElement>()
    val newFileNameInputRef = createRef<HTMLInputElement>()

    var theme by useRequiredContext(ThemeContext)

    val currentPage = useSelector<AppState, PageWrapper?> { s -> s.tuProlog.currentPage }

    AppBar {
        position = AppBarPosition.static

        Toolbar {
            disableGutters = true

            img {
                src = "https://raw.githubusercontent.com/tuProlog/2p-kt/master/.img/logo.svg"
                height = currentStyleConfig2.height

                css {
                    paddingRight = currentStyleConfig2.paddingRight
                    paddingLeft = currentStyleConfig2.paddingLeft
                    backgroundColor = Color("#FFFFFF")
                    margin = 10.px
                    padding = 1.px
                    borderRadius = 10.px
                }
            }

            Typography {
                variant= TypographyVariant.h6
                noWrap=true
                sx {
                    marginRight = 2.em
                    color = null
                    textDecoration = null
                }
                + "tuProlog"
            }

            Box {
                sx {
                    marginRight = 20.px
                }
                Button {
                    startIcon = Add.create()
                    sx {
                        color = Color("#FFFFFF")
                    }
                    onClick = {
                        TuPrologController.application.newPage()
                    }
                    +"Add"
                }
            }

            Box {
                sx {
                    marginRight = 20.px
                }
                Button {
                    startIcon = Edit.create()
                    sx {
                        color = Color("#FFFFFF")
                    }
                    onClick = {
                        isDialogRenameOpen = true
                    }
                    +"Rename"
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
                            localStorage[filePath] = it1
                            val url = JsUrl(protocol = "file", host = "localhost", path = filePath)
                            val jsFile = JsFile(url)
                            TuPrologController.application.load(jsFile)
                        }
                    }
                }
            }

            Box {
                sx {
                    marginRight = 20.px
                }
                Button {
                    startIcon = UploadFileOutlined.create()
                    sx {
                        color = Color("#FFFFFF")
                    }
                    onClick = {
                        uploadInputRef.current?.click()
                    }
                    +"Upload"
                }
            }

            Box {
                sx {
                    marginRight = 20.px
                }
                Button {
                    startIcon = GetAppOutlined.create()
                    sx {
                        color = Color("#FFFFFF")
                    }
                    onClick = {
                        if (currentPage != null) {
                            val editorText = currentPage.theory
                            val fileName = currentPage.id.name
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
                    }
                    +"Download"
                }
            }

            Box {
                sx {
                    flexGrow = number(1.0)
                    display = Display.block
                }
                Button {
                    startIcon = DeleteForever.create()
                    sx {
                        color = Color("#FFFFFF")
                    }
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
                    +"Delete"
                }
            }

            Box {
                sx {
                    display = Display.block
                    marginRight = 20.px
                }

                IconButton {
                    onClick = {
                        isDialogOpen = true
                    }
                    size = Size.large
                    Info()
                }

                Switch {
                    icon = Brightness7.create()
                    checkedIcon = Brightness4.create()
                    checked = (theme == Themes.Dark)

                    onChange = { _, checked ->
                        theme = if (checked) Themes.Dark else Themes.Light
                    }
                }
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
