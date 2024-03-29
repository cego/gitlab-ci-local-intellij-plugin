package dk.cego.gitlab_ci_local_plugin

import com.intellij.execution.*
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

class GclToolWindow(private var project: Project) {
    private var tree: JTree? = null
    private var refreshButton: JButton? = null
    private var panel: JPanel? = null
    private var checkBox: JCheckBox? = null

    init {
        this.tree?.model = DefaultTreeModel(DefaultMutableTreeNode(this.project.name))
        refreshButton!!.icon = AllIcons.Actions.Refresh
        refreshButton!!.text = "Refresh"
        refreshButton!!.addActionListener { refresh() }
        refresh()
    }

    private fun refresh() {
        val task: Task.Backgroundable = object : Task.Backgroundable(project, "Fetching Gitlab-CI jobs...", true) {
            override fun run(indicator: ProgressIndicator) {
                refreshButton?.text = "..."
                var errorMessage = "";
                var output: ProcessOutput? = null;
                try {
                    output = gclList()
                } catch (e: ExecutionException) {
                    errorMessage = e.message!!
                }

                if (output != null) {
                    errorMessage = output.stderr.ifEmpty { output.stdout }
                }
                if (output == null || output.exitCode != 0) {
                    // show IDE balloon
                    ApplicationManager.getApplication().invokeLater {
                        JBPopupFactory.getInstance()
                            .createHtmlTextBalloonBuilder(
                                "<html>Error:<br>${errorMessage}</html>",
                                null,
                                Color.BLACK,
                                Color.RED,
                                null
                            )
                            .createBalloon()
                            .show(
                                JBPopupFactory.getInstance()
                                    .guessBestPopupLocation(refreshButton!!),
                                Balloon.Position.atRight
                            )
                    }
                    refreshButton?.text = "Refresh"
                    return
                }

                // parse output
                val gclJobs = GclJobFactory().parse(output.stdout)

                // update UI
                ApplicationManager.getApplication().invokeLater {
                    showGclJobs(gclJobs)
                }
                refreshButton?.text = "Refresh"
            }
        }
        ProgressManager.getInstance().run(task)
    }

    private fun gclList(): ProcessOutput {
        val runConfiguration =
            GeneralCommandLine(WslUtils.rewriteToWslExec(project.basePath!!, listOf("gitlab-ci-local", "--list-json")))
                .withRedirectErrorStream(true)
        runConfiguration.workDirectory = File(project.basePath!!)
        return ExecUtil.execAndGetOutput(runConfiguration)
    }

    fun showGclJobs(jobs: List<GclJob>) {
        // group jobs by stage
        val stages = ArrayList<GclStage>()
        for (job in jobs) {
            var stage = stages.find { it.stage == job.stage }
            if (stage == null) {
                stage = GclStage(job.stage, ArrayList())
                stages.add(stage)
            }
            // add to list
            stage.jobs.add(job)
        }

        // create tree
        val root = DefaultMutableTreeNode(project.name)

        for (stage in stages) {
            val stageNode = DefaultMutableTreeNode(stage.stage)
            for (job in stage.jobs) {
                stageNode.add(DefaultMutableTreeNode(job.name))
            }
            root.add(stageNode)
        }

        val renderer = DefaultTreeCellRenderer()
        renderer.leafIcon = AllIcons.RunConfigurations.TestState.Run
        renderer.openIcon = AllIcons.Nodes.Folder
        renderer.closedIcon = AllIcons.Nodes.Folder
        // on
        tree?.cellRenderer = renderer
        tree?.model = DefaultTreeModel(root)

        for (i in 0 until tree!!.visibleRowCount) {
            tree!!.expandPath(tree!!.getPathForRow(i))
        }

        // add mouse listener
        addMouseListener()
        tree?.fireTreeExpanded(TreePath(root))
    }


    private fun addMouseListener() {
        val ml: MouseListener = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val selRow = tree!!.getRowForLocation(e.x, e.y)
                val selPath: TreePath? = tree!!.getPathForLocation(e.x, e.y)
                if (selPath != null && selPath.pathCount > 2 && selRow != -1 && e.clickCount == 2) {
                    val script =
                        selPath.path[2].toString() + " " + (if (checkBox!!.isSelected) "--needs" else "--no-needs")
                    val runner: RunnerAndConfigurationSettings = RunManager.getInstance(project)
                        .createConfiguration(script, GclRunConfigurationType::class.java)
                    RunManager.getInstance(project).addConfiguration(runner)
                    // set the configuration as the selected configuration
                    RunManager.getInstance(project).selectedConfiguration = runner
                    val executor: Executor = DefaultRunExecutor.getRunExecutorInstance()
                    ProgramRunnerUtil.executeConfiguration(runner, executor)
                }
            }
        }
        tree!!.addMouseListener(ml)
    }


    val content: JComponent?
        get() = panel
}