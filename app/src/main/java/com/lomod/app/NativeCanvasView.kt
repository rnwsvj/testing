package com.lomod.app

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class NativeCanvasView(context: Context, private val prefs: SharedPreferences) :
    SurfaceView(context), Runnable {

    private var gameThread: Thread? = null
    private var isPlaying = false
    private val surfaceHolder: SurfaceHolder = holder

    private val playerPaint = Paint().apply {
        color = Color.parseColor("#6366f1")
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        isAntiAlias = true
    }
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#090a16")
    }

    private var playerX = 200f
    private var playerY = 600f
    private var score = 0
    private var highScore = prefs.getInt("high_score", 0)

    fun resume() {
        isPlaying = true
        gameThread = Thread(this).also { it.start() }
    }

    fun pause() {
        isPlaying = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        while (isPlaying) {
            update()
            draw()
            try {
                Thread.sleep(16) // ~60 FPS
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun update() {
        // Native update loop
        score++
        if (score > highScore) {
            highScore = score
            prefs.edit().putInt("high_score", highScore).apply()
        }
    }

    private fun draw() {
        if (!surfaceHolder.surface.isValid) return

        val canvas: Canvas = surfaceHolder.lockCanvas()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw Player
        canvas.drawCircle(playerX, playerY, 36f, playerPaint)

        // Draw HUD
        canvas.drawText("LOMOD", 40f, 80f, textPaint)
        canvas.drawText("Score: $score  High: $highScore", 40f, 150f, textPaint)

        surfaceHolder.unlockCanvasAndPost(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                playerX = event.x
                playerY = event.y
            }
        }
        return true
    }
}
