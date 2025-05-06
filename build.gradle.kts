plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.spotless)
}
spotless {
    kotlin {
        target("**/*.kt", "**/*.kts")
        targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "buildSrc/**/*.kt")
        // version, editorConfigPath, editorConfigOverride and customRuleSets are all optional
        ktlint().setEditorConfigPath("$rootDir/.editorconfig") // sample unusual placement
    }
}
val installGitHook by tasks.registering(Copy::class) {
    from(file("${rootProject.rootDir}/.scripts/pre-commit"))
    into(file("${rootProject.rootDir}/.git/hooks"))
    fileMode = 0b111101101
}
project(":composeApp").afterEvaluate {
    tasks.named("preBuild").configure {
        dependsOn(":installGitHook")
    }
}
