package wee.digital.widget.custom

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.widget.R
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.databinding.KeyboardBinding

/**
 * -------------------------------------------------------------------------------------------------
 *  In fragment:
 *  not working with keyboard had shown or view.requestFocus() before
 *  keyboardView.observer(fragment).requestFocus(view)
 *
 *  In Manifest:
 *  <activity
 *      android:name="~.MainActivity"
 *      android:screenOrientation="portrait"
 *      android:fitsSystemWindows="false"
 *      android:theme="@style/App.Theme"
 *      android:windowSoftInputMode="adjustNothing"/>
 *
 * In layout xml:
 * <wee.digital.widget.custom.KeyboardView
 *       android:id="@+id/keyboardView"
 *       android:layout_width="match_parent"
 *       android:layout_height="wrap_content"
 *       app:layout_constraintBottom_toBottomOf="parent"
 *       app:layout_constraintStart_toStartOf="parent" />
 * -------------------------------------------------------------------------------------------------
 */
class KeyboardView : AppCustomView<KeyboardBinding> {

    var keyboardIsShown: Boolean = false

    var onKeyboardShow: (() -> Unit)? = null

    var onKeyboardHide: (() -> Unit)? = null

    var actionFocus: (() -> Unit)? = null

    private val screenOrientation: Int get() = context.resources.configuration.orientation

    private val popupView: View get() = popupWindow.contentView

    private lateinit var popupWindow: PopupWindow

    private lateinit var parentView: View

    private var onKeyboardVisibilityChanged: (() -> Unit)? = null

    private val imm: InputMethodManager get() = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private val imageView: ImageView get() = vb.imageViewKeyboard

    private var alwaysVisible: Boolean = false

    private val Activity.screenHeight: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.height() - insets.top - insets.bottom
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                this.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.heightPixels
            }
        }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding {
        return KeyboardBinding::inflate
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        if (context is AppCompatActivity) {
            context.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            imageView.setBackgroundColor(Color.TRANSPARENT)
            imageView.setImageResource(0)
        } else {
            imageView.scaleType = ImageView.ScaleType.FIT_END
            val srcRes = types.srcRes
            imageView.setImageResource(if (srcRes != 0) srcRes else R.mipmap.keyboard_num)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (context is AppCompatActivity && savedKeyboardHeight > 0) {
            updateViewHeight(savedKeyboardHeight)
        }
    }

    private fun initPopup(activity: AppCompatActivity) {
        parentView = activity.findViewById(android.R.id.content)
        val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupWindow = PopupWindow().also {
            it.contentView = inflater.inflate(R.layout.keyboard_popup, null, false)
            @Suppress("DEPRECATION")
            it.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            it.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            it.width = 0
            it.height = WindowManager.LayoutParams.MATCH_PARENT
        }
        popupWindow.contentView.viewTreeObserver.addOnGlobalLayoutListener {
            handleOnGlobalLayout(activity)
        }
    }

    private fun handleOnGlobalLayout(activity: AppCompatActivity) {

        val keyboardHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val keyboardUpdateCheck = rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true
            if (keyboardUpdateCheck != keyboardIsShown) {
                keyboardIsShown = keyboardUpdateCheck
            }
            val rect = activity.windowManager.currentWindowMetrics.bounds
            popupView.getWindowVisibleDisplayFrame(rect)
            activity.screenHeight - rect.bottom
        } else {
            val screenSize = Point()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getSize(screenSize)
            val rect = Rect()
            popupView.getWindowVisibleDisplayFrame(rect)
            val height = screenSize.y - rect.bottom
            keyboardIsShown = height > 0
            height
        }
        val orientation = screenOrientation
        //Log.d("KeyboardView", keyboardHeight.toString())
        when {
            keyboardHeight <= 0 -> {
                keyboardHasHide()
            }
            orientation == Configuration.ORIENTATION_PORTRAIT -> {
                keyboardHasShow(keyboardHeight)
            }
            else -> {
                keyboardHasShow(keyboardHeight)
            }
        }
    }

    private fun keyboardHasHide() {
        if (!alwaysVisible) {
            updateViewHeight(0)
        }
        onKeyboardVisibilityChanged?.invoke()
        onKeyboardVisibilityChanged = null
        onKeyboardHide?.invoke()
    }

    private fun keyboardHasShow(keyboardHeight: Int) {
        if (keyboardHeight <= 0) return
        if (keyboardHeight == savedKeyboardHeight) return
        savedKeyboardHeight = keyboardHeight
        updateViewHeight(keyboardHeight)
        onKeyboardVisibilityChanged?.invoke()
        onKeyboardVisibilityChanged = null
        onKeyboardShow?.invoke()
    }

    private fun updateViewHeight(keyboardHeight: Int) {
        val lp = this.layoutParams
        lp.height = keyboardHeight
        this.layoutParams = lp
    }

    private fun observer(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun oResume() {
                doOnLayout {
                    keyboardIsShown = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true
                    } else {
                        false
                    }
                }
                lifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    start()
                    actionFocus?.invoke()
                    actionFocus = null
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                close()
            }
        })
    }

    /**
     * Start the KeyboardProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    fun observer(fragment: Fragment): KeyboardView {
        initPopup(fragment.requireActivity() as AppCompatActivity)
        observer(lifecycleOwner = fragment)
        return this
    }

    fun observer(activity: AppCompatActivity): KeyboardView {
        initPopup(activity)
        observer(lifecycleOwner = activity)
        return this
    }

    fun requestFocus(v: View?): KeyboardView {
        actionFocus = {
            v?.requestFocus()
        }
        return this
    }

    fun alwaysVisible(isAlwaysVisible: Boolean = true): KeyboardView {
        alwaysVisible = isAlwaysVisible
        return this
    }

    fun start() {
        if (!popupWindow.isShowing && parentView.windowToken != null) {
            popupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    fun close() {
        onKeyboardShow = null
        onKeyboardHide = null
        popupWindow.dismiss()
    }

    fun onKeyboardShow(block: (() -> Unit)? = null): KeyboardView {
        onKeyboardShow = block
        return this
    }

    fun onKeyboardHide(block: (() -> Unit)? = null): KeyboardView {
        onKeyboardHide = block
        return this
    }

    fun hideKeyboard() {
        popupView.post {
            clearFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun hideKeyboard(onCompleted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hideKeyboard()
            onCompleted.invoke()
            return
        }
        if (!keyboardIsShown) {
            hideKeyboard()
            onCompleted.invoke()
            return
        }
        post {
            onKeyboardVisibilityChanged = onCompleted
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun showKeyboard() {
        popupView.post {
            requestFocus()
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun showKeyboard(onCompleted: (() -> Unit)? = null) {
        if (keyboardIsShown) {
            onCompleted?.invoke()
            return
        }
        post {
            onKeyboardVisibilityChanged = onCompleted
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    companion object {
        var savedKeyboardHeight: Int = 0
    }
}