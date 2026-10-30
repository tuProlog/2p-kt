package it.unibo.tuprolog.solve.exception

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.aType
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertEqualPrologErrorData
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertErrorStructCorrect
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.someExtraData
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [LogicError]
 *
 * @author Enrico
 */
class LogicErrorSpec :
    FunSpec({
        val underTestPrologError =
            object : LogicError(aMessage, aCause, aContext, aType, someExtraData) {
                override fun updateContext(
                    newContext: ExecutionContext,
                    index: Int,
                ): LogicError =
                    of(
                        this.message,
                        this.cause,
                        this.contexts.setItem(index, newContext),
                        this.type,
                        this.extraData,
                    )

                override fun updateLastContext(newContext: ExecutionContext): LogicError =
                    updateContext(
                        newContext,
                        contexts.lastIndex,
                    )

                override fun pushContext(newContext: ExecutionContext): LogicError =
                    of(this.message, this.cause, this.contexts.addLast(newContext), this.type, this.extraData)
            }

        val logicErrorTypeToInstanceMap =
            LogicErrorUtils.recognizedSubTypes.map { (typeParam, _) ->
                typeParam to LogicError.of(aMessage, aCause, aContext, typeParam, someExtraData)
            }

        test("holds-inserted-data") {
            assertEqualPrologErrorData(aMessage, aCause, aContext, aType, someExtraData, underTestPrologError)
        }

        test("defaults-are-null") {
            val toBeTested =
                object : LogicError(context = aContext, type = aType) {
                    override fun updateContext(
                        newContext: ExecutionContext,
                        index: Int,
                    ): LogicError =
                        of(
                            this.message,
                            this.cause,
                            this.contexts.setItem(index, newContext),
                            this.type,
                            this.extraData,
                        )

                    override fun updateLastContext(newContext: ExecutionContext): LogicError =
                        updateContext(
                            newContext,
                            contexts.lastIndex,
                        )

                    override fun pushContext(newContext: ExecutionContext): LogicError =
                        of(this.message, this.cause, this.contexts.addLast(newContext), this.type, this.extraData)
                }
            assertNull(toBeTested.message)
            assertNull(toBeTested.cause)
            assertNull(toBeTested.extraData)
        }

        test("constructor-inserts-message-if-only-cause-specified") {
            val logicError =
                object : LogicError(aCause, aContext, aType, someExtraData) {
                    override fun updateContext(
                        newContext: ExecutionContext,
                        index: Int,
                    ): LogicError =
                        of(
                            this.message,
                            this.cause,
                            this.contexts.setItem(index, newContext),
                            this.type,
                            this.extraData,
                        )

                    override fun updateLastContext(newContext: ExecutionContext): LogicError =
                        updateContext(
                            newContext,
                            contexts.lastIndex,
                        )

                    override fun pushContext(newContext: ExecutionContext): LogicError =
                        of(this.message, this.cause, this.contexts.addLast(newContext), this.type, this.extraData)
                }

            assertEquals(aCause.toString(), logicError.message)
        }

        test("error-is-computed-correctly") {
            assertErrorStructCorrect(underTestPrologError)
            assertErrorStructCorrect(
                object : LogicError(context = aContext, type = aType) {
                    override fun updateContext(
                        newContext: ExecutionContext,
                        index: Int,
                    ): LogicError =
                        of(
                            this.message,
                            this.cause,
                            this.contexts.setItem(index, newContext),
                            this.type,
                            this.extraData,
                        )

                    override fun updateLastContext(newContext: ExecutionContext): LogicError =
                        updateContext(
                            newContext,
                            contexts.lastIndex,
                        )

                    override fun pushContext(newContext: ExecutionContext): LogicError =
                        of(this.message, this.cause, this.contexts.addLast(newContext), this.type, this.extraData)
                },
            )
        }

        test("update-context-returns-exception-with-same-contents-but-updated-context") {
            val allPrologErrorInstances = logicErrorTypeToInstanceMap + (aType to underTestPrologError)

            allPrologErrorInstances.forEach { (type, prologError) ->
                val toBeTested = prologError.updateContext(aDifferentContext)

                assertEqualPrologErrorData(aMessage, aCause, aDifferentContext, type, someExtraData, toBeTested)
            }
        }

        test("of-creates-correct-sub-instance-if-detected") {
            LogicErrorUtils.recognizedSubTypes.forEach { (typeParam, expectedType) ->
                val actualError = LogicError.of(context = aContext, type = typeParam)
                assertEquals(expectedType, actualError::class)
            }
        }
    })
