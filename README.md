# Haru
> 🚟 **Lightweight, and simple scheduling library made for Kotlin (JVM)**

## Why did you build this?
I built this library as a personal usage library to handling schedulers within the applications
I am creating, and it'll be easier if I put it in a library that I can reuse.

## Usage
```kotlin
class MyJob: AbstractJob(
    name = "job name",
    expression = "some expression to use"
) {
    override suspend fun execute() {
        println("working! :D")
    }
}

fun main(args: Array<String>) {
    val scheduler = createScheduler {}
    scheduler.schedule(MyJob()) // using classes to register
    
    // using DSL for building schedulers
    scheduler.schedule {
        name = "some job name"
        expression = "another expression to use"
        executor = {
            println("I am working :D")
        }
    }
}
```

## Will this a multiplatform library?
I don't know, probably...

## Installation
> Documentation: https://haru.floofy.dev
>
> Version: 1.0.0

## Gradle
### Kotlin DSL
```kotlin
repositories {
    maven {
        url = uri("https://maven.floofy.dev/repo/releases")
    }
}

dependencies {
    implementation("dev.floofy.haru:Haru:<VERSION>")
}
```

### Groovy DSL
```groovy
repositories {
    maven {
        url "https://maven.floofy.dev/repo/releases"
    }
}

dependencies {
    implementation "dev.floofy.haru:Haru:<VERSION>"
}
```

## Maven
```xml
<repositories>
    <repository>
        <id>noel-maven</id>
        <url>https://maven.floofy.dev/repo/releases</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>dev.floofy.haru</groupId>
        <artifactId>Haru</artifactId>
        <version>{{VERSION}}</version>
        <type>pom</type>
    </dependency>
</dependencies>
```

## License
**Haru** is released under the **MIT** License. :3
