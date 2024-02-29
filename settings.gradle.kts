rootProject.name = "Candlestick"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		mavenLocal {
			content { includeGroup("io.data2viz.charts") }
		}
		maven(url = "https://maven.pkg.jetbrains.space/data2viz/p/maven/dev") {
			content { includeGroup("io.data2viz.charts") }
		}
    }
}

include(":composeApp")
