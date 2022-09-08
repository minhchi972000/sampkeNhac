package wee.digital.widget.base

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import wee.digital.widget.R

open class AppConstraintLayout : ConstraintLayout {

    private var path: Path? = null

    /** corner radius */
    var radiusTopLeft: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var radiusTopRight: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var radiusBottomLeft: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var radiusBottomRight: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var backgroundColor: Int? = null
        set(@ColorInt value) {
            field = value
            postInvalidate()
        }

    var strokeLineWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    @ColorInt
    var strokeLineColor = 0XFFFFFFFF.toInt()
        set(@ColorInt value) {
            field = value
            postInvalidate()
        }

    var strokeLineColorRes: Int = 0
        set(@ColorRes value) {
            strokeLineColor = ContextCompat.getColor(context, value)
        }

    var dashLineGap: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var dashLineWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        render(attrs)
    }

    private fun render(attrs: AttributeSet?) {
        attrs ?: return
        val a = context.obtainStyledAttributes(attrs, R.styleable.AppCustomView)
        val radius = a.pixels(R.styleable.AppCustomView_radius)

        val attrTopLeftRadius = a.pixels(R.styleable.AppCustomView_radiusTopLeft)
        radiusTopLeft = if (attrTopLeftRadius > 0F) attrTopLeftRadius else radius

        val attrTopRightRadius = a.pixels(R.styleable.AppCustomView_radiusTopRight)
        radiusTopRight = if (attrTopLeftRadius > 0F) attrTopRightRadius else radius

        val attrBottomLeftRadius = a.pixels(R.styleable.AppCustomView_radiusBottomLeft)
        radiusBottomLeft = if (attrTopLeftRadius > 0F) attrBottomLeftRadius else radius

        val attrBottomRightRadius = a.pixels(R.styleable.AppCustomView_radiusBottomRight)
        radiusBottomRight = if (attrTopLeftRadius > 0F) attrBottomRightRadius else radius

        backgroundColor = a.getColor(R.styleable.AppCustomView_backgroundColor, Color.WHITE)

        strokeLineWidth = a.pixels(R.styleable.AppCustomView_strokeLineWidth)
        strokeLineColor = a.getColor(R.styleable.AppCustomView_strokeLineColor, Color.BLACK)

        dashLineWidth = a.pixels(R.styleable.AppCustomView_dashLineWidth)
        dashLineGap = a.pixels(R.styleable.AppCustomView_dashLineGap)

        a.recycle()
    }

    fun TypedArray.pixels(@StyleableRes id: Int): Float {
        return getDimensionPixelSize(id, 0).toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        /** for outline remake when ever draw */
        path = Path()

        clipPathCanvas(
            canvas, floatArrayOf(
                radiusTopLeft, radiusTopLeft, radiusTopRight, radiusTopRight, radiusBottomRight,
                radiusBottomRight, radiusBottomLeft, radiusBottomLeft
            )
        )

        /** set drawable resource corner & background & stroke */
        GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                radiusTopLeft, radiusTopLeft, radiusTopRight, radiusTopRight,
                radiusBottomRight, radiusBottomRight, radiusBottomLeft, radiusBottomLeft
            )
            if (strokeLineWidth != 0F) {
                this.setStroke(strokeLineWidth.toInt(), strokeLineColor, dashLineWidth, dashLineGap)
            }
            backgroundColor?.let {
                setColor(it)
            } ?: setColor(Color.WHITE)

            background = this
        }

        outlineProvider = outlineProvider

        clipChildren = false

        super.dispatchDraw(canvas)
    }

    private fun clipPathCanvas(canvas: Canvas, floatArray: FloatArray) {
        path?.let {
            it.addRoundRect(
                RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()),
                floatArray,
                Path.Direction.CW
            )
            canvas.clipPath(it)
        }
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
    }

    /** For not showing red underline */
    override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        super.setOutlineProvider(provider)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                path?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        outline.setPath(it)
                    } else {
                        @Suppress("DEPRECATION")
                        outline.setConvexPath(it)
                    }
                } ?: throw Exception()
            }
        }
    }
}