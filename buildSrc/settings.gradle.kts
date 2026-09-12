// buildSrc is a separate Gradle build, so the root's auto-detected `gradle/libs.versions.toml` catalog is not
// shared with it implicitly; wiring the same file here explicitly lets buildSrc's build.gradle.kts reference
// the very same `libs` catalog as every other subproject, instead of hardcoding dependency coordinates.
// Repositories are left to buildSrc/build.gradle.kts's own `repositories { ... }` block, unaffected by this.
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
