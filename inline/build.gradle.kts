plugins {
    kotlin("jvm")
}

val myJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map {
        zipTree(it.absolutePath)
        })
    manifest {
        attributes(mapOf("Main-Class" to "TestKt"))
        exclude("META-INF/**/")
    }
}