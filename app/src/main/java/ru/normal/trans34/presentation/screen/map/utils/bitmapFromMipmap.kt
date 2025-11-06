package ru.normal.trans34.presentation.screen.map.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

fun bitmapFromMipmap(context: Context, @DrawableRes resId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, resId)
        ?: throw IllegalArgumentException("Drawable not found")
    val size = 45
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)
    return bitmap
}
