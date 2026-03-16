# JVM Console Application Template Checklist

- Project is created successfully from the Kotlin wizard (no errors in the event log).
- Gradle sync completes without failures.
- Generated project structure matches expected layout (`src/main/kotlin`, `build.gradle.kts`, `settings.gradle.kts`).
- Project SDK and Gradle JVM are auto-configured; no "JDK not defined" or "Gradle JVM missing" warnings.
- Kotlin language version and JVM target align with the configured JDK; no version-mismatch warnings.
- Initial indexing completes and the main source shows no unresolved references or red inspections.
- A full Gradle build (`build` or `assemble`) succeeds (compilation + packaging), not just sync.
- Gradle `run` task executes the main class and prints the expected template output.
- Default run configuration uses the correct module and working directory (no class/module not found).
- If tests are generated, the IDE detects them and the Gradle `test` task passes.
- A build artifact (jar) is produced and is runnable when the application plugin is present.
- Project/module name, group, and artifact in the Gradle model reflect the wizard inputs.
