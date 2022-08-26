package com.example.double_services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withMatrix
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.example.double_services.type.Bonus
import com.example.double_services.type.Entity
import kotlin.math.atan2
import kotlin.properties.Delegates
import kotlin.random.Random

class MyView(context: Context?) : View(context), View.OnTouchListener{

    init {
        setOnTouchListener(this)
    }

    private val myPaint = Paint().apply {
        strokeWidth = 2f
        style = Paint.Style.FILL
        isAntiAlias = true
        color = ContextCompat.getColor(context!!, R.color.stats_background)
    }
    private val textStatsPaint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.stats_text)
        textSize = 25f * resources.displayMetrics.density
        isFakeBoldText = true
    }
    private val myPaint2 = Paint().apply {
        strokeWidth = 25f
        style = Paint.Style.FILL
        isAntiAlias = true
        color = Color.BLACK
    }
    private val myPaint3 = Paint().apply {
        strokeWidth = 25f
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.RED
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.text_in_result_record)
        textSize = 25f * resources.displayMetrics.density
        isFakeBoldText = true
    }

    private val dp = resources.displayMetrics.density
    private val frameRate = 1000L/60L
    private var entityToDelete = mutableListOf<Entity>()

    private var firstLoop = true
    private lateinit var entities: MutableList<Entity>
    private var w by Delegates.notNull<Float>()
    private var h by Delegates.notNull<Float>()
    private var spawnCdr by Delegates.notNull<Long>()
    private var spawnTime by Delegates.notNull<Long>()
    private var maxSpeedR by Delegates.notNull<Int>()
    private var minSpeedR by Delegates.notNull<Int>()
    private var maxPositionR by Delegates.notNull<Int>()
    private var minPositionR by Delegates.notNull<Int>()
    private var maxSpeedVariationX by Delegates.notNull<Float>()
    private var maxSpeedVariationY by Delegates.notNull<Float>()
    private var scaleMax by Delegates.notNull<Float>()
    private var scaleMin by Delegates.notNull<Float>()
    private var logoDimMax by Delegates.notNull<Int>()
    private var logoDimMin by Delegates.notNull<Int>()
    private var timeFade by Delegates.notNull<Long>()
    private var clickUp = true
    private var popped = 0
    private var upDiff = 1L
    private var hearts = 3
    private var clicksToNewHeart = 0
    private var clicksToNewHeartAt = 50
    private var newHeartTime = 0L
    private var end = false
    private var clickPosition = mutableListOf<Float>()
    private val bonuses = mutableListOf<Bonus>()
    lateinit var activity: FragmentActivity
    lateinit var background: Bitmap
    lateinit var backgroundStats: Bitmap
    lateinit var heart: Bitmap
    var start = System.currentTimeMillis()
    var pause = false
    private lateinit var mediaPlayerLifeUp: MediaPlayer
    private lateinit var mediaPlayerHit: MediaPlayer
    private lateinit var mediaPlayerClick: MediaPlayer



    private fun initialize() {
        entities = mutableListOf()
        w = width.toFloat()
        h = height.toFloat()
        start = System.currentTimeMillis()
        spawnTime = 3000L
        spawnCdr = spawnTime
        maxSpeedR = 470
        minSpeedR = 200
        maxPositionR = 160
        minPositionR = 110
        maxSpeedVariationX = 0.02f * dp
        maxSpeedVariationY = 0.02f * dp
        scaleMax = 1.08f
        scaleMin = 0.92f
        logoDimMax = 50
        logoDimMin = 35
        timeFade = 1700L

        mediaPlayerLifeUp = MediaPlayer.create(activity.applicationContext, R.raw.life_up)
        mediaPlayerLifeUp.setVolume(0.5f, 0.5f)

        mediaPlayerHit = MediaPlayer.create(activity.applicationContext, R.raw.hit)
        mediaPlayerHit.setVolume(1.0f, 1.0f)

        mediaPlayerClick = MediaPlayer.create(activity.applicationContext, R.raw.click)
        mediaPlayerClick.setVolume(1.0f, 1.0f)

        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.background,
            null
        )?.toBitmap(
            w.toInt(),
            h.toInt()
        )!!

        backgroundStats = ResourcesCompat.getDrawable(
            resources,
            R.drawable.record_background,
            null
        )?.toBitmap(
            w.toInt() + 20*dp.toInt(),
            90*dp.toInt()
        )!!

        heart = ResourcesCompat.getDrawable(
            resources,
            R.drawable.heart,
            null
        )?.toBitmap(
            (40f*dp).toInt(),
            (40f*dp).toInt()
        )!!
    }

    private fun createEntity(now: Long) {
        // reset CDR
        spawnCdr = now

        // dimension of logo
        val logoH = Random.nextInt(logoDimMin, logoDimMax).toFloat() * dp
        val logoW = 2.2f * logoH

        // random position and speed sign based on quadrant
        var speedX = 0f
        var speedY = 0f
        var positionX = 0f
        var positionY = 0f
        when (Random.nextInt(0, 8)) {
            // 0: top-left
            0 -> {
                speedX = dp * Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f
                speedY = h/w * speedX
                positionX = -logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
                positionY = -logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
            }
            // 1: top
            1 -> {
                speedX = 0f
                speedY = dp * Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f
                positionX = w/2f + logoW * Random.nextInt(-maxPositionR/3, maxPositionR).toFloat() / 100f
                positionY = -logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
            }
            // 2: top-right
            2 -> {
                speedX = dp * Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f
                speedY = h/w * speedX
                speedX = -speedX
                positionX = w + logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
                positionY = -logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
            }
            // 3: right
            3 -> {
                speedX = dp * (-Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f)
                speedY = 0f
                positionX = w + logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
                positionY = h/2f + logoW * Random.nextInt(-maxPositionR/2, maxPositionR).toFloat() / 100f
            }
            // 4: bot-right
            4 -> {
                speedX = dp * (-Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f)
                speedY = h/w * speedX
                positionX = w + logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
                positionY = h + logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
            }
            // 5: bot
            5 -> {
                speedX = 0f
                speedY = dp * (-Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f)
                positionX = w/2f + logoW * Random.nextInt(-maxPositionR/2, maxPositionR).toFloat() / 100f
                positionY = h + logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
            }
            // 6: bot-left
            6 -> {
                speedX = dp * Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f
                speedY = h/w * speedX
                speedY = -speedY
                positionX = -logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
                positionY = h + logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
            }
            // 7: left
            7 -> {
                speedX = dp * Random.nextInt(minSpeedR, maxSpeedR).toFloat() / 1000f
                speedY = 0f
                positionX = -logoW * Random.nextInt(minPositionR, maxPositionR).toFloat() / 100f
                positionY = h/2f + logoW * Random.nextInt(-maxPositionR, maxPositionR).toFloat() / 100f
            }
        }

        // random speed variation
        speedX += maxSpeedVariationX * Random.nextInt(-100, 100).toFloat() / 100f
        speedY += maxSpeedVariationY * Random.nextInt(-100, 100).toFloat() / 100f

        val speed = mutableListOf(speedX, speedY)
        val position = mutableListOf(positionX, positionY)
        val scale = 1f
        val logo = ResourcesCompat.getDrawable(
            resources,
            R.drawable.doppi_servizi,
            null
        )?.toBitmap(
            logoW.toInt(),
            logoH.toInt()
        )!!
        val logoDim = mutableListOf(logoW, logoH)
        var angle = Math.toDegrees(
            atan2(
                speed[1],
                speed[0]
            ).toDouble()
        ).toFloat()
        if (angle > 90f && angle < 180f) {
            angle += 180
        }
        else if (angle < -90f && angle > -180f) {
            angle -= 180
        }
        val scaleSpeed = 0.00015f * dp
        val bound = FloatArray(8)
        // top-left
        bound[1] = 0f
        bound[0] = 0f
        // top-right
        bound[3] = 0f
        bound[2] = logoDim[0]
        // bot-left
        bound[5] = logoDim[1]
        bound[4] = 0f
        // bot-right
        bound[7] = logoDim[1]
        bound[6] = logoDim[0]

        val path = Path()
        path.moveTo(bound[0], bound[1])
        path.moveTo(bound[2], bound[3])
        path.moveTo(bound[6], bound[7])
        path.moveTo(bound[4], bound[5])
        path.close()
        val entity = Entity(
            speed,
            position,
            scale,
            scaleSpeed,
            logo,
            logoDim,
            angle,
            bound,
            path,
            Matrix(),
            0
        )
        entities.add(entity)
    }

    private fun calculateTimeString(now: Long): String {
        val totalTime = (now - start)/1000
        val minutes = totalTime/60
        val seconds = totalTime%60
        val minuteString = if (minutes < 10)
            "0$minutes"
        else
            "$minutes"
        val secondsString = if (seconds < 10)
            "0$seconds"
        else
            "$seconds"
        return "$minuteString:$secondsString"
    }

    private fun setupGUI(canvas: Canvas, now: Long) {
        val rect = RectF(
            0f,
            0f,
            w,
            60f*dp
        )
        canvas.drawRect(rect, myPaint)

        val timeText = calculateTimeString(now)
        if ((now - start)/10000 >= upDiff) {
            spawnTime = (spawnTime * 0.9).toLong()
            upDiff++
        }
        val poppedText = "${resources.getString(R.string.click)}: $popped"
        val timePosition = drawText(
            canvas,
            timeText,
            "center",
            "top",
            w/2f,
            20f*dp,
            paint = textStatsPaint
        )
        drawText(
            canvas,
            poppedText,
            "right",
            "top",
            w - 35f*dp,
            timePosition[3],
            paint = textStatsPaint
        )
        if (clicksToNewHeart >= clicksToNewHeartAt) {
            hearts++
            clicksToNewHeart -= clicksToNewHeartAt
            newHeartTime = System.currentTimeMillis()
            mediaPlayerLifeUp.start()
        }
        canvas.drawBitmap(
            heart,
            30f*dp,
            timePosition[3] - 10*dp,
            null)
        val heartTextPosition = drawText(
            canvas,
            "x$hearts",
            "left",
            "top",
            70f * dp,
            timePosition[3],
            paint = textStatsPaint
        )
        if ((now - newHeartTime) < timeFade) {
            val alpha = (timeFade - (now - newHeartTime)) * 127 / timeFade
            drawText(
                canvas,
                "+1",
                "left",
                "top",
                10*dp + heartTextPosition[1],
                heartTextPosition[3],
                alpha.toInt()
            )
        }
    }

    private fun drawText(
        canvas: Canvas,
        text: String,
        offsetX: String,
        offsetY: String,
        positionX: Float = 10f*dp,
        positionY: Float = 10f*dp,
        alpha: Int = 255,
        paint: Paint = textPaint
    ):  MutableList<Float> {
        val textBound = Rect()
        textPaint.getTextBounds(
            text,
            0,
            text.length,
            textBound
        )
        var textX = 0f
        var textY = 0f
        when (offsetX) {
            "left" -> textX = positionX - textBound.left.toFloat()
            "right" -> textX = positionX - textBound.right.toFloat()
            "center" -> textX = positionX - textBound.exactCenterX()
        }
        when (offsetY) {
            "top" -> textY = positionY - textBound.top.toFloat()
            "bottom" -> textY = positionY - textBound.bottom.toFloat()
            "center" -> textY = positionY - textBound.exactCenterY()
        }

        textPaint.alpha = alpha

        canvas.drawText(
            text,
            textX,
            textY,
            paint
        )
        return mutableListOf(
            positionX - textBound.left.toFloat(),
            positionX + textBound.right.toFloat(),
            positionX - textBound.exactCenterX(),
            positionY - textBound.bottom.toFloat(),
            positionY - textBound.top.toFloat(),
            positionY - textBound.exactCenterY()
        )
//        return mutableListOf(textX, textY)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (firstLoop) {
            firstLoop = false
            initialize()
        }
        if (!end) {
            canvas.drawBitmap(
                background,
                0f,
                0f,
                null)

            val now = System.currentTimeMillis()

            if ((now - spawnCdr) > spawnTime) {
                createEntity(now)
                Log.i("entities", entities.size.toString())
            }

            for (entity in entities) {
                entity.m = Matrix()
                val r = Matrix()
                val s = Matrix()
                val t = Matrix()
                // m = r s m
                r.setRotate(entity.angle, entity.position[0], entity.position[1])
                s.postScale(
                    entity.scale,
                    entity.scale,
                    entity.position[0] + entity.logoDim[0]/2f,
                    entity.position[1] + entity.logoDim[1]/2f
                )
                t.setTranslate(entity.position[0], entity.position[1])
                entity.m.postConcat(s)
                entity.m.postConcat(r)
                entity.m.preConcat(t)

                val topLeft = floatArrayOf(entity.bound[0], entity.bound[1])
                val topRight = floatArrayOf(entity.bound[2], entity.bound[3])
                val botLeft = floatArrayOf(entity.bound[4], entity.bound[5])
                val botRight = floatArrayOf(entity.bound[6], entity.bound[7])
                entity.m.mapPoints(topLeft)
                entity.m.mapPoints(topRight)
                entity.m.mapPoints(botLeft)
                entity.m.mapPoints(botRight)

                val path = Path()
                path.moveTo(topLeft[0], topLeft[1])
                path.lineTo(topRight[0], topRight[1])
                path.lineTo(botRight[0], botRight[1])
                path.lineTo(botLeft[0], botLeft[1])
                path.close()
                entity.pathBound.set(path)
                if (entity.logo != null) {
                    canvas.withMatrix(entity.m) {
                        drawBitmap(entity.logo, 0f, 0f, null)
                    }
                }
                entity.position[0] += entity.speed[0] * frameRate
                entity.position[1] += entity.speed[1] * frameRate
                entity.scaleSpeed = if (entity.scale < scaleMin || entity.scale > scaleMax)
                    -entity.scaleSpeed
                else
                    entity.scaleSpeed
                entity.scale += entity.scaleSpeed * frameRate
            }
            setupGUI(canvas, now)

            // remove item by click
            if (entityToDelete.size > 1) {
                val bonusToAdd = Bonus(
                    mutableListOf(clickPosition[0], clickPosition[1]),
                    "x${entityToDelete.size}",
                    System.currentTimeMillis()
                )
                bonuses.add(bonusToAdd)
            }
            val bonusToDelete = mutableListOf<Bonus>()
            for (bonus in bonuses) {
                if ((now - bonus.startingTime) > timeFade) {
                    bonusToDelete.add(bonus)
                } else {
                    val alpha = (timeFade - (now - bonus.startingTime)) * 130 / timeFade
                    drawText(
                        canvas,
                        bonus.text,
                        "center",
                        "bottom",
                        bonus.position[0],
                        bonus.position[1],
                        alpha.toInt()
                    )
                }
            }
            for (bonus in bonusToDelete) {
                bonuses.remove(bonus)
            }
            for (entity in entityToDelete) {
                entities.remove(entity)
            }
            entityToDelete = mutableListOf()
            for (entity in entities) {
                val topLeft = floatArrayOf(entity.bound[0], entity.bound[1])
                val topRight = floatArrayOf(entity.bound[2], entity.bound[3])
                val botLeft = floatArrayOf(entity.bound[4], entity.bound[5])
                val botRight = floatArrayOf(entity.bound[6], entity.bound[7])
                entity.m.mapPoints(topLeft)
                entity.m.mapPoints(topRight)
                entity.m.mapPoints(botLeft)
                entity.m.mapPoints(botRight)
                val boundPoints = mutableListOf(topLeft, topRight, botLeft, botRight)
                var inside = false
                for (i in 0 until boundPoints.size) {
                    if (boundPoints[i][0] >= -10*dp &&
                        boundPoints[i][0] <= width + 10*dp &&
                        boundPoints[i][1] >= -10*dp &&
                        boundPoints[i][1] <= height + 10*dp
                    ) {
                        inside = true
                        break
                    }
                }
                if (inside) {
                    entity.state = 1
                } else if (entity.state == 1) {
                    entity.state = 2
                }
                if (entity.state == 2) {
                    entityToDelete.add(entity)
                }
            }
            for (entity in entityToDelete) {
                hearts--
                mediaPlayerHit.start()
                entities.remove(entity)
                if (hearts < 0) {
                    end = true
                    val bundle = Bundle()
                    bundle.putInt("popped", popped)
                    bundle.putString("time", calculateTimeString(now))
                    findNavController().navigate(R.id.action_playFragment_to_endGameFragment, bundle)
                }
            }
            if (!pause)
                postInvalidateDelayed(frameRate)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (clickUp) {
                    clickUp = false
                    clickPosition = mutableListOf(event.x, event.y)
                    entityToDelete = mutableListOf()
                    var counter = 0
                    var playSound = false
                    for (entity in entities) {
                        val topLeft = floatArrayOf(entity.bound[0], entity.bound[1])
                        val topRight = floatArrayOf(entity.bound[2], entity.bound[3])
                        val botLeft = floatArrayOf(entity.bound[4], entity.bound[5])
                        val botRight = floatArrayOf(entity.bound[6], entity.bound[7])
                        entity.m.mapPoints(topLeft)
                        entity.m.mapPoints(topRight)
                        entity.m.mapPoints(botLeft)
                        entity.m.mapPoints(botRight)
                        val am = vec2D(mutableListOf(topLeft[0], topLeft[1]), mutableListOf(event.x, event.y))
                        val ab = vec2D(mutableListOf(topLeft[0], topLeft[1]), mutableListOf(topRight[0], topRight[1]))
                        val ad = vec2D(mutableListOf(topLeft[0], topLeft[1]), mutableListOf(botLeft[0], botLeft[1]))
                        if (
                            dot(am, ab) > 0 && dot(am, ab) < dot(ab, ab) &&
                            dot(am, ad) > 0 && dot(am, ad) < dot(ad, ad)
                        ) {
                            entityToDelete.add(entity)
                            counter++
                            playSound = true
                        }
                    }
                    if (playSound) {
                        mediaPlayerClick.start()
                    }
                    popped += counter*counter
                    clicksToNewHeart += counter*counter
                }
            }
            MotionEvent.ACTION_UP -> {
                clickUp = true
            }
        }
        return true
    }

    private fun dot(a: MutableList<Float>, b: MutableList<Float>): Float {
        var result = 0f
        for (i in 0 until a.size) {
            result += a[i]*b[i]
        }
        return result
    }

    private fun vec2D(src: MutableList<Float>, dst: MutableList<Float>): MutableList<Float> {
        return mutableListOf(dst[0] - src[0], dst[1] - src[1])
    }
}