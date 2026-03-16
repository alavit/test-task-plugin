package org.jetbrains

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class TestTaskAction4 : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val mainFile = TestTaskFiles.findMainKt(project) ?: run {
            Messages.showMessageDialog(project, "Main.kt not found in project.", "Task 4", Messages.getInformationIcon())
            return
        }

        val psiFile = TestTaskFiles.toPsiFile(project, mainFile) ?: run {
            Messages.showMessageDialog(project, "Unable to resolve PSI for Main.kt.", "Task 4", Messages.getInformationIcon())
            return
        }

        val context = ConfigurationContext(psiFile)
        val settings = context.configuration
            ?: context.configurationsFromContext?.firstOrNull()?.configurationSettings
            ?: run {
                Messages.showMessageDialog(project, "Run configuration for Main.kt not found.", "Task 4", Messages.getInformationIcon())
                return
            }

        val runManager = RunManager.getInstance(project)
        if (settings !in runManager.allSettings) {
            settings.isTemporary = true
            runManager.addConfiguration(settings)
        }
        runManager.selectedConfiguration = settings

        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val builder = ExecutionEnvironmentBuilder.createOrNull(executor, settings) ?: run {
            Messages.showMessageDialog(project, "Unable to create execution environment.", "Task 4", Messages.getInformationIcon())
            return
        }

        val callback = object : ProgramRunner.Callback {
            override fun processStarted(descriptor: RunContentDescriptor) {
                descriptor.processHandler?.addProcessListener(object : ProcessAdapter() {
                    override fun processTerminated(event: ProcessEvent) {
                        val exitCode = event.exitCode
                        val message = if (exitCode == 0) {
                            "Main.kt finished successfully (exit code 0)."
                        } else {
                            "Main.kt finished with exit code $exitCode."
                        }
                        showResult(project, message)
                    }
                }) ?: showResult(project, "Process handler is not available.")
            }

            override fun processNotStarted() {
                showResult(project, "Process did not start.")
            }
        }

        val environment = builder.build(callback)
        ProgramRunnerUtil.executeConfigurationAsync(environment, false, false, callback)
    }

    private fun showResult(project: Project, message: String) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(project, message, "Task 4", Messages.getInformationIcon())
        }
    }
}
