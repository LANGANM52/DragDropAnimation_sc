package edu.farmingdale.alrajab.dragdropanimation_sc

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import edu.farmingdale.alrajab.dragdropanimation_sc.databinding.ActivityDragAndDropViewsBinding

class DragAndDropViews : AppCompatActivity() {
    lateinit var binding: ActivityDragAndDropViewsBinding
    private lateinit var draggedArrow: Button
    private lateinit var rocketAnimation: AnimationDrawable
    private lateinit var startStopButton: Button
    private lateinit var rocketImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragAndDropViewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rocketImageView = findViewById(R.id.rocketImageView)
        startRocketAnimation()

        val rocketImageView = findViewById<ImageView>(R.id.rocketImageView)
        rocketAnimation = rocketImageView.drawable as AnimationDrawable

        startStopButton = findViewById(R.id.startStopButton)
        startStopButton.setOnClickListener {
            if (rocketAnimation.isRunning) {
                rocketAnimation.stop()
                startStopButton.text = "Start Animation"
            } else {
                rocketAnimation.start()
                startStopButton.text = "Stop Animation"
            }
        }

        // Long click listeners for all arrow buttons
        binding.upMoveBtn.setOnLongClickListener(onLongClickListener)
        binding.downMoveBtn.setOnLongClickListener(onLongClickListener)
        binding.forwardMoveBtn.setOnLongClickListener(onLongClickListener)
        binding.backMoveBtn.setOnLongClickListener(onLongClickListener)

        // Drag listeners for all placeholders
        binding.holder01.setOnDragListener(arrowDragListener)
        binding.holder02.setOnDragListener(arrowDragListener)
        binding.holder03.setOnDragListener(arrowDragListener)
        binding.holder04.setOnDragListener(arrowDragListener)
        binding.holder05.setOnDragListener(arrowDragListener)

    }

    private val onLongClickListener = View.OnLongClickListener { view: View ->
        (view as? Button)?.let {
            val item = ClipData.Item(it.tag as? CharSequence)
            val dragData = ClipData(
                it.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item
            )
            val myShadow = ArrowDragShadowBuilder(it)

            draggedArrow = it // Keep track of the dragged arrow

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(dragData, myShadow, null, 0)
            } else {
                it.startDrag(dragData, myShadow, null, 0)
            }

            true
        } ?: false
    }

    private val arrowDragListener = View.OnDragListener { view, dragEvent ->
        (view as? ImageView)?.let { placeholder ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    return@OnDragListener true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    placeholder.setBackgroundResource(R.drawable.placeholder_highlight) // Change placeholder on drag enter
                    return@OnDragListener true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    placeholder.setBackgroundResource(R.drawable.placeholder_border) // Restore placeholder on drag exit
                    return@OnDragListener true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = dragEvent.clipData.getItemAt(0)
                    val arrowTag = item.text.toString()
                    handleArrowDrop(arrowTag, placeholder)
                    placeholder.setBackgroundResource(R.drawable.placeholder_border) // Restore placeholder on drop
                    return@OnDragListener true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    placeholder.setBackgroundResource(R.drawable.placeholder_border) // Restore placeholder on drag end
                    return@OnDragListener true
                }
                else -> return@OnDragListener false
            }
        } ?: false
    }


    private fun handleArrowDrop(arrowTag: String, placeholder: ImageView) {
        when (arrowTag) {
            "UP" -> placeholder.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
            "DOWN" -> placeholder.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
            "FORWARD" -> placeholder.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
            "BACK" -> placeholder.setImageResource(R.drawable.ic_baseline_arrow_back_24)
            else -> {
                // For arrows without matching actions
                Log.d("DragAndDrop", "Unknown arrow type: $arrowTag")
            }
        }
    }


    private class ArrowDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {
        private val shadow = view.background

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = view.width
            val height: Int = view.height
            shadow?.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            shadow?.draw(canvas)
        }
    }
    private fun startRocketAnimation() {
        // Moves rocket from left to right
        val translationX = ObjectAnimator.ofFloat(
            rocketImageView,
            "translationX",
            0f,
            500f // Distance value
        )
        translationX.duration = 3000 // Duration in milliseconds

        // Rotation animation
        val rotation = ObjectAnimator.ofFloat(
            rocketImageView,
            "rotation",
            0f,
            360f // Rotations value
        )
        rotation.duration = 3000 // Duration in milliseconds

        // AnimatorSet to play both animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationX, rotation)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        // Start the combined animation
        animatorSet.start()
    }
}