@file:JvmName("ViewUtils")

package com.transo.ken42.driver.extension

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mvvm.R
import com.mvvm.domain.extention.toCamelCase
import com.mvvm.extension.resColor
import com.mvvm.extension.resColorStateList
import com.mvvm.extension.resDrawable
import kotlinx.coroutines.*
import java.util.*


/*------------------------------------View---------------------------------------------*/

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

/**
 * Toggle a view's visibility
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

/**
 * Extension method to show a keyboard for View.
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

/**
 * Extension method to show a keyboard for View.
 */
fun View.showForceKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

/**
 * Try to hide the keyboard and returns whether it worked
 */
fun View.hideKeyboard(): Boolean {
    return try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
        false
    }

}

/**
 * Extension method to remove the required boilerplate for running code after a view has been
 * inflated and measured.
 */
fun <T : View> T.afterMeasured(f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

/**
 * Extension method to set View's start padding.
 */
var View.setPaddingStart: Int
    @DimenRes get() = setPaddingStart
    set(value) {
        setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)
    }

/**
 * Extension method to set View's end padding.
 */
var View.setPaddingEnd: Int
    @DimenRes get() = setPaddingEnd
    set(value) {
        setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)
    }

/**
 * Extension method to set View's top padding.
 */
var View.setPaddingTop: Int
    @DimenRes get() = setPaddingTop
    set(value) {
        setPaddingRelative(paddingStart, value, paddingEnd, paddingBottom)
    }

/**
 * Extension method to set View's bottom padding.
 */
var View.setPaddingBottom: Int
    @DimenRes get() = setPaddingBottom
    set(value) {
        setPaddingRelative(paddingStart, paddingTop, paddingEnd, value)
    }

/**
 * Extension method to set View's horizontal padding.
 */
var View.setPaddingHorizontal: Int
    @DimenRes get() = setPaddingHorizontal
    set(value) {
        setPaddingRelative(value, paddingTop, value, paddingBottom)
    }

/**
 * Extension method to set View's vertical padding.
 */
var View.setPaddingVertical: Int
    @DimenRes get() = setPaddingVertical
    set(value) {
        setPaddingRelative(paddingStart, value, paddingEnd, value)
    }

/**
 * Extension method to set View's height.
 */
var View.setHeight: Int
    @DimenRes get() = setHeight
    set(value) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

/**
 * Extension method to set View's width.
 */
var View.setWidth: Int
    @DimenRes get() = setWidth
    set(value) {
        val lp = layoutParams
        lp?.let {
            lp.width = value
            layoutParams = lp
        }
    }

/**
 * Extension method to resize View with height & width.
 */
fun View.resize(width: Int, height: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}

var scope: CoroutineScope? = null

/**
 * Set an onclick listener
 */
@Suppress("UNCHECKED_CAST")
fun <T : View> T.click(debounceMilli: Long = 0L, block: T.() -> Unit) = setOnClickListener {
    scope?.cancel()
    scope = CoroutineScope(Dispatchers.Main)
    scope?.launch {
        delay(debounceMilli)
        block(it as T)
    }
}

/**
 * Extension method to set OnLongClickListener on a view.
 */
@Suppress("UNCHECKED_CAST")
fun <T : View> T.longClick(block: T.() -> Unit) = setOnLongClickListener {
    block(it as T)
    true
}

fun View.disable() {
    isEnabled = false
    alpha = 0.3f
    isClickable = false
}

fun View.enable() {
    isEnabled = true
    alpha = 1.0f
    isClickable = true
}

fun View.isDisable(): Boolean {
    return (!isEnabled && !isClickable)
}


var View.backgroundTint: Int
    @ColorInt get() = backgroundTint
    @SuppressLint("RestrictedApi")
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (background != null) {
                backgroundTintList = context.resColorStateList(value)
            } else {
                setBackgroundColor(context.resColor(value))
            }
        } else {
            if (background != null) {
                when {
                    this is AppCompatAutoCompleteTextView -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatTextView -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatButton -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatImageView -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatMultiAutoCompleteTextView -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatEditText -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatImageButton -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    this is AppCompatSpinner -> supportBackgroundTintList =
                        context.resColorStateList(value)
                    else -> DrawableCompat.setTintList(background, context.resColorStateList(value))
                }
            } else {
                setBackgroundColor(context.resColor(value))
            }

        }
    }

/*------------------------------------ViewGroup-----------------------------------------------*/

fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

fun ViewGroup.forEachIndexed(action: (index: Int, View) -> Unit) {
    for ((index, _) in (0 until childCount).withIndex()) {
        action(index, getChildAt(index))
    }
}

/*------------------------------------TextView-----------------------------------------------*/

/**
 * Extension method to set a drawable to the start of a TextView.
 */
var TextView.drawableStart: Int
    @DrawableRes get() = drawableStart
    set(value) {
        val d = this.compoundDrawablesRelative
        var start: Drawable? = null
        if (value != -1) {
            start = this.context.resDrawable(value)!!
        }
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(start, d[1], d[2], d[3])
    }


/**
 * Extension method to set a drawable to the ens of a TextView.
 */
var TextView.drawableEnd: Int
    @DrawableRes get() = drawableEnd
    set(value) {
        val d = this.compoundDrawablesRelative
        var end: Drawable? = null
        if (value != -1) {
            end = this.context.resDrawable(value)!!
        }
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(d[0], d[1], end, d[3])
    }

var TextView.drawableTop: Int
    @DrawableRes get() = drawableTop
    set(value) {
        val d = this.compoundDrawablesRelative
        var top: Drawable? = null
        if (value != -1) {
            top = this.context.resDrawable(value)!!
        }
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(d[0], top, d[2], d[3])
    }

