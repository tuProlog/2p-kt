var publishCmd = `
./gradlew publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication || exit 3
./gradlew publishJsPackageToNpmjsRegistry || true
`
var prepareCmd = `
rm -rf release-assets
./gradlew clean allShadowJars || exit 4
mkdir -p release-assets
find . -type f -path "*/build/*" -name "*redist*.jar" -exec cp "{}" release-assets/ \\;
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
                { "path": "release-assets/*redist*.jar" }
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
