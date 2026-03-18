package org.jetbrains

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.ui.Messages

class TestTaskAction2 : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val sdkNames = mutableSetOf<String>()
        val libraryLines = mutableSetOf<String>()

        for (module in ModuleManager.getInstance(project).modules) {
            OrderEnumerator.orderEntries(module)
                .recursively()
                .exportedOnly()
                .withoutModuleSourceEntries()
                .forEach { entry ->
                    when (entry) {
                        is JdkOrderEntry -> entry.jdkName?.let(sdkNames::add)
                        is LibraryOrderEntry -> collectLibraryJars(entry, libraryLines)
                    }
                    true // continue enumeration
                }
        }

        val text = (sdkNames.sorted() + libraryLines.sorted())
            .joinToString("\n")
            .ifEmpty { "No external libraries or SDK found." }

        Messages.showMessageDialog(project, text, "Task 2", Messages.getInformationIcon())
    }

    private fun collectLibraryJars(entry: LibraryOrderEntry, dest: MutableSet<String>) {
        val library = entry.library ?: return
        val name = entry.presentableName
        for (root in library.getFiles(OrderRootType.CLASSES)) {
            // VFS appends "!/" to jar entries — strip it for clean display
            val jarName = root.name.removeSuffix("!/").removeSuffix("!")
            dest.add("$name: $jarName")
        }
    }
}
