package com.raywenderlich.whacardroid

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3

/**
 * Created by admin on 12/1/2018.
 */
class TranslatableNode : Node() {

    var position: DroidPosition = DroidPosition.DOWN

    // 2
    fun pullUp(offset: Float = 0.4F, heart: Boolean = false) {
        // If not moving up or already moved up, start animation
        if (position != DroidPosition.MOVING_UP && position != DroidPosition.UP) {
            animatePullUp(offset, heart)
        }
    }

    fun pullDown() {
        if(position != DroidPosition.MOVING_DOWN && position != DroidPosition.DOWN) {
            animmatePullDown()
        }
    }

    private fun animmatePullDown() {
        val low = Vector3(localPosition).apply { y = 0F }
        val high = Vector3(localPosition)

        val animation = localPositionAnimator(high, low)

        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                position = DroidPosition.DOWN
            }

            override fun onAnimationStart(animation: Animator?) {
                position = DroidPosition.MOVING_DOWN
            }

        })
        animation.start()
    }

    private fun localPositionAnimator(vararg values: Any?): ObjectAnimator {
        return ObjectAnimator().apply {
            target = this@TranslatableNode
            propertyName = "localPosition"
            duration = 250
            interpolator = LinearInterpolator()

            setAutoCancel(true)
            // * = Spread operator, this will pass N `Any?` values instead of a single list `List<Any?>`
            setObjectValues(*values)
            // Always apply evaluator AFTER object values or it will be overwritten by a default one
            setEvaluator(VectorEvaluator())
        }
    }
    private fun localPositionAnimatorForHeart(vararg values: Any?): ObjectAnimator {
        return ObjectAnimator().apply {
            target = this@TranslatableNode
            propertyName = "localPosition"
            duration = 1000
            interpolator = LinearInterpolator()

            setAutoCancel(true)
            // * = Spread operator, this will pass N `Any?` values instead of a single list `List<Any?>`
            setObjectValues(*values)
            // Always apply evaluator AFTER object values or it will be overwritten by a default one
            setEvaluator(VectorEvaluator())
        }
    }

    private fun animatePullUp(offset: Float = 0.4F, heart: Boolean) {
        // No matter where you start (i.e. start from .3 instead of 0F),
        // you will always arrive at .4F
        val low = Vector3(localPosition)
        val high = Vector3(localPosition).apply { y = offset}

        val animation = if (heart) localPositionAnimatorForHeart(low, high)
        else localPositionAnimator(low, high)

        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                position = DroidPosition.UP
            }

            override fun onAnimationStart(animation: Animator?) {
                position = DroidPosition.MOVING_UP
            }

        })
        animation.start()
    }

    fun addOffset(x: Float = 0F, y: Float = 0F, z: Float = 0F) {
        val posX = localPosition.x + x
        val posY = localPosition.y + y
        val posZ = localPosition.z + z

        localPosition = Vector3(posX, posY, posZ)
    }

}