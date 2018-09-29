package com.skateboard.lyricsview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class LyricsItemView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet)
{

    var normalTextColor = Color.WHITE
        set(value)
        {
            field = value
            if (isAttachedToWindow)
            {
                postInvalidate()
            }
        }

    var highlightTextColor = Color.GREEN
        set(value)
        {
            field = value
            if (isAttachedToWindow)
            {
                postInvalidate()
            }
        }

    var textSize: Int = 15
        set(value)
        {
            field = value
            if (isAttachedToWindow)
            {
                postInvalidate()
            }
        }

    var lineSpec: Int = 20
        set(value)
        {
            field = value
            if (isAttachedToWindow)
            {
                postInvalidate()
            }
        }

    private var offset = 0.0f

    var lyricsItem: LyricsItem? = null
        set(value)
        {
            field = value
            offset = 0.0f
            if (isAttachedToWindow)
            {
                postInvalidate()
            }
        }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)

    fun isInrange(time: Int): Boolean
    {
        lyricsItem?.let {

            if (time >= it.start && time <= it.start + it.duration)
            {
                offset = (time - it.start) / (it.duration.toFloat())
                return true
            }
            return false
        }
        offset = 0f
        return false
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        canvas?.let {
            val centerX = width.toFloat() / 2
            val centerY = height.toFloat() / 2
            drawText(it, centerX, centerY)
            drawHightlight(it, centerX, centerY)
        }
    }

    private fun drawText(canvas: Canvas, centerX: Float, centerY: Float)
    {
        paint.color = normalTextColor
        val textBounds = Rect()
        paint.getTextBounds(lyricsItem?.lyrics ?: "", 0, (lyricsItem?.lyrics
                ?: "").length, textBounds)
        canvas.drawText(lyricsItem?.lyrics
                ?: "", centerX - textBounds.width() / 2, centerY + textBounds.height() / 2, paint)
    }

    private fun drawHightlight(canvas: Canvas, centerX: Float, centerY: Float)
    {
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        paint.color = highlightTextColor
        canvas.drawRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), paint)
        paint.xfermode = null
    }
}