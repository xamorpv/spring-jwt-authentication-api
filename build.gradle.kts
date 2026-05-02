import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
	java
	id("pmd")
	id("checkstyle")
	id("jacoco")

	id("com.diffplug.spotless") version "8.4.0"

	id("de.aaschmid.cpd") version "3.5"
	id("net.ltgt.errorprone") version "5.1.0"
	id("org.openrewrite.rewrite") version "7.32.0"
	id("com.github.spotbugs") version "6.5.1"

	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.ls"
version = "0.0.1-SNAPSHOT"
description = "first jwt project"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

tasks.jar {
	enabled = false
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

	implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.8"))
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	implementation("org.bouncycastle:bcprov-jdk18on:1.78")

	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("com.tngtech.archunit:archunit-junit5:1.4.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")

	rewrite("org.openrewrite.recipe:rewrite-static-analysis:2.34.0")

	errorprone("com.google.errorprone:error_prone_core:2.41.0")
	errorprone("com.uber.nullaway:nullaway:0.13.3")
}

spotless {
	java {
		googleJavaFormat()
		trimTrailingWhitespace()
		removeUnusedImports()
		endWithNewline()
	}
}

checkstyle {
	toolVersion = "13.4.0"
	configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
	isIgnoreFailures = false
	maxWarnings = 0
}

pmd {
	toolVersion = "7.23.0"
	ruleSetFiles = files("${rootDir}/config/pmd/ruleset.xml")
	rulesMinimumPriority = 5
	isIgnoreFailures = false
	maxFailures = 0
}

cpd {
	language = "java"
	minimumTokenCount = 100
}

rewrite {
	configFile = file("config/rewrite/finalize-variables.yaml")
	activeRecipe("ru.ls.FinalizeVariables")
}

spotbugs {
	effort = com.github.spotbugs.snom.Effort.MAX
	reportLevel = com.github.spotbugs.snom.Confidence.LOW
	excludeFilter = file("${rootDir}/config/spotbugs/exclude.xml")
	ignoreFailures = false
	showProgress = true
}

tasks.cpdCheck {
	source = fileTree("src/main") {
		exclude("**/generated/**", "**/dto/**")
	}

	reports {
		text.required.set(true)
		xml.required.set(true)
	}
}

jacoco {
	toolVersion = "0.8.14"
}
tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

val jacocoExcludedPackages = setOf("dto", "config", "generated")

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree.matching {
			exclude(jacocoExcludedPackages.map { "**/${it}/**" })
		}
	)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			element = "CLASS"
			excludes = jacocoExcludedPackages.map { "**.${it}.*" }
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.80".toBigDecimal()
			}
			limit {
				counter = "BRANCH"
				value = "COVEREDRATIO"
				minimum = "0.70".toBigDecimal()
			}
		}
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.errorprone {
		disableAllChecks.set(true)

		check("NullAway", CheckSeverity.ERROR)

		option("NullAway:AnnotatedPackages", "ru.ls")

		option("NullAway:TreatGeneratedAsUnannotated", "true")

		option("NullAway:ExternalInitAnnotations", "jakarta.persistence.Entity")
	}
}

tasks.register("codeQualityCheck") {
	group = "verification"
	dependsOn(
		"spotlessCheck",
		"checkstyleMain",
		"checkstyleTest",
		"compileJava",
		"compileTestJava",
		"pmdMain",
		"pmdTest",
		"cpdCheck",
		"spotbugsMain",
		"spotbugsTest",
		"test"
	)
}

tasks.register("installGitHooks") {
	group = "help"
	doLast {
		val hookFile = file("${rootDir}/.git/hooks/pre-commit")
		val hookContent = """
            #!/bin/sh
STAGED_FILES=$(git diff --cached --name-only --diff-filter=ACMR)
./gradlew spotlessApply --quiet
for file in ${'$'}STAGED_FILES; do
    if [ -f "${'$'}file" ] && echo "${'$'}file" | grep -q '\.java$'; then
        git add "${'$'}file"
    fi
done
        """.trimIndent()
		hookFile.writeText(hookContent)
		hookFile.setExecutable(true)
	}
}

val generatePmdTestRuleset by tasks.registering {
	group = "pmd"
	description = "Generates ruleset-test.xml based on ruleset.xml with test-specific suppressions"

	val mainRuleset = file("${rootDir}/config/pmd/ruleset.xml")
	val testRuleset = file("${rootDir}/config/pmd/ruleset-test.xml")
	val suppressionsFile = file("${rootDir}/config/pmd/ruleset-test-suppressions.xml")

	inputs.file(mainRuleset)
	inputs.file(suppressionsFile)
	outputs.file(testRuleset)

	doLast {
		val mainContent = mainRuleset.readText()

		if (!suppressionsFile.exists()) {
			suppressionsFile.parentFile.mkdirs()
			suppressionsFile.writeText("""<?xml version="1.0"?>
<rule ref="category/java/design.xml/SignatureDeclareThrowsException">
    <properties>
        <property name="violationSuppressXPath" value=".[true()]" />
    </properties>
</rule>
<!-- Add more rule suppressions here -->
""")
			logger.lifecycle("Created example suppressions file at ${suppressionsFile}")
		}

		val suppressionBlock = suppressionsFile.readText()
		val updatedContent = mainContent.replace("</ruleset>", "\n$suppressionBlock\n</ruleset>")
		testRuleset.writeText(updatedContent)
	}
}

tasks.pmdTest {
	dependsOn(generatePmdTestRuleset)
	ruleSetFiles = files(generatePmdTestRuleset.get().outputs.files.singleFile)
}

tasks.pmdMain {
	ruleSetFiles = files("config/pmd/ruleset.xml")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
