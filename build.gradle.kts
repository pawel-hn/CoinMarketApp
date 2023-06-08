// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext{
        kotlin_version = "1.8.20"
        hilt_version = '2.43.2'
        nav_version = "2.5.3"
    }


    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "com.google.dagger:hilt-android-gradle-plugin:2.43.2"

        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version"

        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}