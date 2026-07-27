pluginManagement {
    repositories {
        maven("https://maven.minecraftforge.net/") {
            name = "MinecraftForge"
        }
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "SpongepoweredMixin"
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}