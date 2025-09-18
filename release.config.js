var publishCmd = `
./gradlew publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication || exit 3
./gradlew publishJsPackageToNpmjsRegistry || exit 4
./gradlew -PotherMavenPassword=$GITHUB_TOKEN publishAllPublicationsToGithubRepository || true
`

var config = require('semantic-release-preconfigured-conventional-commits');
config.plugins.push(
    [
        "@semantic-release/exec",
        {
            "publishCmd": publishCmd,
        }
    ],
    [
        "@semantic-release/github",
        {
            "assets": [
                { "path": "**/build/**/*redist*.jar" },
                { "path": "**/build/**/*full*.jar" },
                { "path": "**/build/**/*javadoc*.jar" },
                { "path": "build/**/*javadoc*.zip" }
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
