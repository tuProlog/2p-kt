package it.unibo.tuprolog.ui.gui

internal data class FileContent(private var _text: String = "", var changed: Boolean = true) {
    var text: String
        get() = _text.also { changed = false }
        set(value) {
            if (value != _text) changed = true
            _text = value
        }
}
