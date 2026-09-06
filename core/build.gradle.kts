plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.test {
    if (!file("C:/Users/ritik/Downloads/pp.ritik.jpg").exists()) {
        exclude("**/RealImageCompressionTest*")
    }
}
