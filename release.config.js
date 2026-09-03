var publishCmd = `
./gradlew publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication || exit 3
./gradlew publishJsPackageToNpmjsRegistry || true
`
var prepareCmd = `
./gradlew dokkaHtml || exit 4
./gradlew allShadowJars || exit 5
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
