package com.raywenderlich.whacardroid

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Light
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.raywenderlich.whacardroid.Configuration.Companion.COL_NUM
import com.raywenderlich.whacardroid.Configuration.Companion.MAX_MOVE_DELAY_MS
import com.raywenderlich.whacardroid.Configuration.Companion.MAX_PULL_DOWN_DELAY_MS
import com.raywenderlich.whacardroid.Configuration.Companion.MIN_MOVE_DELAY_MS
import com.raywenderlich.whacardroid.Configuration.Companion.MIN_PULL_DOWN_DELAY_MS
import com.raywenderlich.whacardroid.Configuration.Companion.MOVES_PER_TIME
import com.raywenderlich.whacardroid.Configuration.Companion.ROW_NUM
import com.raywenderlich.whacardroid.Configuration.Companion.START_LIVES

class MainActivity : AppCompatActivity() {

  private lateinit var arFragment: ArFragment

  private lateinit var scoreboard: ScoreboardView

  private var droidRenderable: ModelRenderable? = null
  private var heartRenderable: ModelRenderable? = null

  private var scoreboardRenderable: ViewRenderable? = null

  private var failLight: Light? = null

  private var lastRowOffset : TranslatableNode? = null

  private var grid = Array(ROW_NUM) { arrayOfNulls<TranslatableNode>(COL_NUM) }
  private var heartNode: TranslatableNode? = null
  private var initialized = false
  private var heartClicked = false

  private val pullUpRunnable: Runnable by lazy {
    Runnable {
      // 1
      if (scoreboard.life > 0) {
        grid.flatMap { it.toList() }
                .filter { it?.position == DroidPosition.DOWN }
                .run { takeIf { size > 0 }?.getOrNull((0..size).random()) }
                ?.apply {
                  // 2
                  pullUp()
                  // 3
                  val pullDownDelay = (MIN_PULL_DOWN_DELAY_MS..MAX_PULL_DOWN_DELAY_MS).random()
                  gameHandler.postDelayed({ pullDown() }, pullDownDelay)
                }
        heartNode?.apply {
          pullUp(2F, true)
        }


        // 4
        // Delay between this move and the next one
        val nextMoveDelay = (MIN_MOVE_DELAY_MS..MAX_MOVE_DELAY_MS).random()
        gameHandler.postDelayed(pullUpRunnable, nextMoveDelay)


      }
    }
  }

  private var gameHandler = Handler()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

    initResources()

    arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, _: MotionEvent ->
      if (initialized) {
        // 1
        // Already initialized!
        // When the game is initialized and user touches without
        // hitting a droid, remove 50 points
        failHit()
        return@setOnTapArPlaneListener
      }

      if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
        // 2
        // Only HORIZONTAL_UPWARD_FACING planes are good to play the game
        // Notify the user and return
        "Find an HORIZONTAL and UPWARD FACING plane!".toast(this)
        return@setOnTapArPlaneListener
      }

      if(droidRenderable == null || scoreboardRenderable == null || failLight == null){
        // 3
        // Every renderable object must be initialized
        // On a real world/complex application
        // it can be useful to add a visual loading
        return@setOnTapArPlaneListener
      }



      val spacing = 0.3F

      val anchorNode = AnchorNode(hitResult.createAnchor())

      anchorNode.setParent(arFragment.arSceneView.scene)

//       4
//       Add N droid to the plane (N = COL x ROW)
      grid.matrixIndices { col, row ->
        val renderableModel = droidRenderable?.makeCopy() ?: return@matrixIndices
        TranslatableNode().apply {
          setParent(anchorNode)
            renderable = renderableModel
          addOffset(x = row * spacing, z = col * spacing)
          grid[col][row] = this
          this.setOnTapListener { _, _ ->
            // TODO: You hit a droid!
            if (this.position != DroidPosition.DOWN) {
              // Droid hit! assign 10 points
              scoreboard.score += 10
              this.pullDown()
            } else {
              // When player hits a droid that is not up
              // it's like a "miss", so remove 50 points
              failHit()
            }
          }
        }
      }

      heartNode = TranslatableNode().apply {
        renderable = heartRenderable
        setParent(anchorNode)
        localScale = Vector3(0.07f, 0.07f, 0.07f)
        addOffset(x = 1 * spacing, z = 3 * spacing )

      }

      heartNode?.setOnTapListener { _, _ ->
        scoreboard.life += 1
        heartClicked = true
      }





      // 5
      // Add the scoreboard view to the plane
      val renderableView = scoreboardRenderable ?: return@setOnTapArPlaneListener
      TranslatableNode()
              .also {
                it.setParent(anchorNode)
                it.renderable = renderableView
                it.addOffset(x = spacing, y = .6F)
              }

      // 6
      // Add a light
      Node().apply {
        setParent(anchorNode)
        light = failLight
        localPosition = Vector3(.3F, .3F, .3F)
      }

      // 7
      initialized = true
    }
  }

  private fun initResources() {

    failLight = Light.builder(Light.Type.POINT)
            .setColor(com.google.ar.sceneform.rendering.Color(Color.RED))
            .setShadowCastingEnabled(true)
            .setIntensity(0F)
            .build()

    scoreboard = ScoreboardView(this)
    scoreboard.onStartTapped =  {
      scoreboard.life = START_LIVES
      scoreboard.score = 0

      gameHandler.post {
        repeat(MOVES_PER_TIME) {
          gameHandler.post(pullUpRunnable)
        }
      }
    }

    ViewRenderable.builder()
            .setView(this, scoreboard)
            .build()
            .thenAccept {
              it.isShadowReceiver = true
              scoreboardRenderable = it
            }
            .exceptionally { it.toast(this) }

    ModelRenderable.builder()
            .setSource(this, R.raw.andy)
            .build()
            .thenAccept { droidRenderable = it }
            .exceptionally {it.toast(this)}

    ModelRenderable.builder()
            .setSource(this, R.raw.heart)
            .build()
            .thenAccept { heartRenderable = it }
            .exceptionally { it.toast(this) }


  }

  private fun failHit() {
    scoreboard.score -= 5
    scoreboard.life -= 1
    failLight?.blink()
    if (scoreboard.life <= 0) {
      // Game over
      gameHandler.removeCallbacksAndMessages(null)
      grid.flatMap { it.toList() }
              .filterNotNull()
              .filter { it.position != DroidPosition.DOWN && it.position != DroidPosition.MOVING_DOWN }
              .forEach { it.pullDown() }
    }
  }
}