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

    val cp1252Identifiers = charArrayOf(
        '\u20ac',
        '\u0000',
        '\u201a',
        '\u0192',
        '\u201e',
        '\u2026',
        '\u2020',
        '\u2021',
        '\u02c6',
        '\u2030',
        '\u0160',
        '\u2039',
        '\u0152',
        '\u0000',
        '\u017d',
        '\u0000',
        '\u0000',
        '\u2018',
        '\u2019',
        '\u201c',
        '\u201d',
        '\u2022',
        '\u2013',
        '\u2014',
        '\u02dc',
        '\u2122',
        '\u0161',
        '\u203a',
        '\u0153',
        '\u0000',
        '\u017e',
        '\u0178'
    )


    val BZIP_HEADER = byteArrayOf(
        'B'.code.toByte(),
        'Z'.code.toByte(),
        'h'.code.toByte(),
        '1'.code.toByte()
    )
}