var TextView.drawableBottom: Int
    @DrawableRes get() = drawableBottom
    set(value) {
        val d = this.compoundDrawablesRelative
        var bottom: Drawable? = null
        if (value != -1) {
            bottom = this.context.resDrawable(value)!!
        }
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(d[0], d[1], d[2], bottom)
    }


fun TextView.toUpperCase(locale: Locale = Locale.getDefault()) {
    text = value.toUpperCase(locale)
}

fun TextView.toLowerCase(locale: Locale = Locale.getDefault()) {
    text = value.toLowerCase(locale)
}

var TextView.setTextSize: Float
    get() = setTextSize
    set(value) {
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
        )
    }

val TextView.value
    get() = text.toString().trim()


var TextView.textColor: Int
    @ColorRes get() = textColor
    set(value) {
        this.setTextColor(this.context.resColor(value))
    }

fun TextView.clear() {
    text = ""
}

fun TextView.toCamelCase() {
    text = value.toCamelCase()
}

var TextView.resHintString: Int
    get() = resHintString
    set(value) {
        setHint(value)
    }

var TextView.resString: Int
    @StringRes get() = resString
    set(value) {
        setText(value)
    }

fun TextView.bold() {
    typeface = ResourcesCompat.getFont(context, R.font.poppinsbold)
}

fun TextView.normal() {
    typeface = ResourcesCompat.getFont(context, R.font.poppinsmedium)
}

fun TextView.semiBold() {
    typeface = ResourcesCompat.getFont(context, R.font.poppinssemibold)
}

/*------------------------------------EditText-----------------------------------------------*/

fun EditText.afterTextChanged(afterTextChanged: (Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable)
        }
    })
}

fun EditText.onTextChanged(onTextChanged: (CharSequence?, Int, Int, Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(p0, start, before, count)
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun EditText.beforeTextChanged(beforeTextChanged: (CharSequence?, Int, Int, Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeTextChanged(s, start, count, after)
        }


        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onEndDrawableClick(click: EditText.() -> Unit) {
    setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingEnd) {
                if (event.action == MotionEvent.ACTION_UP) {
                    click(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

class InputFilterMinMax : InputFilter {
    private var min: Int
    private var max: Int

    constructor(min: Int, max: Int) {
        this.min = min
        this.max = max
    }

    constructor(min: String, max: String) {
        this.min = min.toDoubleOrNull()?.toInt() ?: 0
        this.max = max.toDoubleOrNull()?.toInt() ?: 0
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input: Int = (dest.toString().toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) return null
        } catch (nfe: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}

/*------------------------------------SwipeRefreshLayout-----------------------------------------------*/

/*var androidx.swiperefreshlayout.widget.SwipeRefreshLayout.backgroundColor: Int
    @ColorRes get() = backgroundColor
    set(value) {
        setProgressBackgroundColorSchemeColor(this.context.resColor(value))
    }*/

/*------------------------------------BottomNavigationView-----------------------------------------------*/

val BottomNavigationView.selectedItem: Int
    get() {
        for (i in 0 until menu.size()) {
            if (menu.getItem(i).isChecked) {
                return i
            }
        }
        return 0
    }

fun BottomNavigationView.itemSelect(f: MenuItem.() -> Boolean) {
    setOnNavigationItemSelectedListener {
        f(it)
    }
}

/**
 * Adds an [RecyclerView.OnScrollListener] to show or hide the FloatingActionButton when the RecyclerView scrolls up
 * or down respectively
 */
fun androidx.recyclerview.widget.RecyclerView.bindFloatingActionButton(fab: FloatingActionButton) =
    this.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
        override fun onScrolled(
            recyclerView: androidx.recyclerview.widget.RecyclerView,
            dx: Int,
            dy: Int
        ) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && fab.isShown) {
                fab.hide()
            } else if (dy < 0 && !fab.isShown) {
                fab.show()
            }
        }
    })

fun <T : androidx.recyclerview.widget.RecyclerView.ViewHolder> T.onClick(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event(adapterPosition, itemViewType)
    }
    return this
}

inline fun <reified T> Any.castAs(f: T.() -> Unit) {
    if (this is T) {
        f(this)
    }
}

fun RecyclerView.addDivider(drawable: Drawable?, orientation: Int) {
    drawable?.let {
        addItemDecoration(object : DividerItemDecoration(
            context,
            orientation
        ) {
            /*override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                if (position == parent.adapter?.itemCount?.minus(1)) {
                    outRect.setEmpty()
                } else {
                    super.getItemOffsets(outRect, view, parent, state)
                }
            }*/

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                if (orientation == RecyclerView.VERTICAL) {
                    val left = parent.paddingLeft
                    val right = parent.width - parent.paddingRight

                    val childCount = parent.childCount
                    for (i in 0 until childCount - 1) {
                        val child = parent.getChildAt(i)

                        val params = child.layoutParams as RecyclerView.LayoutParams

                        val top = child.bottom + params.bottomMargin
                        val bottom = top + it.intrinsicHeight

                        it.setBounds(left, top, right, bottom)
                        it.draw(c)
                    }
                } else {
                    val top = parent.paddingTop
                    val bottom = parent.height - parent.paddingBottom

                    val childCount = parent.childCount
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        val right = child.right + params.rightMargin
                        val left = right - it.intrinsicWidth
                        it.setBounds(left, top, right, bottom)
                        it.draw(c)
                    }
                }
            }
        })
    }
}

fun AppCompatActivity.initBackToolbar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun AppCompatActivity.initToolbar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
}

/******* ViewPager *******/

var ViewPager2.pageLimit: Int
    get() = pageLimit
    set(value) {
        offscreenPageLimit = value
    }