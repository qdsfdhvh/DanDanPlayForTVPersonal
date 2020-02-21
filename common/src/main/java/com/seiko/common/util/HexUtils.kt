package com.seiko.common.util


private val HEX_DIGIT_LOWER_CHARS =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
private inline infix fun Byte.shr(other: Int): Int = toInt() shr other

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
private inline infix fun Byte.and(other: Int): Int = toInt() and other

/**
 * 将此数组转为Hex
 * @return Hex
 */
fun ByteArray.toHexString(): String {
    if (this.isEmpty()) return ""

    val result = CharArray(this.size * 2)
    var c = 0
    for (b in this) {
        result[c++] = HEX_DIGIT_LOWER_CHARS[b shr 4 and 0xf]
        result[c++] = HEX_DIGIT_LOWER_CHARS[b       and 0xf]
    }
    return String(result)
}

/**
 * 将Hex转为数组
 * @return 数组
 */
fun String.toModBusByteArray(): ByteArray {
    if (this.isEmpty()) return ByteArray(0)

    val result = ByteArray(this.length / 2)
    for (i in this.indices step 2) {
        val firstIndex = HEX_DIGIT_LOWER_CHARS.indexOf(this[i])
        val secondIndex = HEX_DIGIT_LOWER_CHARS.indexOf(this[i + 1])
        val octet = firstIndex shl 4 or secondIndex
        result[i shr 1] = octet.toByte()
    }
    return result
}