package it.unibo.tuprolog.ui.gui

import java.util.concurrent.Executors

actual object Runner4Tests : Runner by DefaultJvmRunner(Executors.newSingleThreadExecutor())
