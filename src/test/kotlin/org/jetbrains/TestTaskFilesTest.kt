package org.jetbrains

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class TestTaskFilesTest : BasePlatformTestCase() {

    fun `test findMainKt locates file by name`() {
        myFixture.configureByText(TestTaskFiles.MAIN_KT, "fun main() {}")
        val found = TestTaskFiles.findMainKt(project)
        assertNotNull(found)
        assertEquals(TestTaskFiles.MAIN_KT, found!!.name)
    }

    fun `test findMainKt returns null when file is absent`() {
        myFixture.configureByText("SomethingElse.kt", "fun other() {}")
        assertNull(TestTaskFiles.findMainKt(project))
    }

    fun `test toPsiFile resolves virtual file to Kotlin PSI`() {
        myFixture.configureByText(TestTaskFiles.MAIN_KT, "fun main() {}")
        val vFile = TestTaskFiles.findMainKt(project)!!
        val psi = TestTaskFiles.toPsiFile(project, vFile)
        assertNotNull(psi)
        assertTrue(psi!!.fileType.name.contains("Kotlin", ignoreCase = true))
    }
}
