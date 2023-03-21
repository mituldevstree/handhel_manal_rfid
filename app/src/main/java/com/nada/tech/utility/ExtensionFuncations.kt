package com.nada.tech.utility

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nada.tech.BuildConfig
import com.nada.tech.Controller
import com.nada.tech.model.PopupModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null,
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * Return trimmed text of EditText
 * */
fun EditText.getTrimText(): String = text.toString().trim()

/**
 * Return true If EditText is empty otherwise false
 * */
fun EditText.isEmpty(): Boolean = TextUtils.isEmpty(text.toString().trim())

/**
 * Return true If EditText is not empty otherwise false
 * */
fun EditText.isNotEmpty(): Boolean = !isEmpty()

inline fun EditText.afterTextChanged(crossinline listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            listener(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}

inline fun EditText.onTextChanged(crossinline listener: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener(s, start, before, count)
        }
    })
}

inline fun EditText.setOnRightDrawableClickListener(crossinline listener: () -> Unit) {
    setOnTouchListener(View.OnTouchListener { v, event ->
        // val DRAWABLE_LEFT = 0
        // val DRAWABLE_TOP = 1
        val drwableRight = 2
        // val DRAWABLE_BOTTOM = 3
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= right - compoundDrawables[drwableRight].bounds.width()) {
                listener()
                return@OnTouchListener true
            }
        }
        false
    })
}


fun isValidPassword(password: String): Boolean {
    val pattern: Pattern
    val matcher: Matcher

    val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$"

    pattern = Pattern.compile(PASSWORD_PATTERN)
    matcher = pattern.matcher(password)

    return matcher.matches()
}

val gson = GsonBuilder().disableHtmlEscaping().create()
fun Any?.toJSONObject(): JSONObject {
    return JSONObject(this.toJson())
}

fun Any?.toJson(): String = gson.toJson(this)

fun Any?.toJsonExcludeExpose(): String {
    val gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()
    return gson.toJson(this)
}

fun <T> String?.fromJson(classOfT: Class<T>): T = Gson().fromJson(this, classOfT)

fun <T> String?.fromJsonList(): List<T> {
    return Gson().fromJson(this, object : TypeToken<List<T>>() {}.type) ?: emptyList<T>()
}

/**
 * Enable/Disable EditText to editable
 * */
fun EditText.setEditable(enable: Boolean) {
    isFocusable = enable
    isFocusableInTouchMode = enable
    isClickable = enable
    isCursorVisible = enable
}

/*
* Make EditText Scrollable inside scrollview
* */
@SuppressLint("ClickableViewAccessibility")
fun EditText.makeScrollableInScrollView() {
    setOnTouchListener(View.OnTouchListener { v, event ->
        if (hasFocus()) {
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
            }
        }
        false
    })
}

/*
* Show Error at TextInputLayout
* */
/*fun TextInputLayout.showError(errorText: String) {
    if (errorText.isNotEmpty()) {
        isErrorEnabled = true
        error = errorText
    }
}*/

/*
* Hide error at TextInputLayout
* */
/*fun TextInputLayout.hideError() {
    isErrorEnabled = false
    error = null
}*/


/**
 * Check minimum length of EditText content
 * */
fun EditText.hasMinLength(minLength: Int): Boolean {
    return getTrimText().length >= minLength
}

/*
* Run Block of function on debug mode
* */
inline fun debugMode(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}

/*
* Execute block if OS version is greater or equal Lolipop(21)
* */
inline fun lollipopAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        block()
    }
}

/*
* Execute block if OS version is greater than or equal Naugat(24)
* */
inline fun nougatAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        block()
    }
}

/**
 * Check internet connection.
 * */
inline fun Context.withNetwork(block: () -> Unit, blockError: () -> Unit) {
    val connectivityManager = this
        .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    connectivityManager?.let { it ->
        val netInfo = it.activeNetworkInfo
        netInfo?.let {
            if (netInfo.isConnected)
                block()
            else
                blockError()
        }
    }
}

/*
* Execute block into try...catch
* */
inline fun <T> justTry(tryBlock: () -> T) = try {
    tryBlock()
} catch (e: Throwable) {
    e.printStackTrace()
}

// Start new Activity functionsisCheckout

