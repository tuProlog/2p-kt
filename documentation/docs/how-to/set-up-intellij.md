# Set up IntelliJ IDEA for 2P-Kt development

How to open the 2P-Kt project in IntelliJ IDEA ready to build, run, and debug.

## 1. Install prerequisites

- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) — the free Community edition is enough. The
  bundled **Kotlin** plugin (installed by default) is all you need; no extra plugin is required for a
  Gradle Kotlin DSL project like this one.
- A JDK compatible with the project — the CI build matrix currently tests against JDK 17, 21 and 25, so any of
  those works.

You don't need a separate Gradle installation: the repository ships a Gradle wrapper (`./gradlew`) pinned to
the exact Gradle version the build expects.

## 2. Clone the repository

```bash
git clone https://github.com/tuProlog/2p-kt.git
```

## 3. Open the project

1. From IntelliJ's welcome screen (or **File > Open** if you already have a project open), choose **Open**.
2. Select the cloned `2p-kt` folder itself (the one containing `settings.gradle.kts`) and confirm.
3. IntelliJ detects the Gradle Kotlin DSL build automatically and starts importing it — no "Import Project
   from external model" step is needed on modern IntelliJ versions.
4. Wait for the initial Gradle sync to finish. It can take a while the first time, since the project has
   ~30 modules and their dependencies to resolve.

## 4. Double-check the Gradle settings (only if sync fails)

Open **Settings/Preferences > Build, Execution, Deployment > Build Tools > Gradle** and make sure:

- **Use Gradle from** is set to **'gradle-wrapper.properties' file**.
- **Gradle JVM** points at a JDK matching one of the versions above.

Enabling auto-import is also recommended so IntelliJ picks up build script changes automatically.

## Verify

Run a module's tests from the IDE (right-click a module, e.g. `core`, in the Project view and choose **Run
Tests**), or run the whole build from the built-in terminal:

```bash
./gradlew build
```

A green build confirms the IDE is wired up correctly.
