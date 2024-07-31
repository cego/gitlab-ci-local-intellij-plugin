// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dk.cego.gitlabcilocal.plugin

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.icons.AllIcons
import javax.swing.Icon

class GclRunConfigurationType : ConfigurationType {
    override fun getDisplayName(): String {
        return "GitlabCiLocal"
    }

    override fun getConfigurationTypeDescription(): String {
        return "GitlabCiLocal runner"
    }

    override fun getIcon(): Icon {
        return AllIcons.General.Information
    }

    override fun getId(): String {
        return ID
    }

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(GclRunConfigurationFactory(this))
    }

    companion object {
        const val ID = "GitlabCiLocal"
    }
}