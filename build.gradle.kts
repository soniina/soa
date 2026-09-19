plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
    kotlin("plugin.jpa") version "2.2.10"
    id("org.springframework.boot") version "3.5.16"
    war
}

group = "soa"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.16"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat:3.5.16")
}

tasks.bootWar {
    archiveFileName.set("human-being-service.war")
}

tasks.war {
    enabled = false
}
