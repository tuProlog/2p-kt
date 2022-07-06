plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

packageJson {
    dependencies = mutableMapOf(
        "kotlin" to libs.kotlin.stdlib.js.version,
    )
}
