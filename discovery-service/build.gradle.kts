plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
    id("org.springframework.boot") version "3.5.16"
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
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.3"))
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
