package org.jetbrains

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

object TestTaskFiles {
    const val KOTLIN_PLUGIN_ID = "org.jetbrains.kotlin"
    const val MAIN_KT = "Main.kt"

    fun findMainKt(project: Project): VirtualFile? = ReadAction.compute<VirtualFile?, RuntimeException> {
        val candidates = FilenameIndex.getVirtualFilesByName(MAIN_KT, GlobalSearchScope.projectScope(project))
        // prefer the conventional source root location over random matches
        candidates.firstOrNull {
            FileUtil.toSystemIndependentName(it.path).contains("/src/main/kotlin/")
        } ?: candidates.firstOrNull()
    }

    fun toPsiFile(project: Project, file: VirtualFile): PsiFile? =
        ReadAction.compute<PsiFile?, RuntimeException> { PsiManager.getInstance(project).findFile(file) }

    fun showInfo(project: Project, message: String, title: String) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(project, message, title, Messages.getInformationIcon())
        }
    }
}
