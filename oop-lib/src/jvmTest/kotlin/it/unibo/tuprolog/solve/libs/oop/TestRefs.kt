package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestRefs {
    private fun testMethodInvocation(
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
        val expectedSize = cases.size - cases.filter { it.isFailed }.count()
        assertEquals(Integer.of(expectedSize), result.toTerm())
        result = ref.invoke("toList")
        val list = result.asObjectRef()?.`object` as List<*>
        assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! }, list)
        assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! to it.type }, obj.recordings)
    }

    private fun testConstructorInvocation(
        cases: List<TestDatum>,
        detectorType: KClass<*> = ConstructorOverloadDetector::class,
        case2Term: (TestDatum) -> Term,
    ) {
        val ref = TypeRef.of(detectorType)
        for (case in cases) {
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
    }

    @Test
    fun mostProperOverloadIsSelectedWhenInvokingObjectRefMethod() {
        testMethodInvocation(Conversions.bestCases) { it.term }
    }

    @Test
    fun overloadCanBeSelectedViaExplicitCastWhenInvokingObjectRefMethod() {
        testMethodInvocation(Conversions.explicitCases) {
            Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun overloadSelectionMayFailWhenInvokingObjectRefMethod() {
        testMethodInvocation(Conversions.cornerCases) {
            Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun mostProperOverloadIsSelectedWhenInvokingTypeRefMethod() {
        testMethodInvocation(
            Conversions.bestCases,
            detectorCreator = OverloadDetectorObject::refresh,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) },
        ) { it.term }
    }

    @Test
    fun overloadCanBeSelectedViaExplicitCastWhenInvokingTypeRefMethod() {
        testMethodInvocation(
            Conversions.explicitCases,
            detectorCreator = OverloadDetectorObject::refresh,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) },
        ) {
            Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun overloadSelectionMayFailWhenInvokingTypeRefMethod() {
        testMethodInvocation(
            Conversions.cornerCases,
            detectorCreator = OverloadDetectorObject::refresh,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) },
        ) {
            Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun mostProperConstructorIsSelectedWhenInstantiatingTypeRef() {
        testConstructorInvocation(Conversions.bestCases, ConstructorOverloadDetector::class) { it.term }
    }

    @Test
    fun constructorCanBeSelectedViaExplicitCastWhenInstantiatingTypeRef() {
        testConstructorInvocation(Conversions.explicitCases, ConstructorOverloadDetector::class) {
            Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun constructorSelectionMayFailWhenInstantiatingTypeRef() {
        testConstructorInvocation(Conversions.cornerCases, ConstructorOverloadDetector::class) {
            Struct.of(CAST_OPERATOR, it.term, Atom.of(it.type.fullName))
        }
    }
}
