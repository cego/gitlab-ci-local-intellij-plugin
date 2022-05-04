// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dk.cego.gitlab_ci_local_plugin

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

class GclRunConfigurationOptions : RunConfigurationOptions() {
    private val myScriptName: StoredProperty<String?> = string("gitlab-ci-local")
        .provideDelegate(this, "scriptName")

    var scriptName: String?
        get() = myScriptName.getValue(this)
        set(scriptName) {
            myScriptName.setValue(this, scriptName)
        }
}