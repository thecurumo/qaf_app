package com.thecurumo.qaf

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import java.io.File

@CapacitorPlugin(name = "DatabaseSetup")
class DatabaseSetupPlugin : Plugin() {

    private val databaseFileName = "qaf.db"
    private val assetDatabasePath = "public/assets/databases/qaf.db"

    @PluginMethod
    fun ensureDatabaseReady(call: PluginCall) {
        try {
            val context = context

            val targetFile = context.getDatabasePath(databaseFileName)

            if (targetFile.exists() && targetFile.length() > 0) {
                val result = JSObject()
                result.put("alreadyExists", true)
                result.put("path", targetFile.absolutePath)
                result.put("size", targetFile.length())
                call.resolve(result)
                return
            }

            targetFile.parentFile?.let { parent ->
                if (!parent.exists() && !parent.mkdirs()) {
                    call.reject(
                        "خطا: امکان ایجاد پوشه دیتابیس وجود ندارد: ${parent.absolutePath}"
                    )
                    return
                }
            }

            context.assets.open(assetDatabasePath).use { inputStream ->
                targetFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (!targetFile.exists() || targetFile.length() == 0L) {
                targetFile.delete()
                call.reject("خطا: فایل دیتابیس کپی شد اما فایل نهایی معتبر نیست.")
                return
            }

            val result = JSObject()
            result.put("alreadyExists", false)
            result.put("path", targetFile.absolutePath)
            result.put("size", targetFile.length())
            call.resolve(result)

        } catch (e: Exception) {
            call.reject(
                "خطا در آماده‌سازی دیتابیس: ${e.javaClass.simpleName}: ${e.message}",
                e
            )
        }
    }
}
