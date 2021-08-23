package com.xlite.cache.exception

import java.lang.RuntimeException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class IndexFileException(override val message: String): RuntimeException(message)