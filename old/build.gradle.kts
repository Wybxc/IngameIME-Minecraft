import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "1.8.0"
    id("architectury-plugin") version "+"
    id("dev.architectury.loom") version "+" apply false
    id("com.github.johnrengelman.shadow") version "+" apply false
}

version = "${project.mod_version}"
group = "${project.maven_group}"

architectury {
    minecraft = "${project.minecraft_version}"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "com.github.johnrengelman.shadow")

    val shadowC by configurations.creating

    version = "${project.mod_version}"
    group = "${project.maven_group}"
    base {
        archivesBaseName = "$project.archives_base_name-$name-$project.minecraft_version"
    }

    repositories {
        mavenCentral()
    }

    tasks {
        withType(ShadowJar::class) { this.configurations = listOf(shadowC) }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
        }

        withType<JavaCompile> {
            sourceCompatibility = "17"
            targetCompatibility = "17"
        }
    }

    lateinit var mappingsDep: Dependency
    extensions.configure<net.fabricmc.loom.LoomGradleExtension>("loom") {
        mappingsDep = officialMojangMappings()
        silentMojangMappingsLicense()
    }
    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.minecraft_version}")
        "mappings"(mappingsDep)
    }
    class Version(version: String) : Comparable<Version> {
        @Suppress("PropertyName")
        val VERSION_REGEX: Regex = Regex("""(\d+)(?:.(\d+)(?:.(\d+))?)?""")
        var major: Int = 0
        var minor: Int = 0
        var revision: Int = 0

        init {
            VERSION_REGEX.matchEntire(version)?.apply {
                major = this.groupValues[1].toInt()
                minor = this.groupValues[2].toIntOrNull() ?: 0
                revision = this.groupValues[3].toIntOrNull() ?: 0
            } ?: throw IllegalArgumentException("Invalid version string:$version")
        }

        override fun compareTo(other: Version): Int {
            if (this == other) return 0
            if (this.major > other.major) return 1
            if (this.major == other.major) {
                if (this.minor > other.minor) return 1
                if (this.minor == other.minor && this.revision > other.revision) return 1
            }
            return -1
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Version

            if (major != other.major) return false
            if (minor != other.minor) return false
            if (revision != other.revision) return false

            return true
        }

        override fun hashCode(): Int {
            var result = major
            result = 31 * result + minor
            result = 31 * result + revision
            return result
        }

        override fun toString(): String {
            return "$major.$minor.$revision"
        }
    }

    afterEvaluate {
        tasks {
            withType(ShadowJar::class) {
                archiveClassifier.set("shadow-dev")
            }
            withType(net.fabricmc.loom.task.RemapJarTask::class) {
                onlyIf {
                    return@onlyIf this@subprojects.name != "common"
                }
                val shadowTask = getByName("shadowJar") as ShadowJar
                dependsOn(shadowTask)
                input.set(shadowTask.archiveFile)
                archiveAppendix.set("")
            }
        }
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
}
