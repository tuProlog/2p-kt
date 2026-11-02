package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KotlinToJavaMappingSpec :
    FunSpec({
        test("content-of-mapping") {
            KotlinToJavaTypeMap["kotlin.Any"] shouldBe Any::class
            KotlinToJavaTypeMap["kotlin.String"] shouldBe String::class
            KotlinToJavaTypeMap["kotlin.CharSequence"] shouldBe CharSequence::class
            KotlinToJavaTypeMap["kotlin.Throwable"] shouldBe Throwable::class
            KotlinToJavaTypeMap["kotlin.Cloneable"] shouldBe Cloneable::class
            KotlinToJavaTypeMap["kotlin.Number"] shouldBe Number::class
            KotlinToJavaTypeMap["kotlin.Comparable"] shouldBe Comparable::class
            KotlinToJavaTypeMap["kotlin.Enum"] shouldBe Enum::class
            KotlinToJavaTypeMap["kotlin.Annotation"] shouldBe Annotation::class
            KotlinToJavaTypeMap["kotlin.collections.Iterable"] shouldBe Iterable::class
            KotlinToJavaTypeMap["kotlin.collections.Iterator"] shouldBe Iterator::class
            KotlinToJavaTypeMap["kotlin.collections.Collection"] shouldBe Collection::class
            KotlinToJavaTypeMap["kotlin.collections.List"] shouldBe List::class
            KotlinToJavaTypeMap["kotlin.collections.Set"] shouldBe Set::class
            KotlinToJavaTypeMap["kotlin.collections.ListIterator"] shouldBe ListIterator::class
            KotlinToJavaTypeMap["kotlin.collections.Map"] shouldBe Map::class
            KotlinToJavaTypeMap["kotlin.collections.Map.Entry"] shouldBe Map.Entry::class
            KotlinToJavaTypeMap["kotlin.Boolean"] shouldBe Boolean::class
            KotlinToJavaTypeMap["kotlin.Char"] shouldBe Char::class
            KotlinToJavaTypeMap["kotlin.Byte"] shouldBe Byte::class
            KotlinToJavaTypeMap["kotlin.Short"] shouldBe Short::class
            KotlinToJavaTypeMap["kotlin.Int"] shouldBe Int::class
            KotlinToJavaTypeMap["kotlin.Float"] shouldBe Float::class
            KotlinToJavaTypeMap["kotlin.Long"] shouldBe Long::class
            KotlinToJavaTypeMap["kotlin.Double"] shouldBe Double::class
        }
    })
