package com.raywenderlich.whacardroid

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.google.ar.sceneform.rendering.Light
import java.util.concurrent.ThreadLocalRandom
import android.animation.ValueAnimator
import android.animation.ObjectAnimator

fun Any?.toast(ctx: Context): Void? {
  val msg = if (this is Throwable) {
    this.printStackTrace()
    this.localizedMessage
  } else {
    this.toString()
  }

  Toast.makeText(ctx, msg, Toast.LENGTH_LONG)
      .apply { setGravity(Gravity.CENTER, 0, 0) }.show()
  return null
}

fun <T> ClosedRange<T>.random(): T where T : Number, T : Comparable<T> =
    ThreadLocalRandom.current()
        .nextLong(endInclusive.toLong() - start.toLong())
        .plus(start.toLong()) as T

fun <T> Array<Array<T>>.matrixIndices(f: (Int, Int) -> Unit) {
  this.forEachIndexed { col, array ->
    array.forEachIndexed { row, _ ->
      f(col, row)
    }
  }
}



fun Light.blink(times: Int = 1, from: Float = 0F, to: Float = 100000F, inMs: Long = 100) {
  val intensityAnimator = ObjectAnimator.ofFloat(this, "intensity", from, to)
  intensityAnimator.duration = inMs
  intensityAnimator.repeatCount = times * 2 - 1
  intensityAnimator.repeatMode = ValueAnimator.REVERSE
  intensityAnimator.start()
}