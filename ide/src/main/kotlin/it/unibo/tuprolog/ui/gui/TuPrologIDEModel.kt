package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.TimeDuration

class TuPrologIDEModel(defaultTimeout: TimeDuration = Page.DEFAULT_TIMEOUT) :
    Application by Application.of(JavaFxRunner, defaultTimeout = defaultTimeout)
