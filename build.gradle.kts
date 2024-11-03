import io.papermc.hangarpublishplugin.model.Platforms
import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

group = "dev.spys"
version = "1.0.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.aikar.co/content/groups/aikar/") {
        name = "akair-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    paperweight.paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib"))
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(17)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    relocate("co.aikar.commands", "dev.spys.slashspawn.acf")
    relocate("co.aikar.locales", "dev.spys.slashspawn.locales")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

fun executeGitCommand(vararg command: String): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", *command)
        standardOutput = byteOut
    }
    return byteOut.toString(Charsets.UTF_8.name()).trim()
}

fun latestCommitMessage(): String {
    return executeGitCommand("log", "-1", "--pretty=%B")
}

val versionString: String = version as String
val isRelease: Boolean = !versionString.contains('-')

val suffixedVersion: String = if (isRelease) {
    versionString
} else {
    // Give the version a unique name by using the GitHub Actions run number
    versionString + "+" + System.getenv("GITHUB_RUN_NUMBER")
}

// Use the commit description for the changelog
val changelogContent: String = latestCommitMessage()

// If you would like to publish releases with their proper changelogs manually, simply add an if statement with the `isRelease` variable here.
hangarPublish {
    publications.register("plugin") {
        version.set(suffixedVersion)
        channel.set(if (isRelease) "Release" else "Snapshot")
        changelog.set(changelogContent)
        id.set("slashspawn")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                // Set the JAR file to upload
                jar.set(tasks.reobfJar.flatMap { it.outputJar })

                // Set platform versions from gradle.properties file
                val versions: List<String> = (property("paperVersion") as String)
                    .split(",")
                    .map { it.trim() }
                platformVersions.set(versions)
            }
        }
    }
}

