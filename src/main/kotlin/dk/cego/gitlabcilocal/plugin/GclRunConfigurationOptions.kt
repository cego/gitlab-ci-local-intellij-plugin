// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dk.cego.gitlabcilocal.plugin

import com.intellij.execution.configurations.RunConfigurationOptions

class GclRunConfigurationOptions : RunConfigurationOptions() {

    private val scriptNameProperty = string("gitlab-ci-local")
        .provideDelegate(this, "scriptName")

    var scriptName: String
        get() = scriptNameProperty.getValue(this).toString()
        set(scriptName) {
            scriptNameProperty.setValue(this, scriptName)
        }
}