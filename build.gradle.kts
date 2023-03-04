import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `kotlin-dsl`
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.0-SNAPSHOT" apply false
    kotlin("jvm") version "1.8.0" apply false
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")
        // The following line declares the mojmap mappings, you may use other mappings as well
        "mappings"(loom.officialMojangMappings())
        // The following line declares the yarn mappings you may select this one as well.
        // "mappings"("net.fabricmc:yarn:1.19.2+build.3:v2")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    base.archivesName.set(rootProject.property("archives_base_name").toString())
    //base.archivesBaseName = rootProject.property("archives_base_name").toString()
    version = rootProject.property("mod_version").toString()
    group = rootProject.property("maven_group").toString()

    repositories {
        // Add repositories to retrieve artifacts from in here.
        // You should only use this when depending on other mods because
        // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
        // See https://docs.gradle.org/current/userguide/declaring_repositories.html
        // for more information about repositories.
    }

    dependencies {
        "compileClasspath"("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin_version")}")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }

    val compileKotlin: KotlinCompile by tasks
    compileKotlin.kotlinOptions {
        jvmTarget = "17"
    }
    val compileTestKotlin: KotlinCompile by tasks
    compileTestKotlin.kotlinOptions {
        jvmTarget = "17"
    }
}
