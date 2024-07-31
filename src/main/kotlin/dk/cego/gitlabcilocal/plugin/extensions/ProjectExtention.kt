package dk.cego.gitlabcilocal.plugin.extensions

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir


fun Project.obtainRootDirPath(): String {

    println("Project basePath: $basePath")
    println("Project projectFilePath: $projectFilePath")
    println("Project guessProjectDir: ${guessProjectDir()}")
    println("Project guessProjectDir Path: ${guessProjectDir()?.path}")
    println("Project guessProjectDir Name: ${guessProjectDir()?.name}")

    return guessProjectDir()?.path ?: throw RuntimeException("Cannot find project's base path")
}