import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("application")
  id("com.vanniktech.maven.publish")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

// Workaround for https://stackoverflow.com/questions/48988778
// /cannot-inline-bytecode-built-with-jvm-target-1-8-into-bytecode-that-is-being-bui
tasks.withType<KotlinJvmCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
  }
}

dependencies {
  api(projects.shark.sharkAndroid)

  implementation(libs.clikt)
  implementation(libs.neo4j)
  implementation(libs.jline)
  implementation(libs.kotlin.stdlib)
}

application {
  mainClass.set("shark.MainKt")
}

val generatedVersionDir = "${buildDir}/generated-version"

sourceSets {
  main {
    output.dir(generatedVersionDir, "builtBy" to "generateVersionProperties")
  }
}

tasks.register("generateVersionProperties") {
  doLast {
    val propertiesFile = file("$generatedVersionDir/version.properties")
    propertiesFile.parentFile.mkdirs()
    val properties = Properties()
    properties.setProperty("version_name", rootProject.property("VERSION_NAME").toString())
    propertiesFile.writer().use {
      properties.store(it, null)
    }
  }
}
tasks.named("processResources") {
  dependsOn("generateVersionProperties")
}
