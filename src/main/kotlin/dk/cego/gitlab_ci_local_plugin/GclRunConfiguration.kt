package dk.cego.gitlab_ci_local_plugin

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.io.File

class GclRunConfiguration(project: Project?, factory: ConfigurationFactory?, name: String?) :

    RunConfigurationBase<GclRunConfigurationOptions?>(project!!, factory, name) {

    override fun getOptions(): GclRunConfigurationOptions {
        return super.getOptions() as GclRunConfigurationOptions
    }

    var scriptName: String?
        get() = options.scriptName
        set(scriptName) {
            options.scriptName = scriptName
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return GclRunConfigurationEditor()
    }

    override fun checkConfiguration() {}
    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState {
        return object : CommandLineState(executionEnvironment) {
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val needs = name.contains("--needs")
                val job = name.split("--")[0].trim()
                val script = listOf(scriptName!!) + job + (if (needs) "--needs" else "--no-needs")
                val commandLine = PtyCommandLine(WslUtils.rewriteToWslExec(project.basePath!!, script)).withInitialColumns(PtyCommandLine.MAX_COLUMNS)
                commandLine.workDirectory = File(project.basePath!!)
                commandLine.charset = Charsets.UTF_8
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }
}
