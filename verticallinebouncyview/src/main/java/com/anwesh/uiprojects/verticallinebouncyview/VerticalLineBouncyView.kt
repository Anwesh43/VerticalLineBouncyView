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
