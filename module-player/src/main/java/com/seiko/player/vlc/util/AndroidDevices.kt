package com.seiko.player.vlc.util

import android.os.Environment
import com.seiko.player.util.getFileNameFromPath
import java.io.BufferedReader
import java.io.Closeable
import java.io.FileReader
import java.io.IOException
import java.util.*

object AndroidDevices {

    val EXTERNAL_PUBLIC_DIRECTORY: String = Environment.getExternalStorageDirectory().path

    private val typeWL = Arrays.asList("vfat", "exfat", "sdcardfs", "fuse", "ntfs", "fat32", "ext3", "ext4", "esdfs")
    private val typeBL = listOf("tmpfs")
    private val mountWL = arrayOf("/mnt", "/Removable", "/storage")
    private val mountBL = arrayOf(EXTERNAL_PUBLIC_DIRECTORY, "/mnt/secure", "/mnt/shell", "/mnt/asec", "/mnt/nand", "/mnt/runtime", "/mnt/obb", "/mnt/media_rw/extSdCard", "/mnt/media_rw/sdcard", "/storage/emulated", "/var/run/arc")
    private val deviceWL = arrayOf("/dev/block/vold", "/dev/fuse", "/mnt/media_rw", "passthrough")

    // skip if already in list or if type/mountpoint is blacklisted
    // check that device is in whitelist, and either type or mountpoint is in a whitelist
    val externalStorageDirectories: List<String>
        get() {
            var bufReader: BufferedReader? = null
            val list = ArrayList<String>()
            try {
                bufReader = BufferedReader(FileReader("/proc/mounts"))
                var line = bufReader.readLine()
                while (line != null) {

                    val tokens = StringTokenizer(line, " ")
                    val device = tokens.nextToken()
                    val mountpoint = tokens.nextToken().replace("\\\\040".toRegex(), " ")
                    val type = if (tokens.hasMoreTokens()) tokens.nextToken() else null
                    if (list.contains(mountpoint)
                        || typeBL.contains(type)
                        || mountBL.startsWith(mountpoint)) {
                        line = bufReader.readLine()
                        continue
                    }
                    if (deviceWL.startsWith(device) && (typeWL.contains(type) || mountWL.startsWith(mountpoint))) {
                        val position = list.containsName(mountpoint.substringBeforeLast('/'))
                        if (position > -1) list.removeAt(position)
                        list.add(mountpoint)
                    }
                    line = bufReader.readLine()
                }
            } catch (ignored: IOException) {
            } finally {
                close(bufReader)
            }
            list.remove(EXTERNAL_PUBLIC_DIRECTORY)
            return list
        }

    private fun close(closeable: Closeable?): Boolean {
        if (closeable != null) {
            try {
                closeable.close()
                return true
            } catch (e: IOException) {
            }
        }
        return false
    }
}

private fun Array<String>.startsWith(text: String) = any { text.startsWith(it) }

private fun List<String>.containsName(text: String) = indexOfLast { it.endsWith(text) }