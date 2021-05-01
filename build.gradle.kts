import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logbackVersion = "1.2.3"
val ktorVersion = "1.5.4"
val bunqVersion = "1.14.18"
val jaxbVersion = "2.2.11"
val jacksonVersion = "2.12.3"
val commonsNetVersion = "3.8.0"
val hopliteVersion = "1.4.0"
val commonsCodecVersion = "1.15"

plugins {
    application
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = "dev.vjcbs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

application {
    mainClass.set("dev.vjcbs.bunq2ynab.MainKt")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.github.bunq:sdk_java:$bunqVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("commons-net:commons-net:$commonsNetVersion")
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    implementation("commons-codec:commons-codec:$commonsCodecVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
