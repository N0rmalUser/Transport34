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
    tintColor: Int
): Bitmap {
    val drawable = AppCompatResources.getDrawable(context, drawableRes)
        ?: error("Drawable resource $drawableRes not found")

    val width = 150
    val height = 150
    drawable.setBounds(0, 0, width, height)

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    val recolored = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = recolored[x, y]
            if (pixel != Color.WHITE && pixel != Color.TRANSPARENT) {
                recolored[x, y] = tintColor
            }
        }
    }

    return recolored
}
