package dk.cego.gitlabcilocal.plugin

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent


class GclStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val projectName = project.name
        println("GitlabCILocalPlugin: Register for file system changes in project $projectName")
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    if (event.path.contains(".gitlab-ci-local")) {
                        val path = event.path.substring(0, event.path.indexOf(".gitlab-ci-local")) + ".gitlab-ci-local"
                        val file = VirtualFileManager.getInstance().findFileByUrl("file://${path}")
                        if (file != null) {
                            updateIntelliJProjectModel(project, file)
                        }
                    }
                }
            }
        })
    }

    private fun updateIntelliJProjectModel(project: Project, file: VirtualFile) {
        val modules = ModuleManager.getInstance(project).modules
        val model = ModuleRootManager.getInstance(modules.first()).modifiableModel
        model.addContentEntry(file).addExcludeFolder(file)
        model.commit()
    }
}