package com.example.worldmemo.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileUtils {
    fun saveUriToFile(context: Context, uriToSave: Uri, type: FileType): String {
        val inputStream = context.contentResolver.openInputStream(uriToSave)!!
        val filePath = createFile(type, context)
        val out: OutputStream = FileOutputStream(File(filePath))
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        out.close()
        inputStream.close()
        return filePath
    }

    enum class FileType {
        AUDIO, PHOTO
    }


    fun createFile(type: FileType, context: Context): String {
        val formatter = DateTimeFormatter.ofPattern("YYYY_MM_DD_HH_MM_SS")
        val currentDate = LocalDateTime.now().format(formatter)

        if (type == FileType.AUDIO) {
            return createAudioFile(context, currentDate)
        } else if (type == FileType.PHOTO) {
            return createPhotoFile(context, currentDate)
        }
        return ""
    }

    private fun createAudioFile(context: Context, currentDate: String): String {

        val fileName = "${currentDate}_audio.aac"

        return "${context.getExternalFilesDir(null)?.absolutePath}/$fileName"
    }

    private fun createPhotoFile(context: Context, currentDate: String): String {

        val fileName = "${currentDate}_photo.jpg"

        val dcimFolder = File("${context.getExternalFilesDir(null)?.absolutePath}/DCIM")


        if (!dcimFolder.isDirectory) {
            dcimFolder.mkdir()
        }

        return "${dcimFolder.absolutePath}/$fileName"
    }
}