package com.htv.player.util

import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.os.StatFs
import android.os.Build
import com.htv.player.data.model.StorageInfo
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    private val context: Context
) {
    private val removingStorages = mutableSetOf<String>()

    fun getStorageList(): List<StorageInfo> {
        val storages = mutableListOf<StorageInfo>()

        val internalStorage = getInternalStorage()
        if (internalStorage != null) {
            storages.add(internalStorage)
        }

        val externalStorages = getExternalStorages()
        storages.addAll(externalStorages)

        return storages
    }

    fun getDefaultStorage(): StorageInfo? {
        return getStorageList().firstOrNull()
    }

    private fun getInternalStorage(): StorageInfo? {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val totalSize = stat.totalBytes
            val availableSize = stat.availableBytes
            StorageInfo(
                path = path.absolutePath,
                name = "内部存储",
                totalSize = totalSize,
                availableSize = availableSize,
                isExternal = false
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getExternalStorages(): List<StorageInfo> {
        val storages = mutableListOf<StorageInfo>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val volumes = storageManager.storageVolumes

            for (volume in volumes) {
                if (volume.isRemovable) {
                    try {
                        val path = volume.directory?.absolutePath ?: continue
                        if (removingStorages.contains(path)) continue

                        val stat = StatFs(path)
                        val totalSize = stat.totalBytes
                        val availableSize = stat.availableBytes

                        storages.add(
                            StorageInfo(
                                path = path,
                                name = volume.getDescription(context) ?: "外接存储",
                                totalSize = totalSize,
                                availableSize = availableSize,
                                isExternal = true
                            )
                        )
                    } catch (e: Exception) {
                        continue
                    }
                }
            }
        } else {
            val externalDirs = context.getExternalFilesDirs(null)
            for (dir in externalDirs) {
                if (dir != null && dir.exists()) {
                    val path = dir.parentFile?.parentFile?.parentFile?.parentFile?.absolutePath
                    if (path != null && !removingStorages.contains(path)) {
                        try {
                            val stat = StatFs(path)
                            storages.add(
                                StorageInfo(
                                    path = path,
                                    name = "外接存储",
                                    totalSize = stat.totalBytes,
                                    availableSize = stat.availableBytes,
                                    isExternal = true
                                )
                            )
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
            }
        }

        return storages
    }

    fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    fun isStorageRemoving(path: String): Boolean {
        return removingStorages.contains(path)
    }

    fun markStorageRemoving(path: String) {
        removingStorages.add(path)
    }

    fun markStorageReady(path: String) {
        removingStorages.remove(path)
    }

    fun hasEnoughSpace(path: String, requiredSize: Long): Boolean {
        return try {
            val stat = StatFs(path)
            stat.availableBytes >= requiredSize
        } catch (e: Exception) {
            false
        }
    }

    fun getAvailableSpace(path: String): Long {
        return try {
            val stat = StatFs(path)
            stat.availableBytes
        } catch (e: Exception) {
            0L
        }
    }

    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getDownloadDir(storagePath: String, videoId: Int, episodeId: Int): File {
        val baseDir = File(storagePath, "HTVPlayer/Downloads/$videoId")
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
        return File(baseDir, "$episodeId.mp4")
    }

    fun formatSize(size: Long): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }
}
