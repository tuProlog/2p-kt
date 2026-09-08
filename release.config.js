var forceVersion = "-PforceVersion=${nextRelease.version}"

var publishCmd = `
./gradlew ${forceVersion} publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication || exit 3
./gradlew ${forceVersion} publishJsPackageToNpmjsRegistry || true
`
var prepareCmd = `
./gradlew ${forceVersion} dokkaGenerateHtml || true
./gradlew ${forceVersion} allShadowJars || exit 4
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
                { "path": "**/build/**/2p*redist*.jar" }
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
