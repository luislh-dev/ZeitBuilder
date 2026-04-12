plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

val mockitoVersion = "5.5.0"
val assertjVersion = "3.27.7"
val junitVersion = "4.13.2"

group = "com.zeitbuilder"
version = System.getenv("GITHUB_REF_NAME")?.removePrefix("v") ?: "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        val ideaVersion = System.getenv("IDEA_VERSION") ?: "2025.2"
        create("IC", ideaVersion)

        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Plugin.Java)
        bundledPlugin("com.intellij.java")
    }
    testImplementation("junit:junit:${junitVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "231"
        }

        changeNotes = """
            <ul>
                <li>feat: agregar soporte para generadores de builders extensibles</li>
                <li>fix: genera automáticamente constructor sin argumentos, excepto cuando existen propiedades final</li>
                <li>build: actualizar versión de AssertJ a 3.27.7</li>
                <li>refactor: migrar uso de constantes PsiType obsoletas</li>
                <li>feat: agregar soporte para generadores de builders de records</li>
            </ul>
        """.trimIndent()
    }

}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    test {
        useJUnit()
    }

    publishPlugin {
        token.set(System.getenv("JETBRAINS_PUBLISH_TOKEN"))

        val channel = System.getenv("CHANNEL") ?: "alpha"

        channels.set(listOf(channel))
    }

}