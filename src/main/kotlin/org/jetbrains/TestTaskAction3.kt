package org.jetbrains

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class TestTaskAction3: AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val mainFile = TestTaskFiles.findMainKt(project) ?: run {
            Messages.showMessageDialog(project, "Main.kt not found in project.", "Task 3", Messages.getInformationIcon())
            return
        }

        FileEditorManager.getInstance(project).openFile(mainFile, true)

        val document = FileDocumentManager.getInstance().getDocument(mainFile) ?: run {
            Messages.showMessageDialog(project, "Unable to open document for Main.kt.", "Task 3", Messages.getInformationIcon())
            return
        }

        val psiFile = TestTaskFiles.toPsiFile(project, mainFile) ?: run {
            Messages.showMessageDialog(project, "Unable to resolve PSI for Main.kt.", "Task 3", Messages.getInformationIcon())
            return
        }

        DaemonCodeAnalyzer.getInstance(project).restart(psiFile)

        val markers = ReadAction.compute<List<com.intellij.codeInsight.daemon.LineMarkerInfo<*>>, RuntimeException> {
            DaemonCodeAnalyzerImpl.getLineMarkers(document, project)
        }

        val lines = mutableMapOf<Int, MutableList<String>>()

        markers.forEach { info ->
            val lineNumber = document.getLineNumber(info.startOffset) + 1
            val tooltip = info.lineMarkerTooltip?.let { StringUtil.stripHtml(it, false) }?.trim().orEmpty()
            val label = tooltip.ifEmpty {
                deriveLineMarkerType(info) ?: "Line marker"
            }
            lines.getOrPut(lineNumber) { mutableListOf() }.add(label)
        }

        val breakpoints = XDebuggerManager.getInstance(project)
            .breakpointManager
            .allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .filter { it.fileUrl == mainFile.url }

        breakpoints.forEach { bp ->
            val lineNumber = bp.line + 1
            lines.getOrPut(lineNumber) { mutableListOf() }.add("Breakpoint")
        }

        val formattedLines = lines
            .toSortedMap()
            .flatMap { (lineNumber, labels) ->
                labels.distinct().sorted().map { label ->
                    "Line $lineNumber: $label"
                }
            }

        val message = if (formattedLines.isEmpty()) {
            "No line markers found in ${mainFile.name}."
        } else {
            formattedLines.joinToString(separator = "\n")
        }

        Messages.showMessageDialog(project, message, "Task 3", Messages.getInformationIcon())
    }

    private fun deriveLineMarkerType(info: com.intellij.codeInsight.daemon.LineMarkerInfo<*>): String? {
        val renderer = info.createGutterRenderer() ?: return null
        val accessible = try {
            renderer.accessibleName
        } catch (_: IllegalStateException) {
            null
        }
        val raw = accessible?.takeIf { it.isNotBlank() }
            ?: renderer.featureId?.takeIf { it.isNotBlank() }
            ?: return null
        var label = raw.removePrefix("icon:").trim()
        if (label.equals("unknown", ignoreCase = true)) return null
        label = label.replace('_', ' ').replace('-', ' ')
        val parts = label.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (parts.isEmpty()) return null
        return parts.joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
        }
    }
}
