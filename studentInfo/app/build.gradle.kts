plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // File Import Libraries
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("net.sf.ucanaccess:ucanaccess:5.0.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnit("4.13.2")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("org.example.App")
}