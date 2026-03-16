package org.jetbrains

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.extensions.PluginId
import com.intellij.ide.plugins.PluginManagerCore

class TestTaskAction1 : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val version = PluginManagerCore.getPlugin(PluginId.getId("org.jetbrains.kotlin"))
            ?.version
            ?: "not found"
        Messages.showMessageDialog(project, "Kotlin plugin version: $version", "Task 1", Messages.getInformationIcon())
    }
}