/*
* Start Activity from Activity
* */
inline fun <reified T : Any> Context.launchActivity(
    noinline init: Intent.() -> Unit = {},
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

/*
* Start Activity from Activity
* */
inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    noinline init: Intent.() -> Unit = {},
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (requestCode == -1)
        startActivity(intent)
    else
        startActivityForResult(intent, requestCode)
}

/*
* Start Activity from fragment
* */
/*operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}*/

inline fun <reified T : Any> Fragment.launchActivity(
    requestCode: Int = -1,
    noinline init: Intent.() -> Unit = {},
) {
    val intent = newIntent<T>(this.requireContext())
    intent.init()
    if (requestCode == -1)
        startActivity(intent)
    else
        startActivityForResult(intent, requestCode)

}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)


fun Context.openPdfFromUrl(pdfUrl: String?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
    startActivity(browserIntent)
}

fun Fragment.openPdfFromUrl(pdfUrl: String?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
    startActivity(browserIntent)
}


inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

/**
 * Return simple class name
 * */
fun Any.getClassName(): String {
    return this::class.java.simpleName
}

/*
* Show Home button at ActionBar and set icon
* */
fun ActionBar?.showHomeButton(
    show: Boolean, @DrawableRes icon: Int = R.drawable.ic_lock_idle_alarm,
) {
    this?.setDisplayHomeAsUpEnabled(show)
    this?.setDisplayShowHomeEnabled(show)
    this?.setHomeAsUpIndicator(icon)
}


fun Intent.getInt(key: String, defaultValue: Int = 0): Int {
    return extras?.getInt(key, defaultValue) ?: defaultValue
}

fun Intent.getString(key: String, defaultValue: String = ""): String {
    return extras?.getString(key, defaultValue) ?: defaultValue
}

/*
* Return activity main content view
* */
val Activity.contentView: View?
    get() = findViewById<ViewGroup>(R.id.content)?.getChildAt(0)


/**
 * Hide/Show view with scale animation
 * */
fun View.setVisibilityWithScaleAnim(visibility: Int) {
    this.clearAnimation()
    this.visibility = View.VISIBLE
    val scale = if (visibility == View.GONE)
        0f
    else
        1f

    val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", scale),
        PropertyValuesHolder.ofFloat("scaleY", scale)
    )
    scaleDown.duration = 300
    scaleDown.interpolator = DecelerateInterpolator()
    scaleDown.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            this@setVisibilityWithScaleAnim.visibility = visibility
        }
    })
    scaleDown.start()
}

fun View.setAnimationWithVisibility(@AnimRes animationRes: Int, visibility: Int) {
    setVisibility(visibility)
    clearAnimation()
    val viewAnim = AnimationUtils.loadAnimation(context, animationRes)
    animation = viewAnim
    viewAnim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            setVisibility(visibility)
        }

        override fun onAnimationStart(animation: Animation?) {
            setVisibility(View.VISIBLE)
        }
    })
    // viewAnim.start()
}

fun Context.getAppVersionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

fun Context.showToast(
    message: String?,
    duration: Int = Toast.LENGTH_SHORT,
    gravity: Int = Gravity.CENTER,
) {
    if (!message.isNullOrEmpty())
        Toast.makeText(this, message, duration).run {
            setGravity(gravity, 0, 0)
            show()
        }
}

fun SpannableString.setClickableSpan(
    start: Int,
    end: Int, @ColorInt color: Int,
    block: (view: View?) -> Unit,
) {
    setSpan(object : ClickableSpan() {
        override fun onClick(view: View) {
            block(view)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false // set to false to remove underline
        }

    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    // Set Color Span
    setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

/*
* Toggle integer value, from 0->1 or 1->0
* */
fun Int.toggle() = if (this == 1) 0 else 1

/*
* Return true if view is visible otherwise return false
* */
fun View.isVisible() = visibility == View.VISIBLE
fun View.isGone(): Boolean = visibility == View.GONE
fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


/*
* Set enabled/disable
* */
fun View.setEnabledWithAlpha(enabled: Boolean, disabledAlpha: Float = 0.5f) {
    isEnabled = enabled
    alpha = if (isEnabled) 1f else disabledAlpha
}


fun String?.nullSafe(defaultValue: String = ""): String {
    return this ?: defaultValue
}

fun Int?.nullSafe(defaultValue: Int = 0): Int {
    return this ?: defaultValue
}

fun Float?.nullSafe(defaultValue: Float = 0f): Float {
    return this ?: defaultValue
}

fun Long?.nullSafe(defaultValue: Long = 0L): Long {
    return this ?: defaultValue
}

fun Double?.nullSafe(defaultValue: Double = 0.0): Double {
    return this ?: defaultValue
}

fun Boolean?.nullSafe(defaultValue: Boolean = false): Boolean {
    return this ?: defaultValue
}

fun <T> List<T>?.nullSafe(): List<T> {
    return this ?: ArrayList()
}

@SuppressWarnings("deprecation")
fun String?.fromHtml(): Spanned {
    if (this == null)
        return SpannableString("")
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
        // we are using this flag to give a consistent behaviour
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        return Html.fromHtml(this)
    }
}

/**
 * Return ActionBar height
 * */
fun Activity.getActionBarHeight(): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(R.attr.actionBarSize, tv, true))
        TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    else 0
}

