package org.jetbrains

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

object TestTaskFiles {
    fun findMainKt(project: Project): VirtualFile? = ReadAction.compute<VirtualFile?, RuntimeException> {
        val files = FilenameIndex.getVirtualFilesByName("Main.kt", GlobalSearchScope.projectScope(project))
        files.firstOrNull { file ->
            val path = FileUtil.toSystemIndependentName(file.path)
            path.contains("/src/main/kotlin/")
        } ?: files.firstOrNull()
    }

    fun toPsiFile(project: Project, file: VirtualFile): PsiFile? =
        ReadAction.compute<PsiFile?, RuntimeException> { PsiManager.getInstance(project).findFile(file) }
}
