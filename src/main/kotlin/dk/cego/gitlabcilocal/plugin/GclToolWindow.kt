package dk.cego.gitlabcilocal.plugin

import com.intellij.execution.*
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import dk.cego.gitlabcilocal.plugin.extensions.obtainRootDirPath
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

    private lateinit var jobsTree: JTree
    private lateinit var refreshButton: JButton
    private lateinit var panel: JPanel
    private lateinit var checkBox: JCheckBox
    private lateinit var schemaValidationCheckBox: JCheckBox

    init {
        this.jobsTree.model = DefaultTreeModel(DefaultMutableTreeNode(this.project.name))
        setupRefreshButton()
        //refresh()
    }

    private fun setupRefreshButton() {
        refreshButton.icon = AllIcons.Actions.Refresh
        refreshButton.text = "Refresh"
        refreshButton.addActionListener { refresh() }
    }

    private fun refresh() {
        val task: Task.Backgroundable = object : Task.Backgroundable(project, "Fetching Gitlab-CI jobs...", true) {
            override fun run(indicator: ProgressIndicator) {
                refreshButton.text = "..."
                try {
                    val output = runGclListCommand(schemaValidationCheckBox.isSelected)
                    if (output.exitCode != 0) {
                        val errorMessage = output.stderr.ifEmpty { output.stdout }
                        showErrorMessage(errorMessage)
                    } else {
                        // parse output
                        val gclJobs = GclJobFactory().parse(output.stdout)

                        // update UI
                        ApplicationManager.getApplication().invokeLater {
                            showGclJobsInJobTree(gclJobs)
                        }
                    }
                } catch (e: ExecutionException) {
                    e.message?.let { showErrorMessage(it) }
                }
                refreshButton.text = "Refresh"
            }

            override fun onCancel() {
                super.onCancel()
                refreshButton.text = "Refresh"
            }
        }
        ProgressManager.getInstance().run(task)
    }

    private fun showErrorMessage(errorMessage: String) {
        // show IDE balloon
        ApplicationManager.getApplication().invokeLater {
            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(
                    "<html>GitlabCiLocal Error:<br>${errorMessage}</html>",
                    null,
                    JBColor.BLACK,
                    JBColor.RED,
                    null
                )
                .createBalloon()
                .show(
                    JBPopupFactory.getInstance().guessBestPopupLocation(refreshButton),
                    Balloon.Position.atRight
                )
        }
    }

    private fun runGclListCommand(validateSchema: Boolean): ProcessOutput {
        val commandWithArgs = listOf(
            "gitlab-ci-local",
            "--list-json",
            "--json-schema-validation=${validateSchema}"
        )
        val projectBasePath = project.obtainRootDirPath()
        val cliCommand = GeneralCommandLine(
            WslUtils.rewriteToWslExec(projectBasePath, commandWithArgs)
        ).withRedirectErrorStream(true)
        cliCommand.workDirectory = File(projectBasePath)
        return ExecUtil.execAndGetOutput(cliCommand)
    }

    fun showGclJobsInJobTree(jobs: List<GclJob>) {
        val stages = GclStage.stagesFromJobs(jobs)

        // create jobsTree
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
        jobsTree.cellRenderer = renderer
        jobsTree.model = DefaultTreeModel(root)

        for (i in 0 until jobsTree.visibleRowCount) {
            jobsTree.expandPath(jobsTree.getPathForRow(i))
        }

        addMouseListener(jobsTree)
        jobsTree.fireTreeExpanded(TreePath(root))
    }

    private fun addMouseListener(tree: JTree) {
        val ml: MouseListener = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val selectedRow = tree.getRowForLocation(e.x, e.y)
                val selectedPath = tree.getPathForLocation(e.x, e.y)
                if (selectedPath != null && selectedPath.pathCount > 2 && selectedRow != -1 && e.clickCount == 2) {

                    val needsArgument = if (checkBox.isSelected) "--needs" else "--no-needs"
                    val schemaValidationArgument = "--schema-validation=${schemaValidationCheckBox.isSelected}"
                    val script = "${selectedPath.path[2]} $needsArgument $schemaValidationArgument"
                    val runManager = RunManager.getInstance(project)
                    val configuration = runManager.createConfiguration(script, GclRunConfigurationType::class.java)
                    runManager.addConfiguration(configuration)
                    // set the configuration as the selected configuration
                    runManager.selectedConfiguration = configuration
                    val executor: Executor = DefaultRunExecutor.getRunExecutorInstance()
                    ProgramRunnerUtil.executeConfiguration(configuration, executor)
                }
            }
        }
        tree.addMouseListener(ml)
    }

    val content: JComponent
        get() = panel
}