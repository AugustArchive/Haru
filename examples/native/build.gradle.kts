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

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
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

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":cron-scheduler"))
            }
        }

        val nativeMain by getting {
            dependencies {
                api(project(":cron-scheduler"))
            }
        }
    }
}
