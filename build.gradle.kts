plugins {
    id("org.jetbrains.intellij") version "1.17.4"
    kotlin("jvm") version "2.0.0"
}

repositories {
    mavenCentral()
}

group = "org.jetbrains"
version = "1.0-SNAPSHOT"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2024.1.4")
    plugins.set(listOf("org.jetbrains.kotlin"))
}

kotlin {
    jvmToolchain(17)
}

tasks {
    test {
        systemProperty("NO_FS_ROOTS_ACCESS_CHECK", "true")
    }

    patchPluginXml {
        version.set("${project.version}")
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        
            """.trimIndent())
    }
}