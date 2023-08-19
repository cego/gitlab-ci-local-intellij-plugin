package dk.cego.gitlab_ci_local_plugin

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.ExperimentalPathApi


@OptIn(ExperimentalPathApi::class)
class GclStartup : StartupActivity {
    override fun runActivity(project: Project) {
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    if (event.path.contains(".gitlab-ci-local")) {
                        val path = event.path.substring(0, event.path.indexOf(".gitlab-ci-local")) + ".gitlab-ci-local"
                        val file = VirtualFileManager.getInstance().findFileByUrl("file://${path}")
                        if (file != null) {
                            val modules = ModuleManager.getInstance(project).modules
                            val model = ModuleRootManager.getInstance(modules.first()).modifiableModel
                            model.addContentEntry(file).addExcludeFolder(file)
                            model.commit()
                        }
                    }
                }
            }
        })

        // Create .gitlab-ci-local folder in project.basePath, it should automatically be excluded by above event.
        val folderPath = Path(project.basePath!!).resolve(".gitlab-ci-local")
        if (!folderPath.exists()) {
            folderPath.createDirectory()
        }
    }

}