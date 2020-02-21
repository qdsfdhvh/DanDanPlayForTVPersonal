package com.seiko.player.util

import java.io.File

/**
 * ../aaa.txt -> aaa.txt
 */
fun String.getFileNameFromPath() = substringAfterLast(File.separator)

/**
 * .../aaa.txt -> txt
 */
fun String.getFileExtFromPath() = substringAfterLast('.', "")

/**
 * .../aaa.txt -> .../aaa
 */
fun String.getFilePathWithOutExt() = substringBeforeLast('.', "")