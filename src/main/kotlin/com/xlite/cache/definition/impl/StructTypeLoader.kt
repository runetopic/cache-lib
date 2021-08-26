package com.xlite.cache.definition.impl

import com.xlite.cache.definition.Definition
import com.xlite.cache.definition.StructType
import com.xlite.cache.extension.readInt
import com.xlite.cache.extension.readMedium
import com.xlite.cache.extension.readString
import com.xlite.cache.extension.readUnsignedByte
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class StructTypeLoader: Definition<StructType> {
    override fun decode(id: Int, data: ByteArray): StructType {
        val struct = StructType(id)
        val buffer = ByteBuffer.wrap(data)

        while (true) {
            val opcode = buffer.readUnsignedByte()

            if (opcode == 0) {
                break
            }

            this.decodeValues(opcode, struct, buffer)
        }

        return struct
    }

    private fun decodeValues(opcode: Int, structType: StructType, buffer: ByteBuffer) {
        if (opcode == 249) {
            val length: Int = buffer.get().toInt() and 0xff

            for (i in 0 until length) {
                val isString = buffer.get().toInt() and 0xff == 1
                val key: Long = buffer.readMedium().toLong()
                var value: Any
                value = if (isString) {
                    buffer.readString()
                } else {
                    buffer.int
                }
                structType.params[key] = value
            }
        }
    }
}