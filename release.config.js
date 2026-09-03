var publishCmd = `
./gradlew publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication || exit 3
./gradlew publishJsPackageToNpmjsRegistry || true
`
var prepareCmd = `
./gradlew dokkaGenerateHtml || true
./gradlew allShadowJars || exit 4
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
                { "path": "**/build/**/*redist*.jar" }
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
