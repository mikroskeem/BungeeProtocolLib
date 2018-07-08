import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.2.51"
    id("net.minecrell.licenser") version "0.3"
    id("net.minecrell.plugin-yml.bungee") version "0.2.1"
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

group = "eu.mikroskeem"
version = "0.0.1-SNAPSHOT"

val waterfallVersion = "1.12-SNAPSHOT"
val asmVersion = "6.2"
val mcProtocolLibVersion = "master-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://destroystokyo.com/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("com.github.Steveice10:MCProtocolLib:$mcProtocolLibVersion") {
        exclude(group = "io.netty")
        isTransitive = true
    }

    compileOnly("io.github.waterfallmc:waterfall-api:$waterfallVersion")
}

val shadowJar by tasks.getting(ShadowJar::class) {
    val target = "eu.mikroskeem.bungeeprotocollib.lib"
    val relocs = listOf(
            "kotlin",
            "org.objectweb.asm",
            "com.github.steveice10"
    )
    relocs.forEach {
        relocate(it, "$target.$it")
    }

    exclude("META-INF/maven/**")
    exclude("yggdrasil_session_pubkey.der")
    exclude("com/google/gson/**")
}

license {
    header = rootProject.file("etc/HEADER")
    filter.include("**/*.java")
    filter.include("**/*.kt")
}

bungee {
    name = "BungeeProtocolLib"
    main = "eu.mikroskeem.bungeeprotocollib.BungeeProtocolLib"
    author = "${listOf("mikroskeem")}"
}

tasks["build"].dependsOn(shadowJar)