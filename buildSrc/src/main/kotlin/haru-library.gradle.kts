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

plugins {
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
    kotlin("multiplatform")
}

group = "dev.floofy.haru"
version = "$VERSION"

repositories {
    mavenCentral()
    mavenLocal()
    noel()
}

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions.freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
            kotlinOptions.javaParameters = true
            kotlinOptions.jvmTarget = JAVA_VERSION.toString()
        }
    }

    js(BOTH) {
        nodejs()
    }

    val os = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")

    logger.lifecycle("[${project.name}] Running on $os ($arch)")
    val nativeTarget = when {
        os.startsWith("Windows") -> {
            logger.lifecycle("[${project.name}] Disabled targets: [linuxX64, linuxArm64, macosX64]")
            mingwX64("native")
        }

        os == "Linux" -> {
            when (arch) {
                "amd64" -> {
                    logger.lifecycle("[${project.name}] Disabled targets: [linuxArm64, mingwX64, macosX64]")
                    linuxX64("native")
                }

                "arm64" -> {
                    logger.lifecycle("[${project.name}] Disabled targets: [linuxX64, mingwX64, macosX64]")
                    linuxArm64("native")
                }

                else -> throw GradleException("Linux with architecture [$arch] is not supported.")
            }
        }

        os == "Mac OS X" -> {
            when (arch) {
                "x86_64" -> macosX64("native")
                "arm64" -> macosArm64("native")
                else -> error("macOS with architecture $arch is not supported.")
            }
        }

        else -> error("Operating system [$os ($arch)] is not supported.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
                api(kotlin("stdlib-common", "1.7.0"))
            }
        }

        val jvmMain by getting {
            dependencies {
                if (project.name == "cron-scheduler") {
                    api("com.cronutils:cron-utils:9.1.6")
                }

                api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.3")
                api("dev.floofy.commons:slf4j:2.1.1")
            }
        }

        val jsMain by getting {
            dependencies {
                npm("cron-parser", "4.5.0")
            }
        }

        val nativeMain by getting
    }

    if (project.name == "cron-scheduler") {
        nativeTarget.apply {
            binaries.sharedLib()
        }
    }
}

tasks {
    dokkaHtml {
        dokkaSourceSets.configureEach {
            sourceLink {
                localDirectory.set(projectDir.resolve("src/$name/kotlin"))
                remoteUrl.set(uri("https://github.com/auguwu/Haru/blob/master/src/$name/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }

            val map = asMap

            if (map.containsKey("jsMain")) {
                named("jsMain") {
                    displayName.set("JS")
                }
            }

            if (map.containsKey("jvmMain")) {
                named("jvmMain") {
                    jdkVersion.set(8)
                    displayName.set("JVM")
                }
            }

            if (map.containsKey("nativeMain")) {
                named("nativeMain") {
                    displayName.set("Native")
                }
            }
        }
    }
}
