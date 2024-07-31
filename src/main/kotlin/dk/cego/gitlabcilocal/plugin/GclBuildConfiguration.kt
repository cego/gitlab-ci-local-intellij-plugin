package dk.cego.gitlabcilocal.plugin

import com.intellij.openapi.roots.ProjectModelBuildableElement
import com.intellij.openapi.roots.ProjectModelExternalSource

@Suppress("UnstableApiUsage")
open class GclBuildConfiguration(
) : ProjectModelBuildableElement {
    open val enabled: Boolean get() = true

    override fun getExternalSource(): ProjectModelExternalSource? = null
}