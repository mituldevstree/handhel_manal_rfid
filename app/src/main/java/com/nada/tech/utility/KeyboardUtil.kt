package com.nada.tech.utility

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil {
    fun hideKeyboard(view: View?) {
        if (view == null) return
        val inputMethodManager =
            view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(activity: Activity?) {
        val view = activity?.currentFocus ?: return
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(
            activity.window.decorView.windowToken,
            0
        )
    }

    fun showKeyboard(view: View?) {
        if (view == null) return
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}