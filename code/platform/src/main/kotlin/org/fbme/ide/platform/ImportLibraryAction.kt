package org.fbme.ide.platform

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.util.indexing.diagnostic.dump.paths.PortableFilePath.*
import jetbrains.mps.ide.actions.MPSCommonDataKeys
import jetbrains.mps.ide.newSolutionDialog.NewModuleUtil
import jetbrains.mps.kernel.model.SModelUtil
import jetbrains.mps.project.Solution
import jetbrains.mps.project.StandaloneMPSProject
import jetbrains.mps.project.structure.modules.SolutionDescriptor
import jetbrains.mps.smodel.SModelRepository
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ImportLibraryAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val filePath = "/Users/emgariko/work/itmo/thesis/fbme_fork/test.zip"

        val destinationDir = "/Users/emgariko/work/itmo/thesis/fbme_fork/solutions"
        try {
            val destDir = File(destinationDir)
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            ZipInputStream(FileInputStream(filePath)).use { zipInputStream ->
                var entry: ZipEntry
                while (zipInputStream.getNextEntry().also { entry = it } != null) {
                    val entryName = entry.name
                    val entryFile = File(destDir, entryName)

                    File(entryFile.getParent()).mkdirs()
                    FileOutputStream(entryFile).use { fos ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (zipInputStream.read(buffer).also { bytesRead = it } != -1) {
                            fos.write(buffer, 0, bytesRead)
                        }
                    }

                    zipInputStream.closeEntry()
                }
            }

            println("Unarchive completed successfully!")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        val mpsProject = e.getData(MPSCommonDataKeys.MPS_PROJECT) as StandaloneMPSProject

        val newSolution = NewModuleUtil.createSolution("Runtime.Base1", "/Users/emgariko/work/itmo/thesis/fbme_fork/solutions/Runtime.Base1", mpsProject)
        
    }
}
