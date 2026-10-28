package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RefsSpec :
    FunSpec({
        @Suppress("SwallowedException")
        fun testMethodInvocation(
            cases: List<TestDatum>,
            detectorCreator: () -> OverloadDetector = OverloadDetector.Companion::create,
            refCreator: (OverloadDetector) -> Ref = ObjectRef.Companion::of,
            case2Term: (TestDatum) -> Term,
        ) {
            val obj = detectorCreator()
            val ref = refCreator(obj)
            for (case in cases) {
                try {
                    val result = ref.invoke("call", case2Term(case))
                    assertFalse { case.isFailed }
                    assertTrue { result is Result.Value }
                    assertEquals(case.string, result.asObjectRef()?.`object`)
                    assertEquals(Atom.of(case.string), result.toTerm())
                } catch (e: OopRuntimeException) {
                    assertTrue { case.term.let { it is Var || it is NullRef } }
                } catch (e: OopException) {
                    assertTrue { case.isFailed }
                    assertEquals(case.exception!!::class, e::class)
                }
            }
            var result = ref.invoke("size")
            val expectedSize = cases.size - cases.count { it.isFailed }
            assertEquals(Integer.of(expectedSize), result.toTerm())
            result = ref.invoke("toList")
            val list = result.asObjectRef()?.`object` as List<*>
            assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! }, list)
            assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! to it.type }, obj.recordings)
        }

        @Suppress("SwallowedException")
        fun testConstructorInvocation(
            case: TestDatum,
            case2Term: (TestDatum) -> Term,
        ) {
            val ref = TypeRef.of(ConstructorOverloadDetector::class)
            try {
                val result = ref.create(case2Term(case))
                assertFalse { case.isFailed }
                assertTrue { result is Result.Value }
                assertTrue { result.asObjectRef()?.`object` is ConstructorOverloadDetector }
                val constructorOverloadDetector = result.asObjectRef()?.`object` as ConstructorOverloadDetector
                assertEquals(case.converted to case.type, constructorOverloadDetector.args)
            } catch (e: OopRuntimeException) {
                assertTrue { case.term.let { it is Var || it is NullRef } }
            } catch (e: OopException) {
                assertTrue { case.isFailed }
                assertEquals(case.exception!!::class, e::class)
            }
        }

        fun castTerm(case: TestDatum): Term = Struct.of(CAST_OPERATOR, case.term, Atom.of(case.type.fullName))

        test("most-proper-overload-is-selected-when-invoking-object-ref-method") {
            testMethodInvocation(Conversions.bestCases) { it.term }
        }

        test("overload-can-be-selected-via-explicit-cast-when-invoking-object-ref-method") {
            testMethodInvocation(Conversions.explicitCases) {
                Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
            }
        }

        test("overload-selection-may-fail-when-invoking-object-ref-method") {
            testMethodInvocation(Conversions.cornerCases) {
                Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
            }
        }

        test("most-proper-overload-is-selected-when-invoking-type-ref-method") {
            testMethodInvocation(
                Conversions.bestCases,
                detectorCreator = OverloadDetectorObject::refresh,
                refCreator = { TypeRef.of(OverloadDetectorObject::class) },
            ) { it.term }
        }

        test("overload-can-be-selected-via-explicit-cast-when-invoking-type-ref-method") {
            testMethodInvocation(
                Conversions.explicitCases,
                detectorCreator = OverloadDetectorObject::refresh,
                refCreator = { TypeRef.of(OverloadDetectorObject::class) },
            ) {
                Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
            }
        }

        test("overload-selection-may-fail-when-invoking-type-ref-method") {
            testMethodInvocation(
                Conversions.cornerCases,
                detectorCreator = OverloadDetectorObject::refresh,
                refCreator = { TypeRef.of(OverloadDetectorObject::class) },
            ) {
                Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
            }
        }

        withTests(nameFn = { "most-proper-constructor-is-selected/${it.id}" }, Conversions.bestCases) { case ->
            testConstructorInvocation(case, TestDatum::term)
        }

        withTests(
            nameFn = { "constructor-can-be-selected-via-explicit-cast/${it.id}" },
            Conversions.explicitCases,
        ) { case ->
            testConstructorInvocation(case, ::castTerm)
        }

        withTests(nameFn = { "constructor-selection-may-fail/${it.id}" }, Conversions.cornerCases) { case ->
            testConstructorInvocation(case, ::castTerm)
        }
    })
