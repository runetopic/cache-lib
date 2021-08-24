package com.xlite.cache.exception

import java.lang.RuntimeException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class ProtocolException(override val message: String): RuntimeException(message)