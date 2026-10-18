plugins {
    java
    id("org.springframework.boot") version "2.3.12.RELEASE"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "soa"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR12")
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-zuul")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime")
}

tasks.withType<JavaCompile> {
    options.release.set(11)
    options.encoding = "UTF-8"
}

tasks.bootJar {
    archiveFileName.set("api-gateway.jar")
}
