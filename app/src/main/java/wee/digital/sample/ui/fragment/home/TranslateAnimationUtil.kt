package wee.digital.sample.ui.fragment.home
import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import wee.digital.sample.R

class TranslateAnimationUtil(context: Context, viewAnimation: View) :
    OnTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, SimpleGestureDetector(viewAnimation))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    class SimpleGestureDetector(private val viewAnimation: View?) :
        SimpleOnGestureListener() {
        private var isFinshAnimation = true
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (distanceX > 0) {
                hiddenView()
            } else {
                showView()
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        private fun hiddenView() {
            if (viewAnimation == null || viewAnimation.visibility == View.GONE) {
                return
            }
            val animationDown = AnimationUtils.loadAnimation(
                viewAnimation.context, R.anim.move_down
            )
            animationDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    viewAnimation.visibility = View.VISIBLE
                    isFinshAnimation = false
                }

                override fun onAnimationEnd(animation: Animation) {
                    viewAnimation.visibility = View.GONE
                    isFinshAnimation = true
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            if (isFinshAnimation) {
                viewAnimation.startAnimation(animationDown)
            }
        }

        private fun showView() {
            if (viewAnimation == null || viewAnimation.visibility == View.VISIBLE) {
                return
            }
            val animationUp = AnimationUtils.loadAnimation(
                viewAnimation.context, R.anim.move_up
            )
            animationUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    viewAnimation.visibility = View.VISIBLE
                    isFinshAnimation = false
                }

                override fun onAnimationEnd(animation: Animation) {
                    isFinshAnimation = true
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            if (isFinshAnimation) {
                viewAnimation.startAnimation(animationUp)
            }
        }
    }


}