fun View.measureWidthHeight(onCompleteMeasure: (width: Int, height: Int) -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            onCompleteMeasure.invoke(measuredWidth, measuredHeight)
            return true
        }
    })
}

fun Drawable.setTintColor(@ColorInt colorTint: Int): Drawable {
    colorFilter = PorterDuffColorFilter(colorTint, PorterDuff.Mode.SRC_ATOP)
    return this
}

fun SearchView.setHintColor(@ColorInt hintColor: Int) {
    (findViewById<EditText>(androidx.appcompat.R.id.search_src_text)).setHintTextColor(hintColor)
}

fun String?.float(defaultValue: Float = 0f): Float {
    return if (isNullOrEmpty()) {
        defaultValue
    } else {
        try {
            this.toFloat().nullSafe()
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }
}

/**
 * Convert string to int, If string is empty or null return default value
 * */
fun String?.int(defaultValue: Int = 0): Int {
    return if (isNullOrEmpty()) {
        defaultValue
    } else {
        try {
            this.toInt().nullSafe()
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }
}

/**
 * Convert string to double, If string is empty or null return default value
 * */
fun String?.toDoubleOrDefaultValue(defaultValue: Double = 0.0): Double {
    return if (isNullOrEmpty()) {
        defaultValue
    } else {
        try {
            this?.toDouble().nullSafe()
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }
}

/**
 * Return Tag of view as string
 * */
fun View.getStringTag(): String = if (tag == null) "" else tag.toString()

fun RadioGroup.getCheckedButtonText(): String {
    return if (checkedRadioButtonId != -1)
        findViewById<RadioButton>(checkedRadioButtonId).text.toString()
    else
        ""
}


fun ImageView.loadImage(
    imageUrl: String?,
    @DrawableRes placeholder: Int = 0,
) {
    justTry {
        Glide.with(Controller.instance).load(imageUrl).placeholder(placeholder).into(this)
    }
}

fun ImageView.loadImageWithError(
    imageUrl: String?,
    @DrawableRes placeholder: Int = 0,
) {
    justTry {
        Glide.with(Controller.instance).load(imageUrl).placeholder(placeholder)
            .into(this)
    }
}

fun ImageView.loadImage(
    images: List<String>?,
    @DrawableRes placeholder: Int = 0,
) {
    justTry {
        Glide.with(Controller.instance).clear(this)
        if (images == null || images.isEmpty()) return
        Glide.with(Controller.instance).load(images[0]).placeholder(placeholder).into(this)
    }
}

fun ImageView.loadImageSmall(
    images: List<String>?,
    @DrawableRes placeholder: Int = 0,
) {
    justTry {
        Glide.with(Controller.instance).clear(this)
        if (images == null || images.isEmpty()) return
        Glide.with(Controller.instance).load(images[0]).override(250, 250).placeholder(placeholder)
            .into(this)
    }
}

fun ImageView.loadResource(res: Int) {
    justTry { Glide.with(Controller.instance).load(res).into(this) }
}


/** multipart*/
fun EditText.toRequestBody(): RequestBody {
    return getTrimText().requestBody()
}

fun String.requestBody(): RequestBody {
    return toRequestBody(("text/plain").toMediaTypeOrNull())
}

fun File.toMultipartBody(parameterName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        parameterName,
        name,
        this.asRequestBody("image/*".toMediaType())
    )
}

fun File.toMultipartBody(type: MediaType, parameterName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        parameterName,
        name,
        this.asRequestBody(type)
    )
}