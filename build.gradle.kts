import java.text.SimpleDateFormat
import java.util.*

plugins {
    eclipse
    idea
    alias(libs.plugins.forgeGradle)
    alias(libs.plugins.parchmentGradle)
    alias(libs.plugins.mixinGradle)
}

val modId: String = providers.gradleProperty("mod_id").get()
val modVersion: String = providers.gradleProperty("mod_version").get()
val modGroupId: String = providers.gradleProperty("mod_group_id").get()
val modName: String = providers.gradleProperty("mod_name").get()
val modLicense: String = providers.gradleProperty("mod_license").get()
val modAuthors: String = providers.gradleProperty("mod_authors").get()
val modDescription: String = providers.gradleProperty("mod_description").get()

val minecraftVersion: String = libs.versions.minecraft.get()
val minecraftVersionRange: String = libs.versions.minecraftRange.get()
val forgeVersion: String = libs.versions.forge.get()
val forgeVersionRange: String = libs.versions.forgeRange.get()
val parchmentVersion: String = libs.versions.parchment.get()

val jadeVersionRange: String = libs.versions.jadeRange.get()

group = modGroupId
version = modVersion

base {
    archivesName.set(modId)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

minecraft {
    mappings("parchment", "$parchmentVersion-$minecraftVersion")
    copyIdeResources = true

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)
            mods {
                register(modId) {
                    source(sourceSets.main.get())
                }
            }
        }
        create("client")
    }
}

mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")
    config("${modId}.mixins.json")
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

repositories {
    exclusiveContent {
        forRepository {
            maven("https://cursemaven.com") {
                name = "CurseMaven"
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    minecraft(libs.forge) {
        version {
            require("$minecraftVersion-$forgeVersion")
        }
    }
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })
    implementation(libs.jade)
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "forge_version" to forgeVersion,
        "forge_version_range" to forgeVersionRange,
        "loader_version_range" to forgeVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to modVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "jade_version_range" to jadeVersionRange
    )
    inputs.properties(replaceProperties)
    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modId,
                "Specification-Vendor" to modAuthors,
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion.get(),
                "Implementation-Vendor" to modAuthors,
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
            )
        )
    }
    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}