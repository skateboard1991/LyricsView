package com.skateboard.lyricsview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller


class LyricsView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet)
{

    private var normalTextColor = Color.WHITE

    private var highlightTextColor = Color.GREEN

    private var textSize = 40

    private var lineHeight = 80

    var lyricsList: List<LyricsItem> = mutableListOf<LyricsItem>()
        set(value)
        {
            field = value
            lyricsHeight = lyricsList.size * lineHeight
            if (isAttachedToWindow)
            {
                postInvalidate()
            }
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var time = 0L

    private var touchY = 0f

    private var scroller = Scroller(context)

    private var MIN_SCROLLY = 0

    private var MAX_SCROLLY = 0

    private var highLightPos = 0

    private var lyricsHeight = 0

    private var isTouching = false

    private var isScrolling=false

    private var speed=0

    init
    {
        if (attributeSet != null)
        {
            paresAttributeSet(context, attributeSet)
        }

        paint.textSize = textSize.toFloat()
        if (lineHeight < textSize)
        {
            lineHeight = 2 * textSize
        }
        post {

            MIN_SCROLLY = -height / 2
            scrollY = MIN_SCROLLY
            MAX_SCROLLY = lyricsHeight - height / 2
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
            val dstBitmap = Bitmap.createBitmap(width, lyricsHeight, Bitmap.Config.ARGB_8888)
            val dstCanvas = Canvas(dstBitmap)
            drawInfo(dstCanvas)
            drawLyrics(dstCanvas)
            drawHighlight(dstBitmap, it)
        }
    }

    private fun drawInfo(canvas: Canvas)
    {
        for (i in 0 until 4)
        {
            drawLyricItem(canvas, lyricsList[i], i)
        }
    }

    private fun drawLyrics(canvas: Canvas)
    {


        for (i in 5 until lyricsList.size)
        {
            drawLyricItem(canvas, lyricsList[i], i)
        }
    }

    private fun drawLyricItem(canvas: Canvas, lyricsItem: LyricsItem, index: Int)
    {
        paint.color = normalTextColor
        val centerX = width.toFloat() / 2
        val textBound = Rect()
        val lyricContent = getLyricDrawContent(lyricsItem)
        paint.getTextBounds(lyricContent, 0, lyricContent.length, textBound)
        val topOffset = lineHeight.toFloat() * index
        canvas.drawText(lyricContent, centerX - textBound.width() / 2, topOffset + lineHeight / 2 + textBound.height() / 2, paint)
    }

    private fun drawHighlight(dstBitmap: Bitmap, canvas: Canvas)
    {
        val centerX = width.toFloat() / 2
        paint.color = normalTextColor
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(dstBitmap, 0f, 0f, paint)
        val lyrics = lyricsList[highLightPos]
        val lyricsContent = getLyricDrawContent(lyrics)
        val textBound = Rect()
        val topOffset = highLightPos * lineHeight.toFloat()
        paint.getTextBounds(lyricsContent, 0, lyricsContent.length, textBound)
        val offset = (time.toFloat() - lyrics.start) / lyrics.duration.toFloat()
        paint.color = highlightTextColor
        canvas.drawRect(centerX - textBound.width() / 2, topOffset, centerX - textBound.width() / 2 + offset * textBound.width(), topOffset + lineHeight, paint)
        paint.xfermode = null
        dstBitmap.recycle()
    }

    private fun getLyricDrawContent(lyricsItem: LyricsItem): String
    {
        if (lyricsItem.ti.isNotEmpty())
        {
            return lyricsItem.ti
        }
        if (lyricsItem.ar.isNotEmpty())
        {
            return "歌手:" + lyricsItem.ar
        }
        if (lyricsItem.al.isNotEmpty())
        {
            return "专辑:" + lyricsItem.al
        }
        if (lyricsItem.by.isNotEmpty())
        {
            return "作曲:" + lyricsItem.by
        }
        return lyricsItem.lyrics
    }

    private fun scrollToPosition(i: Int,duration: Int=300)
    {
        var scrollOffset = lineHeight * i - scrollY - height / 2-speed*duration
        scrollOffset(scrollOffset,duration)
    }

    private fun scrollOffset(scrollOffset:Int,duration: Int=300)
    {
        var offset=scrollOffset
        if(scrollY+scrollOffset>MAX_SCROLLY)
        {
            offset=MAX_SCROLLY-scrollY
        }
        if(scrollY+scrollOffset<MIN_SCROLLY)
        {
            offset=MIN_SCROLLY-scrollY
        }
        scroller.startScroll(scrollX, scrollY, 0, offset, duration)
    }

    override fun computeScroll()
    {
        if (scroller.computeScrollOffset())
        {
            isScrolling=true
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
        else
        {
            isScrolling=false
            speed=0
        }
    }


    fun update(time: Long)
    {
        this.time = time
        for (i in 0 until lyricsList.size)
        {
            val lyricsItem = lyricsList[i]
            if (isInRange(time, lyricsItem))
            {
                if (highLightPos != i)
                {
                    highLightPos = i
                    if (!isTouching && !isScrolling)
                    {
                        scrollToPosition(i)
                    }
                } else
                {
                    postInvalidate()
                }
            }
        }

    }

    private fun isInRange(time: Long, lyrics: LyricsItem): Boolean
    {
        return time >= lyrics.start && time <= lyrics.start + lyrics.duration
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        val velocityTracker = VelocityTracker.obtain()
        velocityTracker.addMovement(event)
        when (event?.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                removeCallbacks(resetCallback)
                touchY = event.y
                isTouching = true
            }

            MotionEvent.ACTION_MOVE ->
            {
                scrollY -= (event.y - touchY).toInt()
                scrollY = Math.min(MAX_SCROLLY, Math.max(scrollY, MIN_SCROLLY))
                touchY = event.y
                velocityTracker.computeCurrentVelocity(1000)
                speed = velocityTracker.yVelocity.toInt()
            }

            MotionEvent.ACTION_UP ->
            {
                velocityTracker.clear()
                velocityTracker.recycle()
                scrollOffset(-speed,1000)
                postDelayed(resetCallback, 2000)
            }
        }
        return true
    }

    private val resetCallback = Runnable {
        isTouching = false
    }
}