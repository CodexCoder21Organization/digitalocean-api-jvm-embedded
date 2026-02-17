@KotlinBuildScript("https://tools.kotlin.build/")
@file:WithArtifact("kompile:build-kotlin-jvm:0.0.1")
package community.kotlin.contrib.digitalocean.embedded
import build.kotlin.withartifact.WithArtifact

import java.io.File
import build.kotlin.jvm.*
import build.kotlin.annotations.MavenArtifactCoordinates

val dependencies = resolveDependencies(
    MavenPrebuilt("org.jetbrains.kotlin:kotlin-stdlib:1.8.22"),
    MavenPrebuilt("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.22"),
    MavenPrebuilt("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22"),
    MavenPrebuilt("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22"),
    MavenPrebuilt("community.kotlin.contrib.digitalocean:api:0.0.2"),
    MavenPrebuilt("com.google.code.gson:gson:2.8.6"),
    MavenPrebuilt("org.apache.commons:commons-lang3:3.10"),
    MavenPrebuilt("org.apache.httpcomponents:httpclient:4.5.12"),
    MavenPrebuilt("org.apache.httpcomponents:httpcore:4.4.13"),
    MavenPrebuilt("commons-logging:commons-logging:1.2"),
    MavenPrebuilt("commons-codec:commons-codec:1.11"),
    MavenPrebuilt("org.slf4j:slf4j-api:1.7.30"),
)

@MavenArtifactCoordinates("community.kotlin.contrib.digitalocean:embedded:")
fun buildMaven(): File {
    return buildSimpleKotlinMavenArtifact(
        coordinates="community.kotlin.contrib.digitalocean:embedded:0.0.1",
        src=File("src"),
        compileDependencies=dependencies
    )
}

fun buildSkinnyJar(): File {
    return buildMaven().jar
}

fun buildFatJar(): File {
    val manifest = Manifest("community.kotlin.contrib.digitalocean.embedded.DigitalOceanClient")
    return BuildJar(manifest, dependencies.map { it.jar } + buildSkinnyJar())
}
