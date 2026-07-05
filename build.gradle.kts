import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

    // Plugin signing. Only configured when the signing secrets are present,
    // so local builds and unsigned CI runs don't fail.
    val certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
    if (certificateChain.isPresent) {
        signing {
            this.certificateChain = certificateChain
            privateKey = providers.environmentVariable("PRIVATE_KEY")
            password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
        }
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // "default" = stable channel; "beta" = beta channel (custom repo URL).
        channels = providers.environmentVariable("CHANNEL")
            .map { listOf(it) }
            .orElse(listOf("default"))
    }

    // Runs the JetBrains IntelliJ Plugin Verifier against recommended IDEs
    // before publishing, catching API-compatibility problems early.
    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    test {
        useJUnit()
    }
}