package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.OutputChannel

abstract class AbstractOutputChannel<T> : AbstractChannel<T>(), OutputChannel<T>