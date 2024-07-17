plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("maven-publish")
    id("signing")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
}

group = "io.github.createduser"
version = "0.9.1.1"
description = "A text localization library built with Kotlin. 用Kotlin构建的文字本地化类库。"

centralPortal {
    username = System.getenv("CENTRAL_USERNAME")
    password = System.getenv("CENTRAL_PASSWORD")

    pom {
        url = "https://github.com/createduser/LanguageString"
        licenses {
            license {
                name = "The MIT License"
                url = "https://opensource.org/license/MIT"
            }
        }
        developers {
            developer {
                id = "userrrrr"
                name = "Created User"
                email = "a2091644320@163.com"
            }
        }
        scm {
            connection = "scm:git:git://github.com/createduser/LanguageString.git"
            developerConnection = "scm:git:ssh://github.com/createduser/LanguageString.git"
            url = "https://github.com/createduser/LanguageString"
        }
        scm {
            connection = "scm:git:git://gitee.com/userrrrr/LanguageString.git"
            developerConnection = "scm:git:ssh://gitee.com/userrrrr/LanguageString.git"
            url = "https://gitee.com/userrrrr/LanguageString"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
}

kotlin {
    jvmToolchain(8)
}

signing {
    useGpgCmd()
}