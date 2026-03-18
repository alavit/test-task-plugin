package org.jetbrains

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class TestTaskAction1Test : BasePlatformTestCase() {

    fun `test Kotlin plugin is available and has a version`() {
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(TestTaskFiles.KOTLIN_PLUGIN_ID))
        assertNotNull("Kotlin plugin must be present in the test IDE", plugin)
        assertTrue("Version must not be blank", plugin!!.version.isNotBlank())
    }
}
