plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

val mockitoVersion = "5.5.0"
val assertjVersion = "3.27.7"
val junitVersion = "4.13.2"

group = "com.zeitbuilder"
val pluginVersion = System.getenv("RELEASE_VERSION") ?: "1.0.1-SNAPSHOT"
version = pluginVersion

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

        val rawChangelog = System.getenv("RELEASE_CHANGELOG") ?: "<ul><li>Local build / Snapshot</li></ul>"
        changeNotes = "<![CDATA[\n$rawChangelog\n]]>"
        
        version = pluginVersion
    }

}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    test {
        useJUnit()
    }

    signPlugin {
        // Configuration for signing the plugin
    }

    publishPlugin {
        token.set(System.getenv("JETBRAINS_PUBLISH_TOKEN"))

        val channel = System.getenv("CHANNEL") ?: "alpha"

        channels.set(listOf(channel))
    }

}