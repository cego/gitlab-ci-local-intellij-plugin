package dk.cego.gitlabcilocal.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
class GitlabCiLocalToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val gclToolWindow = GclToolWindow(project)
        val contentFactory = ContentFactory.getInstance()
        val content: Content = contentFactory.createContent(gclToolWindow.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}