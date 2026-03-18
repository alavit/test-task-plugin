package org.jetbrains

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class TestTaskAction3Test : BasePlatformTestCase() {

    private val sampleMain = """
        fun main() {
            println("Hello")
        }
    """.trimIndent()

    fun `test highlighting produces Run line marker on main function`() {
        val psiFile = myFixture.configureByText(TestTaskFiles.MAIN_KT, sampleMain)
        myFixture.doHighlighting()

        val doc = FileDocumentManager.getInstance().getDocument(psiFile.virtualFile)!!
        val markers = DaemonCodeAnalyzerImpl.getLineMarkers(doc, project)

        assertFalse("Expected at least one line marker (Run gutter icon)", markers.isEmpty())

        val hasMarkerOnMain = markers.any { doc.getLineNumber(it.startOffset) == 0 }
        assertTrue("Run marker expected on the main() declaration line", hasMarkerOnMain)

        markers.forEach {
            assertTrue(
                "Offset ${it.startOffset} out of document bounds",
                it.startOffset in 0 until doc.textLength
            )
        }
    }
}
