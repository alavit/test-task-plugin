# JVM Console Application Template — Test Checklist

## 1. Project Creation (Happy Path)

- [ ] Project is created successfully from the Kotlin wizard with default settings (no errors in Event Log).
- [ ] Wizard respects user-specified project name, location, group ID, and artifact ID.
- [ ] Generated project structure matches expected layout (`src/main/kotlin/`, `build.gradle.kts`, `settings.gradle.kts`, `gradle/wrapper/`).
- [ ] `Main.kt` is generated with a valid `main` function inside the correct package.
- [ ] Gradle wrapper files are present and the wrapper version is appropriate.

## 2. Project Creation (Negative / Edge Cases)

- [ ] Cancelling the wizard at any step does not create a partial project or leave stale state.
- [ ] Creating a project with spaces in the name/path works correctly.
- [ ] Creating a project with special characters in the name (`#`, `&`, unicode) is handled gracefully.
- [ ] Creating a project in a read-only directory shows an informative error.
- [ ] Re-creating a project with the same name in the same directory warns the user or handles the conflict.
- [ ] Creating a project when no JDK is configured shows a clear error/prompt to configure one.

## 3. SDK and Toolchain

- [ ] Project SDK is auto-configured; no "JDK not defined" or "Gradle JVM missing" warnings.
- [ ] Kotlin language version and JVM target align with the configured JDK; no version-mismatch warnings.
- [ ] Project works with different JDK versions (11, 17, 21).
- [ ] Project works with different JDK vendors (Oracle, Corretto, GraalVM, Temurin).

## 4. Gradle Sync and Build

- [ ] Gradle sync completes without failures after project creation.
- [ ] A full Gradle `build` (compilation + packaging) succeeds.
- [ ] Gradle `assemble` task produces the expected output artifacts.
- [ ] No deprecation warnings in the Gradle build output for the generated build scripts.
- [ ] `build.gradle.kts` contains correct plugin declarations, dependencies, and Kotlin JVM configuration.

## 5. Run and Execution

- [ ] Gradle `run` task executes the main class and prints the expected template output.
- [ ] Default Run Configuration is auto-detected and uses the correct module and main class.
- [ ] Running from the gutter icon (line marker) on `fun main()` works correctly.
- [ ] Process exits with code 0 on successful execution.
- [ ] Console output is displayed correctly in the Run tool window.

## 6. Debug Support

- [ ] Setting a breakpoint in `Main.kt` and starting a debug session stops at the breakpoint.
- [ ] Step-over, step-into, and step-out work correctly during a debug session.
- [ ] Variable inspection and evaluation work in the debugger.
- [ ] Debug session can be terminated gracefully.

## 7. IDE Features in Generated Project

- [ ] Initial indexing completes and `Main.kt` shows no unresolved references or red inspections.
- [ ] Code completion works for standard library functions (e.g., `println`, `listOf`).
- [ ] Go to Declaration navigates to the correct source/decompiled class.
- [ ] Find Usages works for symbols defined in the generated code.
- [ ] Rename refactoring (e.g., renaming the `main` function parameter) works without errors.
- [ ] Live templates (e.g., `main`, `sout`) expand correctly in Kotlin files.
- [ ] Code formatting (`Code -> Reformat Code`) produces well-formatted output.

## 8. Testing Support

- [ ] If a test source set is generated, the IDE detects tests and the Gradle `test` task passes.
- [ ] Adding a new test class is recognized by both Gradle and the IDE test runner.
- [ ] Test Run Configuration is auto-detected for generated test files.

## 9. Artifacts and Packaging

- [ ] A build artifact (JAR) is produced when the application plugin is present.
- [ ] The produced JAR is runnable via `java -jar` if configured as a fat/shadow JAR.
- [ ] Project/module name, group, and artifact in the Gradle model reflect the wizard inputs.

## 10. Project Lifecycle

- [ ] Closing and reopening the project preserves all settings and the project compiles without re-sync issues.
- [ ] Invalidate Caches / Restart does not break the project.
- [ ] Importing the project from existing sources (re-import) works correctly.
- [ ] Updating the Gradle wrapper version does not break the build.

## 11. Cross-Platform

- [ ] Template works on Windows (paths, line endings, script execution).
- [ ] Template works on macOS.
- [ ] Template works on Linux.
- [ ] `gradlew` / `gradlew.bat` scripts are executable on their respective platforms.

## 12. Performance

- [ ] Project creation completes within a reasonable time (<30 seconds).
- [ ] Initial indexing completes without excessive CPU/memory usage.
- [ ] Gradle sync does not cause IDE freezes or UI hangs.

## 13. Compatibility

- [ ] Template works in the latest stable IntelliJ IDEA.
- [ ] Template works in IntelliJ IDEA EAP (if applicable).
- [ ] Template works with the latest stable Kotlin plugin version.
- [ ] No conflicts with other commonly installed plugins (e.g., Android, Spring).
