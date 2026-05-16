package com.hexglyph.a1.steganography

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HexGlyphSteganographyEngine {

    suspend fun encode(bitmap: Bitmap, payload: ByteArray): Bitmap =
        withContext(Dispatchers.Default) {
            val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val pixels = IntArray(mutable.width * mutable.height)

            mutable.getPixels(
                pixels,
                0,
                mutable.width,
                0,
                0,
                mutable.width,
                mutable.height
            )

            var bitIndex = 0

            for (i in pixels.indices) {
                if (bitIndex >= payload.size * 8) break

                val color = pixels[i]
                val bit = (payload[bitIndex / 8].toInt() shr (7 - bitIndex % 8)) and 1

                val blue = (color and 0xFF)
                val modifiedBlue = (blue and 0xFE) or bit

                pixels[i] = (color and 0xFFFFFF00.toInt()) or modifiedBlue
                bitIndex++
            }

            mutable.setPixels(
                pixels,
                0,
                mutable.width,
                0,
                0,
                mutable.width,
                mutable.height
            )

            mutable
        }

    suspend fun decode(bitmap: Bitmap, size: Int): ByteArray =
        withContext(Dispatchers.Default) {
            val pixels = IntArray(bitmap.width * bitmap.height)

            bitmap.getPixels(
                pixels,
                0,
                bitmap.width,
                0,
                0,
                bitmap.width,
                bitmap.height
            )

            val result = ByteArray(size)

            var bitIndex = 0

            for (pixel in pixels) {
                if (bitIndex >= size * 8) break

                val bit = pixel and 1
                result[bitIndex / 8] =
                    (result[bitIndex / 8].toInt() or (bit shl (7 - bitIndex % 8))).toByte()

                bitIndex++
            }

            result
        }
}
