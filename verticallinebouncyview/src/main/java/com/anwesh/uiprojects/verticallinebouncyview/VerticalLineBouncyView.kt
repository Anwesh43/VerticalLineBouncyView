package com.anwesh.uiprojects.verticallinebouncyview

/**
 * Created by anweshmishra on 21/11/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.content.Context

val nodes : Int = 5
val lines : Int = 5
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val scGap : Float = 0.02f
val delay : Long = 30
val lineColor : Int = Color.parseColor("#4CAF50")
val rectColor : Int = Color.parseColor("#1565C0")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Float.cosify() : Float = 1f - Math.cos(Math.PI / 2 + (Math.PI / 2) * this).toFloat()

fun Canvas.drawVerticalLineBouncy(scale : Float, size : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sc : Float = scale.divideScale(1, 2).cosify()
    for (j in 0..1) {
        save()
        scale(1f, 1f - 2 * j)
        paint.color = lineColor
        drawLine(0f, h / 2, 0f, h / 2 - (h / 2) * sf, paint)
        paint.color = rectColor
        drawRect(RectF(-size, 0f, size, (h / 2) * sc), paint)
        restore()
    }
}

fun Canvas.drawVLBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawVerticalLineBouncy(scale, size, h, paint)
    restore()
}

class VerticalLineBouncyView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class VLBNode(var i : Int, val state : State = State()) {

        private var next : VLBNode? = null
        private var prev : VLBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = VLBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawVLBNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : VLBNode {
            var curr : VLBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class VerticalBouncyLine(var i : Int) {

        private var curr : VLBNode = VLBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : VerticalLineBouncyView) {

        private val animator : Animator = Animator(view)
        private val vbl : VerticalBouncyLine = VerticalBouncyLine(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            vbl.draw(canvas, paint)
            animator.animate {
                vbl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            vbl.startUpdating {
                animator.start()
            }
        }
    }
}