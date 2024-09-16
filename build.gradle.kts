plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22" // Плагин для сериализации
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Тестирование
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Ktor Client для HTTP-запросов
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0") // CIO - одна из реализаций клиента

    // kotlinx.serialization для работы с JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // SLF4J для логирования (если планируешь использовать)
    implementation("org.slf4j:slf4j-simple:1.7.36")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
