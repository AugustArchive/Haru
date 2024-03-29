# WARNING
Haru will be merged into a new project soon.

# Haru
> 🚟 **Lightweight, and simple scheduling library made for Kotlin (JVM)**

## Why did you build this?
I built this library as a personal usage library to handling schedulers within the applications
I am creating, and it'll be easier if I put it in a library that I can reuse.

I currently use **Haru** with my private Discord bot, Noel.

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
    val scheduler = Scheduler()
    scheduler.schedule(MyJob(), start = true) // using classes to register
    
    // using DSL for building schedulers
    scheduler.schedule {
        name = "some job name"
        expression = "another expression to use"
        start = true
        
        executor = {
            println("I am working :D")
        }
    }
}
```

## Will this a multiplatform library?
I don't know, probably...

## How long did it take?
I'd say around ~3 hours :)

![proof](https://cute.floofy.dev/images/75255c28.png)

## Installation
> Documentation: https://haru.floofy.dev
>
> Version: 1.3.0

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
