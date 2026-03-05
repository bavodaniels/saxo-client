plugins {
    java
    `java-library`
    `maven-publish`
    checkstyle
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.25.0"
    id("org.owasp.dependencycheck") version "9.0.7"
}

group = "com.saxolab"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat("1.35.0")
    }
}

checkstyle {
    toolVersion = "10.12.7"
    configFile = file("config/checkstyle/checkstyle.xml")
}

dependencyCheck {
    format = "ALL"
    suppressionFile = "config/dependencycheck-suppressions.xml"
}


repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionOf("runtimeClasspath")
                }
            }
        }
    }
}
