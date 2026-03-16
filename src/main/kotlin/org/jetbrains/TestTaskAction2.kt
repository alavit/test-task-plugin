package org.jetbrains

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.OrderEntry
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.OrderRootType

class TestTaskAction2 : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        fun forEachOrderEntry(action: (OrderEntry) -> Unit) {
            ModuleManager.getInstance(project).modules.forEach { module ->
                OrderEnumerator.orderEntries(module)
                    .recursively()
                    .exportedOnly()
                    .withoutModuleSourceEntries()
                    .forEach { entry ->
                        action(entry)
                        true
                    }
            }
        }

        val sdkNames = buildSet {
            forEachOrderEntry { entry ->
                if (entry is JdkOrderEntry) {
                    entry.jdkName?.let(::add)
                }
            }
        }

        val libraryLines = buildSet {
            forEachOrderEntry { entry ->
                if (entry is LibraryOrderEntry) {
                    val library = entry.library ?: return@forEachOrderEntry
                    val libraryName = entry.presentableName
                    library.getFiles(OrderRootType.CLASSES).forEach { root ->
                        val jarName = root.name.removeSuffix("!/").removeSuffix("!")
                        add("$libraryName: $jarName")
                    }
                }
            }
        }

        val allLines = buildList {
            addAll(sdkNames.sorted())
            addAll(libraryLines.sorted())
        }

        val message = allLines
            .takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "\n")
            ?: "No external libraries or SDK found."

        Messages.showMessageDialog(project, message, "Task 2", Messages.getInformationIcon())
    }
}
