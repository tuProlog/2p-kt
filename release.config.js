var forceVersion = "-PforceVersion=${nextRelease.version}"
// Plain (non-template-literal) string: kept literal here so `@semantic-release/exec`'s own templating
// substitutes `${nextRelease.version}` at actual release time, once it's known -- interpolating it now, at
// config-load time, would throw (nextRelease doesn't exist yet). Recorded to a $RUNNER_TEMP file (rather than
// directly to $GITHUB_OUTPUT) because this command runs inside build-check-deploy-gradle-action's own
// "Deploy" step, which the action doesn't expose an output for -- a later, own step in the same job (see
// build-and-deploy.yml's release job) reads this file and republishes it as a real job output, which a later
// job in the same workflow run (see docs.yml) uses to force its own Gradle invocations to this exact version,
// rather than recomputing it (and risking a mismatch, e.g. if a commit landed on the branch in between).
var recordVersionCmd = 'echo "${nextRelease.version}" > "$RUNNER_TEMP/last-release-version.txt"'

var publishCmd = `
./gradlew ${forceVersion} publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication || exit 3
./gradlew ${forceVersion} publishJsPackageToNpmjsRegistry || true
${recordVersionCmd}
`
var prepareCmd = `
./gradlew ${forceVersion} dokkaGenerateHtml || true
./gradlew ${forceVersion} allShadowJars || exit 4
./gradlew ${forceVersion} :ide-web:zipWebDistribution || exit 5
`

var config = require('semantic-release-preconfigured-conventional-commits');
config.plugins.push(
    [
        "@semantic-release/exec",
        {
            "prepareCmd": prepareCmd,
            "publishCmd": publishCmd,
        }
    ],
    [
        "@semantic-release/github",
        {
            "assets": [
                { "path": "**/build/**/2p*redist*.jar" },
                { "path": "**/build/**/ide-web*.zip" }
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
