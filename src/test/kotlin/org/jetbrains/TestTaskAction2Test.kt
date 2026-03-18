package org.jetbrains

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class TestTaskAction2Test : BasePlatformTestCase() {

    fun `test project fixture has modules and OrderEnumerator works`() {
        val modules = ModuleManager.getInstance(project).modules
        assertTrue("Light fixture should have at least one module", modules.isNotEmpty())

        val names = mutableListOf<String>()
        OrderEnumerator.orderEntries(modules.first())
            .recursively()
            .exportedOnly()
            .withoutModuleSourceEntries()
            .forEach { names.add(it.presentableName); true }
        assertNotNull(names)
    }
}
