package com.raywenderlich.whacardroid

import android.animation.TypeEvaluator
import com.google.ar.sceneform.math.Vector3

class VectorEvaluator : TypeEvaluator<Vector3> {

  override fun evaluate(fraction: Float, startValue: Vector3, endValue: Vector3): Vector3? {
    val startX = startValue.x
    val x = startX + fraction * (endValue.x - startX)

    val startY = startValue.y
    val y = startY + fraction * (endValue.y - startY)

    val startZ = startValue.z
    val z = startZ + fraction * (endValue.z - startZ)

    return Vector3(x, y, z)
  }
}