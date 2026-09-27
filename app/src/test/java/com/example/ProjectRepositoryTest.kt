package com.example

import com.example.data.TemplateGenerator
import com.example.data.model.ProjectEntity
import org.junit.Assert.*
import org.junit.Test

class ProjectRepositoryTest {

    @Test
    fun testTemplateGeneratorGeneratesManifestAndFiles() {
        val project = ProjectEntity(
            id = 1,
            name = "TestApp",
            packageName = "com.test.app",
            location = "/storage/emulated/0/AndroidIDEProjects",
            language = "Kotlin",
            minSdk = "API 24",
            template = "Compose Activity",
            notificationsPermission = true,
            cameraPermission = true
        )

        val files = TemplateGenerator.generateFilesForProject(project)
        assertTrue(files.isNotEmpty())

        val manifestFile = files.find { it.relativePath == "app/src/main/AndroidManifest.xml" }
        assertNotNull(manifestFile)
        assertTrue(manifestFile!!.content.contains("android.permission.POST_NOTIFICATIONS"))
        assertTrue(manifestFile.content.contains("android.permission.CAMERA"))
    }

    @Test
    fun testPythonTemplateGeneration() {
        val project = ProjectEntity(
            id = 2,
            name = "PyMobile",
            packageName = "com.py.mobile",
            location = "/storage/emulated/0/AndroidIDEProjects",
            language = "Python",
            minSdk = "API 26",
            template = "Python App"
        )

        val files = TemplateGenerator.generateFilesForProject(project)
        val mainPy = files.find { it.relativePath == "main.py" }
        assertNotNull(mainPy)
        assertTrue(mainPy!!.content.contains("Python 3.11"))
    }
}
