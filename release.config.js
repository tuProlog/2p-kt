var staging = "-PstagingRepositoryId=${process.env.STAGING_REPO_ID}"
var version = "-PforceVersion=${process.env.ENFORCE_VERSION}"

var publishCmd = `
./gradlew ${version} ${staging} releaseStagingRepositoryOnMavenCentral || exit 3
./gradlew ${version} ${staging} publishJsPackageToNpmjsRegistry || exit 4
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
