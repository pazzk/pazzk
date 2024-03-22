import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.dokka") version "1.9.20"
    id("eu.kakde.gradle.sonatype-maven-central-publisher") version "1.0.5"
}

group="io.pazzk"
version="0.1.0"
archivesName = "pazzk"

repositories {
    mavenCentral()
}

val ktorVersion: String by project

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("io.ktor:ktor-client-okhttp:2.3.9")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}
tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}
tasks.dokkaHtml {
    moduleName.set(project.name)
    moduleVersion.set(project.version.toString())
    failOnWarning.set(false)
    suppressObviousFunctions.set(false)
    suppressInheritedMembers.set(false)
    offlineMode.set(false)
    outputDirectory.set(layout.buildDirectory.dir("documentation/html"))
}
tasks.dokkaGfm {
    outputDirectory.set(layout.buildDirectory.dir("documentation/markdown"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

// ------------------------------------
// PUBLISHING TO SONATYPE CONFIGURATION
// ------------------------------------
object Meta {
    const val COMPONENT_TYPE = "java"
    const val GROUP = "io.pazzk"
    const val ARTIFACT_ID = "pazzk"
    const val VERSION = "0.1.0"
    const val PUBLISHING_TYPE = "AUTOMATIC" // USER_MANAGED or AUTOMATIC
    val SHA_ALGORITHMS =
        listOf(
            "SHA-512",
        ) // sha256 and sha512 are supported but not mandatory. Only sha1 is mandatory but it is supported by default in the plugin.
    const val DESC = "Kotlin Coroutines For Chzzk Donation"
    const val LICENSE = "GNU General Public License version 3"
    const val LICENSE_URL = "https://opensource.org/licenses/GPL-3.0"
    const val GITHUB_REPO = "pazzk/pazzk.git"
    const val DEVELOPER_ID = "iqpizza6349"
    const val DEVELOPER_NAME = "iqpizza6349"
    const val DEVELOPER_EMAIL = "iqpizza6349@pazzk.io"
    const val DEVELOPER_ORGANIZATION = "pazzk.io"
}

val sonatypeUsername: String? by project // this is defined in ~/.gradle/gradle.properties
val sonatypePassword: String? by project // this is defined in ~/.gradle/gradle.properties

sonatypeCentralPublishExtension {
    groupId.set(Meta.GROUP)
    artifactId.set(Meta.ARTIFACT_ID)
    version.set(Meta.VERSION)
    componentType.set(Meta.COMPONENT_TYPE)
    publishingType.set(Meta.PUBLISHING_TYPE)
    shaAlgorithms.set(Meta.SHA_ALGORITHMS)
    username.set(System.getenv("SONATYPE_USERNAME") ?: sonatypeUsername)
    password.set(System.getenv("SONATYPE_PASSWORD") ?: sonatypePassword)

    pom {
        name.set(Meta.ARTIFACT_ID)
        description.set(Meta.DESC)
        url.set("https://github.com/${Meta.GITHUB_REPO}")
        licenses {
            license {
                name.set(Meta.LICENSE)
                url.set(Meta.LICENSE_URL)
            }
        }
        developers {
            developer {
                id.set(Meta.DEVELOPER_ID)
                name.set(Meta.DEVELOPER_NAME)
                email.set(Meta.DEVELOPER_EMAIL)
                organization.set(Meta.DEVELOPER_ORGANIZATION)
            }
        }
        scm {
            url.set("https://github.com/${Meta.GITHUB_REPO}")
            connection.set("scm:git:https://github.com/${Meta.GITHUB_REPO}")
            developerConnection.set("scm:git:https://github.com/${Meta.GITHUB_REPO}")
        }
        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/${Meta.GITHUB_REPO}/issues")
        }
    }
}
