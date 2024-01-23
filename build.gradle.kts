plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("io.github.wldt:wldt-core:0.2.1")
}

application {
    mainClass.set("wldt.custom.demo.App")
}
