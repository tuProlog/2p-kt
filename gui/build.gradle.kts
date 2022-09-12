plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":parser-theory"))
                api(project(":solve"))
                api(project(":io-utils"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("parser-theory"),
        npmSubproject("solve"),
        npmSubproject("io-utils"),
    )
}