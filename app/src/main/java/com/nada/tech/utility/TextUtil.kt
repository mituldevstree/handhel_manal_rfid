package com.nada.tech.utility

import android.graphics.Paint
import android.text.TextUtils
import android.text.util.Linkify
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView

object TextUtil {
    fun isNullOrEmpty(charSequence: CharSequence?): Boolean {
        return charSequence?.toString()?.isEmpty() ?: true
    }

    fun isNullOrEmpty(string: String?): Boolean {
        return string?.isEmpty() ?: true
    }

    fun isNullOrEmpty(textView: TextView?): Boolean {
        if (textView == null) return true
        return if (textView.text == null) true else isNullOrEmpty(
            textView.text.toString().trim { it <= ' ' })
    }

    fun isNullOrEmpty(editText: EditText?): Boolean {
        if (editText == null) return true
        return if (editText.text == null) true else isNullOrEmpty(
            editText.text.toString().trim { it <= ' ' })
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun linkify(txtMsg: TextView?) {
        Linkify.addLinks(txtMsg!!, Linkify.ALL)
    }

    fun parseDouble(value: String): Double {
        return try {
            value.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun parseFloat(value: Int?): Float {
        if (value == null) return 0f
        return try {
            value.toFloat()
        } catch (e: Exception) {
            0f
        }
    }

    fun parseLong(value: String): Long {
        return try {
            value.toLong()
        } catch (e: Exception) {
            0
        }
    }

    fun parseInt(raw: String): Int {
        return try {
            raw.toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun parseBoolean(raw: String?): Boolean {
        if (raw == null || raw.isEmpty()) return false
        if (raw.contains("1") || raw.contains("0")) {
            if (raw.contains("1")) return true
            if (raw.contains("0")) return false
        } else return raw.toLowerCase().contains("true")
        return false
    }

    fun getLastCharacters(word: String, count: Int): String {
        if (word.length == count) return word
        return if (word.length > count) word.substring(word.length - count) else ""
    }

    fun addStrike(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun removeStrike(textView: TextView) {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}