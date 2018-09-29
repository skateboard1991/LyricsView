package com.skateboard.lyricsview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Scroller

class LyricsView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet)
{

    private var normalTextColor = Color.WHITE

    private var highlightTextColor = Color.GREEN

    private var textSize = 20

    private var lineHeight = 20

    private var lyricsList: List<LyricsItem> = mutableListOf<LyricsItem>()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var scrollOffset = 0

    private var time = 0

    private var scroller = Scroller(context)

    init
    {
        if (attributeSet != null)
        {
            paresAttributeSet(context, attributeSet)
        }
    }

    private fun paresAttributeSet(context: Context, attributeSet: AttributeSet)
    {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LyricsView)
        normalTextColor = typedArray.getColor(R.styleable.LyricsView_normalTextColor, normalTextColor)
        highlightTextColor = typedArray.getColor(R.styleable.LyricsView_highlightTextColor, highlightTextColor)
        textSize = typedArray.getDimensionPixelOffset(R.styleable.LyricsView_textSize, textSize)
        lineHeight = typedArray.getDimensionPixelOffset(R.styleable.LyricsView_lineHeight, lineHeight)
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        canvas?.let {
            it.clipRect(left, top, right, bottom)
            drawLyrics(it)
        }
    }

    private fun drawLyrics(canvas: Canvas)
    {
        paint.color = normalTextColor
        val centerX = width.toFloat() / 2
        for (i in 0 until lyricsList.size)
        {
            val lyricsItem = lyricsList[i]
            val textBound = Rect()
            paint.getTextBounds(lyricsItem.lyrics, 0, lyricsItem.lyrics.length, textBound)
            val topOffset = lineHeight.toFloat() * (i - scrollY)
            canvas.drawText(lyricsItem.lyrics, centerX - textBound.width() / 2, topOffset + lineHeight / 2 + textBound.height() / 2, paint)
            if (isInRange(time, lyricsItem))
            {
                scroll(i)
                drawHightLightColor(canvas, topOffset, textBound, lyricsItem)
            }
        }
    }

    private fun scroll(i: Int)
    {
        scrollOffset += i * lineHeight
        scroller.startScroll(scrollX, scrollY, 0, scrollOffset, 100)
    }

    override fun computeScroll()
    {
        if (scroller.computeScrollOffset())
        {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    private fun drawHightLightColor(canvas: Canvas, topOffset: Float, textBound: Rect, lyrics: LyricsItem)
    {
        val centerX = width.toFloat() / 2
        paint.color = highlightTextColor
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val offset = time.toFloat() - lyrics.start
        canvas.drawRect(centerX - textBound.width() / 2, topOffset, centerX - textBound.width() / 2 + offset, topOffset + lineHeight, paint)
        paint.xfermode = null
    }

    fun update(time: Int)
    {
        this.time = time
        postInvalidate()
    }

    private fun isInRange(time: Int, lyrics: LyricsItem): Boolean
    {
        return time >= lyrics.start && time <= lyrics.start + lyrics.duration
    }
}