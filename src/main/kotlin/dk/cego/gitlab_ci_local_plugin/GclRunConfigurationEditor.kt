// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dk.cego.gitlab_ci_local_plugin

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import javax.swing.JComponent
import javax.swing.JPanel

class GclRunConfigurationEditor : SettingsEditor<GclRunConfiguration>() {
  private val myPanel: JPanel? = null
  private var myScriptName: LabeledComponent<TextFieldWithBrowseButton>? = null
  override fun resetEditorFrom(gclRunConfiguration: GclRunConfiguration) {
    myScriptName!!.component.text = gclRunConfiguration.scriptName.toString()
  }

  override fun applyEditorTo(demoRunConfiguration: GclRunConfiguration) {
    demoRunConfiguration.scriptName = (myScriptName!!.component.text)
  }

  override fun createEditor(): JComponent {
    return myPanel!!
  }

  private fun createUIComponents() {
    myScriptName = LabeledComponent()
    myScriptName!!.component = TextFieldWithBrowseButton()
  }
}