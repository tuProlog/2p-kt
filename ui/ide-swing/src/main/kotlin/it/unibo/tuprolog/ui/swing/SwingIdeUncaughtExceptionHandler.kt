package it.unibo.tuprolog.ui.swing

import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea

private const val REPORT_AREA_ROWS = 24
private const val REPORT_AREA_COLUMNS = 100

internal class SwingIdeUncaughtExceptionHandler(
    private val frame: () -> SwingIdeFrame?,
    private val fallback: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        error: Throwable,
    ) {
        onEdt {
            val ide = frame()
            if (ide == null) {
                fallback?.uncaughtException(thread, error)
            } else {
                val details = ide.supportReport(thread, error)
                val area = JTextArea(details, REPORT_AREA_ROWS, REPORT_AREA_COLUMNS).apply { caretPosition = 0 }
                JOptionPane.showMessageDialog(
                    ide,
                    JScrollPane(area),
                    "Unexpected error — please report it",
                    JOptionPane.ERROR_MESSAGE,
                )
            }
        }
    }

    internal companion object {
        fun stackTrace(error: Throwable): String =
            StringWriter().also { error.printStackTrace(PrintWriter(it)) }.toString()
    }
}
