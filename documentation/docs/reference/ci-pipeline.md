# CI/CD pipeline

2P-Kt's continuous integration and delivery run entirely on GitHub Actions (there is no GitLab CI/CD involved,
despite what older documentation said). Three workflow files, chained together:

## `dispatcher.yml`

The entry point, triggered on every `push` (excluding Dependabot branches and doc/metadata-only changes) and
every `pull_request`. It gates external pull requests (a PR from a fork only proceeds if it targets
`dependabot/*`) and then calls `build-and-deploy.yml` as a reusable workflow.

## `build-and-deploy.yml`

```yaml
--8<-- ".github/workflows/build-and-deploy.yml:6:28"
```

The `build` job runs `./gradlew check --parallel --continue` and a **dry-run** deploy (publishes locally and
runs the Maven Central Portal release steps with `--dry-run`, to catch publication problems without actually
releasing). It then fans out to `test-extensively.yml` (see below). Only if both succeed does the `release`
job run for real: it installs Node, then runs `npx semantic-release`, which:

- determines the next version from conventional-commit messages since the last release (see
  [Contribute with Git Flow](../how-to/contribute-with-git-flow.md)),
- publishes JVM/JS artifacts to Maven Central (via OIDC — no long-lived credentials) and to the GitHub Packages
  Maven registry,
- publishes JS artifacts to npm under the `@tuprolog` scope,
- tags the release and updates `CHANGELOG.md` on GitHub.

A final `success` job aggregates the results of `build`, `test-extensively`, and `release` so branch protection
can depend on a single required check.

## `test-extensively.yml`

Runs the real test suite across a compatibility matrix, so a change is validated on every officially supported
combination before release:

- **JVM matrix**: `windows-latest` / `macos-latest` / `ubuntu-latest` × Java `17` / `21` / `25` ×
  `oracle` / `temurin` distributions — `./gradlew jvmMainClasses jvmTestClasses` then `./gradlew jvmTest`.
- **JS matrix**: `windows-latest` / `macos-latest` / `ubuntu-latest` × Node `latest-22` / `latest-24`.

Both matrices use `fail-fast: false` (one failing combination doesn't cancel the others) and cache test results
per-combination keyed on source/build-file hashes, so an unrelated change doesn't re-run everything.

## Documentation deployment

This site is built and deployed by a separate workflow, `.github/workflows/docs.yml`, on push to `master`: it
builds the MkDocs site and the aggregated Dokka API reference via `./gradlew :documentation:assembleSite`, then
publishes the result to GitHub Pages using GitHub's native Pages Actions (`upload-pages-artifact` /
`deploy-pages`).
