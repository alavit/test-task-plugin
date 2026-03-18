package org.jetbrains

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import java.util.concurrent.TimeUnit

private const val POLL_DELAY_MS = 200L
private const val MAX_POLL_ATTEMPTS = 50

class TestTaskAction3 : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val mainFile = TestTaskFiles.findMainKt(project) ?: run {
            TestTaskFiles.showInfo(project, "${TestTaskFiles.MAIN_KT} not found in project.", "Task 3")
            return
        }

        FileEditorManager.getInstance(project).openFile(mainFile, true)

        val document = FileDocumentManager.getInstance().getDocument(mainFile) ?: return
        val psiFile = TestTaskFiles.toPsiFile(project, mainFile) ?: return

        val analyzer = DaemonCodeAnalyzer.getInstance(project) as DaemonCodeAnalyzerImpl
        analyzer.restart(psiFile)

        // daemon analysis is async — poll until it finishes, then read markers
        pollForMarkers(analyzer, project, mainFile, document)
    }

    private fun pollForMarkers(
        analyzer: DaemonCodeAnalyzerImpl,
        project: Project,
        file: VirtualFile,
        document: Document,
    ) {
        var attempt = 0
        fun schedule() {
            AppExecutorUtil.getAppScheduledExecutorService().schedule({
                attempt++
                val pending = ReadAction.compute<Boolean, RuntimeException> { analyzer.isRunningOrPending }
                if (pending && attempt < MAX_POLL_ATTEMPTS) {
                    schedule()
                    return@schedule
                }

                val message = collectGutterInfo(project, file, document)
                TestTaskFiles.showInfo(project, message, "Task 3")
            }, POLL_DELAY_MS, TimeUnit.MILLISECONDS)
        }
        schedule()
    }

    private fun collectGutterInfo(project: Project, file: VirtualFile, document: Document): String {
        val lines = mutableMapOf<Int, MutableList<String>>()

        // line markers from code analysis (Run, Override, Implement, etc.)
        val markers = ReadAction.compute<List<LineMarkerInfo<*>>, RuntimeException> {
            DaemonCodeAnalyzerImpl.getLineMarkers(document, project)
        }
        for (info in markers) {
            val line = document.getLineNumber(info.startOffset) + 1
            val label = info.lineMarkerTooltip
                ?.let { StringUtil.stripHtml(it, false).trim() }
                ?.ifEmpty { null }
                ?: "Line marker"
            lines.getOrPut(line) { mutableListOf() }.add(label)
        }

        // breakpoints are a separate gutter concept but still visible "on the left"
        val breakpoints = XDebuggerManager.getInstance(project)
            .breakpointManager.allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .filter { it.fileUrl == file.url }
        for (bp in breakpoints) {
            lines.getOrPut(bp.line + 1) { mutableListOf() }.add("Breakpoint")
        }

        if (lines.isEmpty()) return "No line markers found."

        return lines.toSortedMap().flatMap { (line, labels) ->
            labels.distinct().sorted().map { "Line $line: $it" }
        }.joinToString("\n")
    }
}
