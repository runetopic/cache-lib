package com.xlite.cache.file

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
object FileConstants {
    private const val PREFIX = "main_file_cache"
    const val MAIN_FILE_IDX = "$PREFIX.idx"
    const val MAIN_FILE_DAT = "$PREFIX.dat2"
    const val MAIN_FILE_255 = "$PREFIX.idx255"

    const val SECTOR_SIZE = 520
    const val MAIN_INDEX_ID = 255

    val BZIP_HEADER = byteArrayOf(
        'B'.toByte(),
        'Z'.toByte(),
        'h'.toByte(),
        '1' .toByte())
}