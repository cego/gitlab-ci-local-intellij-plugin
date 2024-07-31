package dk.cego.gitlabcilocal.plugin

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class GclRunConfigurationEditor : SettingsEditor<GclRunConfiguration>() {
  private lateinit var settingsPanel: JPanel
  private lateinit var scriptNameLabel: LabeledComponent<TextFieldWithBrowseButton>

  override fun resetEditorFrom(gclRunConfiguration: GclRunConfiguration) {
    scriptNameLabel.component.text = gclRunConfiguration.scriptName
  }

  override fun applyEditorTo(runConfiguration: GclRunConfiguration) {
    runConfiguration.scriptName = scriptNameLabel.component.text
  }

  override fun createEditor(): JComponent {
    createUIComponents()
    settingsPanel = JPanel(BorderLayout())
    settingsPanel.add(scriptNameLabel, BorderLayout.NORTH)

    return settingsPanel
  }

  private fun createUIComponents() {
    scriptNameLabel = LabeledComponent()
    scriptNameLabel.component = TextFieldWithBrowseButton()
  }
}