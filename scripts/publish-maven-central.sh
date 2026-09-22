#!/usr/bin/env bash
# Publishes every subproject to Maven Central Portal, retrying only the subprojects whose
# zip/release task actually failed on the first attempt.
#
# Maven Central Portal publishes each subproject as its own independent bundle upload/deployment
# (see org.danilopianini:publish-on-central's PublishPortalDeployment): a subproject that fails
# validation/release there is a dead deployment, it can't be resurrected by re-running the same
# command. Without --continue, a single subproject's release failure stops Gradle from even
# ATTEMPTING the rest (fail-fast), so one flaky upload used to fail the whole release wholesale.
# --continue fixes that half; the other half is that blindly re-running the full task list would
# re-upload the subprojects that already succeeded too, and Central rejects a duplicate upload of
# an already-published GAV+version, turning already-done work into new "failures". So: run once
# with --continue, and if (and only if) some subprojects' zip/release tasks are what failed, retry
# EXACTLY those subprojects (never the ones that already succeeded, and never anything that failed
# for some other reason, e.g. a real local packaging bug in publishAllPublicationsToProjectLocalRepository
# -- that fails fast immediately, no blind retry). If a subproject still fails on the retry, this
# still exits non-zero: check its deployment on https://central.sonatype.com/publishing/deployments,
# and if it's genuinely stuck, `./gradlew -PforceVersion=<version> :<module>:zipMavenCentralPortalPublication
# :<module>:releaseMavenCentralPortalPublication` re-publishes just that one module by hand.
#
# Usage: scripts/publish-maven-central.sh -PforceVersion=<version>

set -uo pipefail

gradleVersionArg="$1"
log="$(mktemp)"

./gradlew "$gradleVersionArg" publishAllPublicationsToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication --continue > "$log" 2>&1
status=$?
cat "$log"

if [ "$status" -ne 0 ]; then
  failedModules=$(
    grep -oE "Execution failed for task ':[^:]+:(zip|release)MavenCentralPortalPublication'" "$log" |
      sed -E "s/Execution failed for task ':([^:]+):.*/\1/" |
      sort -u
  )
  if [ -n "$failedModules" ]; then
    echo "Retrying Maven Central publish for: $failedModules"
    retryTasks=""
    for m in $failedModules; do
      retryTasks="$retryTasks :$m:zipMavenCentralPortalPublication :$m:releaseMavenCentralPortalPublication"
    done
    # shellcheck disable=SC2086 # retryTasks is an intentional space-separated list of task names
    ./gradlew "$gradleVersionArg" $retryTasks --continue || exit 3
  else
    exit 3
  fi
fi

rm -f "$log"
