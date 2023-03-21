package com.nada.tech.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Spannable
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.nada.tech.Controller
import com.nada.tech.common.BaseActivity
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Utils {

    @SuppressLint("HardwareIds")
    private fun generateDeviceIdentifier(context: Context = Controller.instance): String {
        val pseudoId = "35"
        +Build.BOARD.length % 10
        +Build.BRAND.length % 10
        +Build.SUPPORTED_ABIS.size % 10
        +Build.DEVICE.length % 10
        +Build.DISPLAY.length % 10
        +Build.HOST.length % 10
        +Build.ID.length % 10
        +Build.MANUFACTURER.length % 10
        +Build.MODEL.length % 10
        +Build.PRODUCT.length % 10
        +Build.TAGS.length % 10
        +Build.TYPE.length % 10
        +Build.USER.length % 10

        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val longId = pseudoId + androidId
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(longId.toByteArray(), 0, longId.length)
            val md5Bytes: ByteArray = messageDigest.digest()
            var identifier = ""
            for (md5Byte in md5Bytes) {
                val b = 0xFF and md5Byte.toInt()
                if (b <= 0xF) identifier += "0"
                identifier += Integer.toHexString(b)
            }
            return identifier.uppercase(Locale.ENGLISH)
        } catch (e: java.lang.Exception) {
            Log.e("TAG", e.toString())
        }
        return ""
    }

    fun getUniquePsuedoID(): String {
        val identifier = generateDeviceIdentifier()
        if (identifier.isNotEmpty()) return identifier

        val uniqueSerialCode = "35"
        +Build.BOARD.length % 10
        +Build.BRAND.length % 10
        +Build.SUPPORTED_ABIS[0].length % 10
        +Build.DEVICE.length % 10
        +Build.MANUFACTURER.length % 10
        +Build.MODEL.length % 10
        +Build.PRODUCT.length % 10

        var serial: String
        try {
            serial = Build::class.java.getField("SERIAL")[null]!!.toString()
            return UUID(uniqueSerialCode.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: Exception) {
            serial = "serial"
        }
        return UUID(uniqueSerialCode.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    fun executeOnMain(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        justTry {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
        return "" //04c67804c6ee1cc0
    }

    fun executeInBackground(runnable: Runnable?) {
        Thread(runnable).start()
    }

    fun executeDelay(runnable: Runnable, delay: Long) {
        Handler().postDelayed(runnable, delay)
    }

    @JvmStatic
    fun Toast(s: String) {
        Toast.makeText(Controller.instance, s, Toast.LENGTH_SHORT).show()
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun setSpan(view: TextView, fulltext: String, subtext: String, color: Int) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE)
        val str = view.text as Spannable
        val i = fulltext.indexOf(subtext)
        str.setSpan(
            UnderlineSpan(),
            i,
            i + subtext.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun isValideEmail(email: AppCompatEditText?): Boolean {
        val emailExp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,10}$"
        val pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email!!.text.toString())
        return if (matcher.matches()) {
            true
        } else {
            email.error = "Enter valid email"
            email.requestFocus()
            false
        }
    }

    fun isValidePass(pass: AppCompatEditText?): Boolean {
        val emailExp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+\$).{4,}\$"
        val pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(pass!!.text.toString())
        return if (matcher.matches()) {
            true
        } else {
            pass.requestFocus()
            false
        }
    }

    fun getFormattedDate(dateString: String, outputformat: String, inputFormat: String): String {
        val sdf = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        try {
            val date = sdf.parse(dateString)
            return SimpleDateFormat(outputformat, Locale.ENGLISH).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    fun hideKeyboard(baseActivity: BaseActivity) {
        try {
            if (baseActivity.currentFocus != null) {
                val imm =
                    baseActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(baseActivity.currentFocus!!.windowToken, 0)

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showKeyboard(baseActivity: BaseActivity, editText: AppCompatEditText) {
        val imm =
            baseActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}