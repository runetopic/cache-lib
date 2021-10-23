package com.runetopic.loader.extension

import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
fun ByteArray.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(this)