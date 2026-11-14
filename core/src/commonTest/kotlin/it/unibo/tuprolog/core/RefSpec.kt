package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.utils.setTag

private class RejectedPayloadCase(
    val id: String,
    val payload: () -> Term,
)

private class RefFactoryCase(
    val id: String,
    val build: (Any) -> Ref,
)

private class RefOrderCase(
    val id: String,
    val smaller: () -> Term,
    val greater: () -> Term,
)

private data class HostValue(
    val content: String,
)

private class SecretHolder {
    override fun toString(): String = "s3cr3t"
}

private class TestObjectRef(
    value: Any,
    tags: Map<String, Any> = emptyMap(),
) : ObjectRef(value, tags) {
    override fun copyWithTags(tags: Map<String, Any>): ObjectRef = TestObjectRef(value, tags)
}

class RefSpec :
    FunSpec({
        val rejectedPayloads =
            listOf(
                RejectedPayloadCase("atom") { Atom.of("a") },
                RejectedPayloadCase("integer") { Integer.ONE },
                RejectedPayloadCase("real") { Real.ONE_HALF },
                RejectedPayloadCase("var") { Var.of("X") },
                RejectedPayloadCase("struct") { Struct.of("f", Atom.of("a")) },
                RejectedPayloadCase("pattern") { Pattern.of(Var.of("P"), Atom.of("a")) },
                RejectedPayloadCase("empty-list") { EmptyList() },
                RejectedPayloadCase("null-ref") { NullRef() },
                RejectedPayloadCase("object-ref") { ObjectRef.of(Any()) },
            )

        withTests(nameFn = { "rejects-term-payload/ref-of/${it.id}" }, rejectedPayloads) { case ->
            shouldThrow<IllegalArgumentException> { Ref.of(case.payload()) }
        }

        withTests(nameFn = { "rejects-term-payload/object-ref-of/${it.id}" }, rejectedPayloads) { case ->
            shouldThrow<IllegalArgumentException> { ObjectRef.of(case.payload()) }
        }

        withTests(nameFn = { "rejects-term-payload/scope-ref-of/${it.id}" }, rejectedPayloads) { case ->
            shouldThrow<IllegalArgumentException> { Scope.empty { refOf(case.payload()) } }
        }

        withTests(nameFn = { "rejects-term-payload/scope-object-ref-of/${it.id}" }, rejectedPayloads) { case ->
            shouldThrow<IllegalArgumentException> { Scope.empty { objectRefOf(case.payload()) } }
        }

        withTests(nameFn = { "rejects-term-payload/subclass-constructor/${it.id}" }, rejectedPayloads) { case ->
            shouldThrow<IllegalArgumentException> { TestObjectRef(case.payload()) }
        }

        withTests(
            nameFn = { "factory/non-null/${it.id}" },
            RefFactoryCase("ref-of") { Ref.of(it) },
            RefFactoryCase("object-ref-of") { ObjectRef.of(it) },
            RefFactoryCase("scope-ref-of") { Scope.empty { refOf(it) } },
            RefFactoryCase("scope-object-ref-of") { Scope.empty { objectRefOf(it) } },
        ) { case ->
            val host = HostValue("x")
            val ref = case.build(host)
            ref.shouldBeInstanceOf<ObjectRef>()
            ref.value shouldBeSameInstanceAs host
            ref.isNullRef.shouldBeFalse()
        }

        test("factory/null/ref-of-is-null-ref-instance") {
            Ref.of(null) shouldBeSameInstanceAs NullRef.instance
            Scope.empty { refOf(null) } shouldBeSameInstanceAs NullRef.instance
            (Ref.of(null) is ObjectRef).shouldBeFalse()
        }

        test("factory/null/invoke-and-scope-are-equal-to-instance") {
            NullRef() shouldBe NullRef.instance
            Scope.empty { nullRefOf() } shouldBe NullRef.instance
            NullRef().isNullRef.shouldBeTrue()
        }

        test("null-ref/value-is-null") {
            val value: Nothing? = NullRef().value
            value.shouldBeNull()
            Ref.of(null).value.shouldBeNull()
        }

        test("null-ref/tagged-copy") {
            val original = NullRef()
            val tagged = original.setTag("origin", "test")
            tagged.shouldBeInstanceOf<NullRef>()
            tagged shouldNotBeSameInstanceAs original
            tagged.tags shouldBe mapOf("origin" to "test")
            original.tags shouldBe emptyMap()
            tagged shouldBe original
            tagged.hashCode() shouldBe original.hashCode()
            tagged.compareTo(original) shouldBe 0
            tagged.structurallyEquals(original).shouldBeTrue()
        }

        test("null-ref/fresh-copy-and-apply-preserve-instance") {
            val ref = NullRef().setTag("origin", "test")
            ref.freshCopy() shouldBeSameInstanceAs ref
            Scope.empty { ref.freshCopy(this) } shouldBeSameInstanceAs ref
            ref.apply(Substitution.of(Var.of("X"), Atom.of("a"))) shouldBeSameInstanceAs ref
        }

        test("object-ref/equal-iff-same-host-instance") {
            val host = HostValue("x")
            val sameValued = HostValue("x")
            ObjectRef.of(host) shouldBe ObjectRef.of(host)
            ObjectRef.of(host).hashCode() shouldBe ObjectRef.of(host).hashCode()
            ObjectRef.of(host) shouldNotBe ObjectRef.of(sameValued)
            ObjectRef.of(host).structurallyEquals(ObjectRef.of(host)).shouldBeTrue()
            ObjectRef.of(host).structurallyEquals(ObjectRef.of(sameValued)).shouldBeFalse()
        }

        test("object-ref/equal-across-subclasses-wrapping-same-host") {
            val host = HostValue("x")
            ObjectRef.of(host) shouldBe TestObjectRef(host)
            TestObjectRef(host) shouldBe ObjectRef.of(host)
        }

        test("object-ref/collections-by-identity") {
            val list = mutableListOf(1)
            ObjectRef.of(list) shouldBe ObjectRef.of(list)
            ObjectRef.of(list) shouldNotBe ObjectRef.of(mutableListOf(1))
            ObjectRef.of(mutableSetOf(1)) shouldNotBe ObjectRef.of(mutableSetOf(1))
        }

        test("refs-never-equal-atoms") {
            ObjectRef.of("a") shouldNotBe Atom.of("a")
            Atom.of("a") shouldNotBe ObjectRef.of("a")
            NullRef() shouldNotBe Atom.of("null")
            Atom.of("null") shouldNotBe NullRef()
            NullRef() shouldNotBe ObjectRef.of(Any())
        }

        test("object-ref/tagged-copy") {
            val host = HostValue("x")
            val original = ObjectRef.of(host)
            val tagged = original.setTag("origin", "test")
            tagged.shouldBeInstanceOf<ObjectRef>()
            tagged shouldNotBeSameInstanceAs original
            tagged.value shouldBeSameInstanceAs host
            tagged.tags shouldBe mapOf("origin" to "test")
            original.tags shouldBe emptyMap()
            tagged shouldBe original
        }

        test("object-ref/fresh-copy-and-apply-preserve-instance") {
            val ref = ObjectRef.of(HostValue("x"))
            ref.freshCopy() shouldBeSameInstanceAs ref
            ref.apply(Substitution.of(Var.of("X"), Atom.of("a"))) shouldBeSameInstanceAs ref
        }

        withTests(
            nameFn = { "type-checks/${it.id}" },
            RefFactoryCase("object-ref") { Ref.of(it) },
            RefFactoryCase("null-ref") { NullRef() },
        ) { case ->
            val ref: Term = case.build(HostValue("x"))
            ref.isRef.shouldBeTrue()
            ref.isConstant.shouldBeTrue()
            ref.isGround.shouldBeTrue()
            ref.variables.toList().shouldBeEmpty()
            ref.isAtom.shouldBeFalse()
            ref.isNumber.shouldBeFalse()
            ref.isStruct.shouldBeFalse()
            ref.isCompound.shouldBeFalse()
            ref.isVar.shouldBeFalse()
            ref.asRef() shouldBeSameInstanceAs ref
            ref.castToRef() shouldBeSameInstanceAs ref
            ref.asConstant() shouldBeSameInstanceAs ref
            ref.isObjectRef shouldBe (ref is ObjectRef)
            ref.isNullRef shouldBe (ref is NullRef)
            if (ref.isObjectRef) {
                ref.castToObjectRef() shouldBeSameInstanceAs ref
                ref.asNullRef().shouldBeNull()
                shouldThrow<ClassCastException> { ref.castToNullRef() }
            } else {
                ref.castToNullRef() shouldBeSameInstanceAs ref
                ref.asObjectRef().shouldBeNull()
                shouldThrow<ClassCastException> { ref.castToObjectRef() }
            }
        }

        test("type-checks/non-refs") {
            Atom.of("a").isRef.shouldBeFalse()
            Atom.of("a").asRef().shouldBeNull()
            Integer.ONE.isObjectRef.shouldBeFalse()
            Var.of("X").isNullRef.shouldBeFalse()
            shouldThrow<ClassCastException> { Atom.of("a").castToRef() }
        }

        test("visitor/chain") {
            val visitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "default"

                    override fun visitConstant(term: Constant) = "constant"

                    override fun visitNullRef(term: NullRef) = "null-ref"
                }
            ObjectRef.of(Any()).accept(visitor) shouldBe "constant"
            NullRef().accept(visitor) shouldBe "null-ref"
            val refVisitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "default"

                    override fun visitRef(term: Ref) = "ref"
                }
            ObjectRef.of(Any()).accept(refVisitor) shouldBe "ref"
            NullRef().accept(refVisitor) shouldBe "ref"
            val objectRefVisitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "default"

                    override fun visitObjectRef(term: ObjectRef) = "object-ref"
                }
            ObjectRef.of(Any()).accept(objectRefVisitor) shouldBe "object-ref"
            NullRef().accept(objectRefVisitor) shouldBe "default"
        }

        test("format/opaque") {
            val formatted = ObjectRef.of(SecretHolder()).toString()
            formatted shouldStartWith "<object:"
            formatted shouldNotContain "s3cr3t"
            TermFormatter.default().format(ObjectRef.of(SecretHolder())) shouldNotContain "s3cr3t"
            NullRef().toString() shouldBe "<null-ref>"
            TermFormatter.default().format(NullRef()) shouldBe "<null-ref>"
        }

        withTests(
            nameFn = { "order/${it.id}" },
            RefOrderCase("integer-before-ref", { Integer.ONE }, { ObjectRef.of(Any()) }),
            RefOrderCase("atom-before-null-ref", { Atom.of("zzz") }, { NullRef() }),
            RefOrderCase("atom-before-object-ref", { Atom.of("zzz") }, { ObjectRef.of(Any()) }),
            RefOrderCase("null-ref-before-object-ref", { NullRef() }, { ObjectRef.of(Any()) }),
            RefOrderCase("object-ref-before-compound", { ObjectRef.of(Any()) }, { Struct.of("a", Atom.of("a")) }),
            RefOrderCase("null-ref-before-compound", { NullRef() }, { Struct.of("a", Atom.of("a")) }),
        ) { case ->
            val smaller = case.smaller()
            val greater = case.greater()
            smaller.compareTo(greater) shouldBeLessThan 0
            greater.compareTo(smaller) shouldBeGreaterThan 0
        }

        test("order/same-host-is-zero") {
            val host = HostValue("x")
            ObjectRef.of(host).compareTo(ObjectRef.of(host).setTag("a", "b")) shouldBe 0
            NullRef().compareTo(NullRef.instance) shouldBe 0
        }

        test("order/distinct-hosts-antisymmetric-and-transitive") {
            val refs = (1..64).map { ObjectRef.of(HostValue("x")) }
            for (x in refs) {
                for (y in refs) {
                    val xy = x.compareTo(y)
                    val yx = y.compareTo(x)
                    if (x === y) {
                        xy shouldBe 0
                    } else {
                        xy shouldNotBe 0
                        (xy < 0) shouldBe (yx > 0)
                    }
                }
            }
            val sorted = refs.sorted()
            sorted.zipWithNext().all { (x, y) -> x < y }.shouldBeTrue()
        }
    })
