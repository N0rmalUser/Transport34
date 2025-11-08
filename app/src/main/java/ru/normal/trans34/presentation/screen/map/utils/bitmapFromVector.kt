package ru.normal.trans34.presentation.screen.map.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set

fun bitmapFromVector(
    context: Context,
    drawableRes: Int,
    size: Int = 48,
    tintColor: Int? = null
): Bitmap {
    val drawable = AppCompatResources.getDrawable(context, drawableRes)
        ?: error("Drawable resource $drawableRes not found")

    drawable.setBounds(0, 0, size, size)

    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    if (tintColor == null) return bitmap

    val recolored = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    for (x in 0 until size) {
        for (y in 0 until size) {
            val pixel = recolored[x, y]
            if (pixel != Color.WHITE && pixel != Color.TRANSPARENT) {
                recolored[x, y] = tintColor
            }
        }
    }

    return recolored
}
