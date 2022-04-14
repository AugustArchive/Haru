/**
 * Copyright (c) 2021 Noel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.text.SimpleDateFormat
import java.util.Properties
import java.util.Date

plugins {
    id("com.diffplug.spotless") version "6.4.2"
    id("org.jetbrains.dokka") version "1.6.20"
    kotlin("jvm") version "1.5.31"
    `maven-publish`
}

val current = "1.3.0"
group = "dev.floofy"
version = current

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Kotlin libraries
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.1")
    implementation(kotlin("stdlib", "1.5.31"))

    // Cron Support
    implementation("com.cronutils:cron-utils:9.1.6")

    // SLF4J Logger
    api("org.slf4j:slf4j-api:1.7.36")
}

tasks.register("generateMetadata") {
    val path = sourceSets["main"].resources.srcDirs.first()
    if (!file(path).exists()) path.mkdirs()

    val date = Date()
    val formatter = SimpleDateFormat("MMM dd, yyyy @ hh:mm:ss")

    file("$path/metadata.properties").writeText("""built.at = ${formatter.format(date)}
app.version = $version
""".trimIndent())
}

spotless {
    kotlin {
        trimTrailingWhitespace()
        licenseHeaderFile("${rootProject.projectDir}/assets/HEADER")
        endWithNewline()

        // We can't use the .editorconfig file, so we'll have to specify it here
        // issue: https://github.com/diffplug/spotless/issues/142
        ktlint()
            .userData(mapOf(
                "no-consecutive-blank-lines" to "true",
                "no-unit-return" to "true",
                "disabled_rules" to "no-wildcard-imports,colon-spacing",
                "indent_size" to "4"
            ))
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.javaParameters = true
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    dokkaHtml {
        outputDirectory.set(file("${rootProject.projectDir}/docs"))

        dokkaSourceSets {
            configureEach {
                platform.set(org.jetbrains.dokka.Platform.jvm)
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(uri("https://github.com/auguwu/Haru/tree/master/src/main/kotlin").toURL())
                    remoteLineSuffix.set("#L")
                }

                jdkVersion.set(11)
            }
        }
    }
}


val publishingProps = try {
    Properties().apply { load(file("${rootProject.projectDir}/gradle/publishing.properties").reader()) }
} catch(e: Exception) {
    Properties()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assemble Kotlin documentation with Dokka"

    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
    dependsOn(tasks.dokkaHtml)
}

publishing {
    publications {
        create<MavenPublication>("Haru") {
            from(components["kotlin"])
            groupId = "dev.floofy.haru"
            artifactId = "Haru"
            version = current

            artifact(sourcesJar.get())
            artifact(dokkaJar.get())

            pom {
                description.set("Lightweight, and simple scheduling library made for Kotlin (JVM)")
                name.set("Haru")
                url.set("https://haru.floofy.dev")

                organization {
                    name.set("Noel")
                    url.set("https://floofy.dev")
                }

                developers {
                    developer {
                        name.set("Noel")
                    }
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/auguwu/Haru/issues")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/auguwu/Haru.git")
                    developerConnection.set("scm:git:ssh://git@github.com:auguwu/Haru.git")
                    url.set("https://github.com/auguwu/Haru")
                }
            }
        }
    }

    repositories {
        maven(url = "s3://maven.floofy.dev/repo/releases") {
            credentials(AwsCredentials::class.java) {
                accessKey = publishingProps.getProperty("s3.accessKey") ?: ""
                secretKey = publishingProps.getProperty("s3.secretKey") ?: ""
            }
        }
    }
}
