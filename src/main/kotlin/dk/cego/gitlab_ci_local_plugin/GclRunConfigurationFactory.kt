// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.sdk.runConfiguration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import dk.cego.gitlab_ci_local_plugin.GclRunConfiguration
import dk.cego.gitlab_ci_local_plugin.GclRunConfigurationOptions
import dk.cego.gitlab_ci_local_plugin.GclRunConfigurationType

class GclRunConfigurationFactory(type: ConfigurationType?) : ConfigurationFactory(type!!) {
    override fun getId(): String {
        return GclRunConfigurationType.ID
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return GclRunConfiguration(project, this, "GitlabCiLocal")
    }

    override fun getOptionsClass(): Class<out BaseState?> {
        return GclRunConfigurationOptions::class.java
    }
}