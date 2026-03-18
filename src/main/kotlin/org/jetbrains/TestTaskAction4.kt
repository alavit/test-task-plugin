package org.jetbrains

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private const val EXECUTION_TIMEOUT_SEC = 60L

class TestTaskAction4 : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val mainFile = TestTaskFiles.findMainKt(project) ?: run {
            TestTaskFiles.showInfo(project, "${TestTaskFiles.MAIN_KT} not found in project.", "Task 4")
            return
        }
        val psiFile = TestTaskFiles.toPsiFile(project, mainFile) ?: return

        val settings = resolveRunConfiguration(psiFile, project) ?: run {
            TestTaskFiles.showInfo(project, "No run configuration found for ${TestTaskFiles.MAIN_KT}.", "Task 4")
            return
        }

        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val env = ExecutionEnvironmentBuilder.createOrNull(executor, settings) ?: run {
            TestTaskFiles.showInfo(project, "Unable to create execution environment.", "Task 4")
            return
        }

        executeAndReport(project, env)
    }

    private fun resolveRunConfiguration(psiFile: PsiFile, project: Project): RunnerAndConfigurationSettings? {
        val context = ConfigurationContext(psiFile)
        val settings = context.configuration
            ?: context.configurationsFromContext?.firstOrNull()?.configurationSettings
            ?: return null

        val runManager = RunManager.getInstance(project)
        if (settings !in runManager.allSettings) {
            settings.isTemporary = true
            runManager.addConfiguration(settings)
        }
        runManager.selectedConfiguration = settings
        return settings
    }

    private fun executeAndReport(project: Project, envBuilder: ExecutionEnvironmentBuilder) {
        val reported = AtomicBoolean(false)

        fun report(message: String) {
            if (reported.compareAndSet(false, true)) {
                TestTaskFiles.showInfo(project, message, "Task 4")
            }
        }

        val callback = object : ProgramRunner.Callback {
            override fun processStarted(descriptor: RunContentDescriptor) {
                val handler = descriptor.processHandler
                if (handler == null) {
                    report("Process handler is not available.")
                    return
                }
                handler.addProcessListener(object : ProcessAdapter() {
                    override fun processTerminated(event: ProcessEvent) {
                        val code = event.exitCode
                        report(
                            if (code == 0) "${TestTaskFiles.MAIN_KT} finished successfully (exit code 0)."
                            else "${TestTaskFiles.MAIN_KT} finished with exit code $code."
                        )
                    }
                })
            }

            override fun processNotStarted() {
                report("Process did not start.")
            }
        }

        // guard against hanging processes
        AppExecutorUtil.getAppScheduledExecutorService().schedule({
            report("Execution timed out after $EXECUTION_TIMEOUT_SEC seconds.")
        }, EXECUTION_TIMEOUT_SEC, TimeUnit.SECONDS)

        val environment = envBuilder.build(callback)
        ProgramRunnerUtil.executeConfigurationAsync(environment, false, false, callback)
    }
}
