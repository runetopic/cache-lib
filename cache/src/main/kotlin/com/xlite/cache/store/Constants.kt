package com.xlite.cache.store

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal object Constants {
    private const val PREFIX = "main_file_cache"
    const val MAIN_FILE_IDX = "$PREFIX.idx"
    const val MAIN_FILE_DAT = "$PREFIX.dat2"
    const val MAIN_FILE_255 = "$PREFIX.idx255"

    const val SECTOR_SIZE = 520
    const val MAIN_INDEX_ID = 255

    val BZIP_HEADER = byteArrayOf(
        'B'.code.toByte(),
        'Z'.code.toByte(),
        'h'.code.toByte(),
        '1'.code.toByte()
    )
}