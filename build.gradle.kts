import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("io.freefair.lombok") version "8.6"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
		sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

/* Spring */
dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
}

/* Spring Security */
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	testImplementation("org.springframework.security:spring-security-test")
}


/* Spring test */
dependencies {
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

/* Instancio */
dependencies {
	implementation("org.instancio:instancio-junit:3.6.0")
}

/* DataFaker */
dependencies {
	implementation("net.datafaker:datafaker:2.0.2")
}

/* Json Assertj */
dependencies {
	implementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
}

/* Spring MapStruct */
dependencies {
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

/* Jackson */
dependencies {
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

}

/* DB */
dependencies {
	runtimeOnly("com.h2database:h2:2.2.224")
	implementation("org.postgresql:postgresql:42.7.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	useJUnitPlatform()
	// https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		// showStackTraces = true
		// showCauses = true
		showStandardStreams = true
	}
}
/* Jococo */
tasks.jacocoTestReport { reports { xml.required.set(true) } }