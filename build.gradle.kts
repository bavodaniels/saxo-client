plugins {
    java
    `java-library`
    `maven-publish`
    checkstyle
    jacoco
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.25.0"
    id("org.owasp.dependencycheck") version "9.1.0"
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
    toolVersion = "10.21.0"
    configFile = file("config/checkstyle/checkstyle.xml")
}

dependencyCheck {
    format = "ALL"
    suppressionFile = "config/dependencycheck-suppressions.xml"
}

jacoco {
    toolVersion = "0.8.13"
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
    finalizedBy("jacocoTestReport")
}

tasks.test {
    jacoco {
        includes.addAll(listOf("com/saxolab/openapi/**"))
        excludes.addAll(listOf("**/model/**", "**/config/**"))
    }
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

// JaCoCo Test Report Configuration
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

// JaCoCo Coverage Verification Task
tasks.register("jacocoVerification") {
    dependsOn("jacocoTestReport")
    doLast {
        val jacocoReportFile = file("build/reports/jacoco/test/jacocoTestReport.xml")
        if (!jacocoReportFile.exists()) {
            throw GradleException("JaCoCo report not found at ${jacocoReportFile.absolutePath}")
        }

        val xmlContent = jacocoReportFile.readText()

        // Extract line coverage from JaCoCo XML counter elements
        // Format: <counter type="LINE" missed="X" covered="Y"/>
        val lineCounterRegex = """<counter type="LINE" missed="(\d+)" covered="(\d+)"/>""".toRegex()

        var totalMissed = 0
        var totalCovered = 0

        lineCounterRegex.findAll(xmlContent).forEach { match ->
            totalMissed += match.groupValues[1].toInt()
            totalCovered += match.groupValues[2].toInt()
        }

        val totalLines = totalMissed + totalCovered
        val lineCoverage = if (totalLines > 0) (totalCovered * 100.0) / totalLines else 100.0

        println("\n" + "=".repeat(70))
        println("JaCoCo Coverage Report")
        println("=".repeat(70))
        println("Line Coverage: ${String.format("%.2f", lineCoverage)}% ($totalCovered/$totalLines lines)")
        println("Required Coverage: 90.00%")
        println("=".repeat(70))

        if (lineCoverage < 90.0) {
            throw GradleException(
                "Code coverage ${String.format("%.2f", lineCoverage)}% is below the required 90%"
            )
        }
        println("✓ Coverage requirement met!\n")
    }
}

// Integrate coverage verification into build
tasks.named("check").configure {
    dependsOn("jacocoVerification")
}
