/*
 * 🚟 Haru (春): Lightweight, and simple scheduling library made for Kotlin (JVM, JS, and Native)
 * Copyright (c) 2021-2022 Noel <cutie@floofy.dev>
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

import dev.floofy.utils.gradle.*
import dev.floofy.haru.gradle.*
import java.io.StringReader
import java.util.Properties

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    `java-library`
}

// Get the `publishing.properties` file from the `gradle/` directory
// in the root project.
val publishingPropsFile = file("${rootProject.projectDir}/gradle/publishing.properties")
val publishingProps = Properties()

// If the file exists, let's get the input stream
// and load it.
if (publishingPropsFile.exists()) {
    publishingProps.load(publishingPropsFile.inputStream())
} else {
    // Check if we do in environment variables
    val accessKey = System.getenv("NOEL_PUBLISHING_ACCESS_KEY") ?: ""
    val secretKey = System.getenv("NOEL_PUBLISHING_SECRET_KEY") ?: ""

    if (accessKey.isNotEmpty() && secretKey.isNotEmpty()) {
        val data = """
        |s3.accessKey=$accessKey
        |s3.secretKey=$secretKey
        """.trimMargin()

        publishingProps.load(StringReader(data))
    }
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
        filterIsInstance<MavenPublication>().forEach { publication ->
            publication.version = "$VERSION"
            publication.artifact(dokkaJar.get())
            publication.pom {
                description by "\uD83D\uDE9F Lightweight, and simple scheduling library made for Kotlin (JVM, JS, and Native)"
                name by project.name
                url by "https://haru.floofy.dev"

                licenses {
                    license {
                        name by "MIT License"
                        url by "https://github.com/auguwu/Haru/blob/master/LICENSE"
                    }
                }

                developers {
                    developer {
                        email by "cutie@floofy.dev"
                        name by "Noel"
                        url by "https://floofy.dev"
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/auguwu/Haru.git")
                    developerConnection.set("scm:git:https://github.com/auguwu/Haru.git")
                    url.set("https://github.com/auguwu/Haru")
                }
            }
        }
    }

    repositories {
        maven("s3://maven.floofy.dev/repo/releases") {
            credentials(AwsCredentials::class) {
                this.accessKey = publishingProps.getProperty("s3.accessKey") ?: ""
                this.secretKey = publishingProps.getProperty("s3.secretKey") ?: ""
            }
        }
    }
}
