import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
}

group = "me.uconu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.neo4j:neo4j-ogm-core:3.2.26")
    implementation("com.graphql-java:graphql-java:17.3")
    implementation("com.graphql-java:graphql-java-extended-scalars:17.0")
    runtimeOnly("org.neo4j:neo4j-ogm-bolt-driver:3.2.26")

    implementation("com.jayway.jsonpath:json-path:2.6.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}