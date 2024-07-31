package dk.cego.gitlabcilocal.plugin

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import dk.cego.gitlabcilocal.plugin.extensions.obtainRootDirPath
import java.io.File

class GclRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<GclRunConfigurationOptions>(project, factory, name) {

    override fun getOptions(): GclRunConfigurationOptions {
        return super.getOptions() as GclRunConfigurationOptions
    }

    var scriptName: String
        get() = options.scriptName
        set(scriptName) {
            options.scriptName = scriptName
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return GclRunConfigurationEditor()
    }

    override fun checkConfiguration() {
        println("GclRunConfiguration checkConfiguration called")
    }
    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState {
        return object : CommandLineState(executionEnvironment) {
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val projectBasePath = project.obtainRootDirPath()

                // original
                println("scriptName: $scriptName")
                println("name: $name")
                //val script = listOf(scriptName) + name.split(" ")

                val scriptParts = scriptName.split("--").map { it.trim() }
                val arguments = scriptParts.map { "--$it" }
                val commandList = listOf(scriptParts.first()) + arguments.subList(1, arguments.size)
                println("GclRunConfiguration - startProcess: script: $commandList")
                val commandLine = PtyCommandLine(
                    WslUtils.rewriteToWslExec(projectBasePath, commandList)
                ).withInitialColumns(PtyCommandLine.MAX_COLUMNS)
                commandLine.workDirectory = File(projectBasePath)
                commandLine.charset = Charsets.UTF_8
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }
}