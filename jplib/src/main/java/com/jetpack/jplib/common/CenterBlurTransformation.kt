package com.jetpack.jplib.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import jp.wasabeef.glide.transformations.internal.RSBlur
import android.renderscript.RSRuntimeException
import jp.wasabeef.glide.transformations.BitmapTransformation
import jp.wasabeef.glide.transformations.internal.FastBlur
import java.security.MessageDigest

class CenterBlurTransformation(
    private val radius: Int,
    private val sampling: Int,
    private val context: Context?
) : BitmapTransformation() {

    companion object {
        private const val VERSION = 1
        private const val ID = "jp.wasabeef.glide.transformations.BlurTransformation." + VERSION
        private const val MAX_RADIUS = 25
        private const val DEFAULT_DOWN_SAMPLING = 1
    }
    constructor(context: Context?) : this(MAX_RADIUS, DEFAULT_DOWN_SAMPLING, context)
    constructor(radius: Int, context: Context?) : this(radius, DEFAULT_DOWN_SAMPLING, context)

    override fun transform(
        context: Context, pool: BitmapPool,
        toTransform: Bitmap, outWidth: Int, outHeight: Int
    ): Bitmap {
        val bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
        return blurCrop(pool, bitmap)!!
    }

    private fun blurCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        val width = source.width
        val height = source.height
        val scaledWidth = width / sampling
        val scaledHeight = height / sampling
        var bitmap: Bitmap? = pool[scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(bitmap!!)
        canvas.scale(1 / sampling.toFloat(), 1 / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(source, 0f, 0f, paint)
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                RSBlur.blur(context, bitmap, radius)
            } catch (e: RSRuntimeException) {
                FastBlur.blur(bitmap, radius, true)
            }
        } else {
            FastBlur.blur(bitmap, radius, true)
        }
        return bitmap
    }

    override fun equals(o: Any?): Boolean {
        return o is CenterBlurTransformation && o.radius == radius && o.sampling == sampling
    }

    override fun hashCode(): Int {
        return ID.hashCode() + radius * 1000 + sampling * 10
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}


}