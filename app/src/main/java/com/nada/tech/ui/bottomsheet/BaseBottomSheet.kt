package com.nada.tech.ui.bottomsheet

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.common.BaseActivity
import com.nada.tech.common.NavigationActivity
import com.nada.tech.utility.KeyboardUtil.hideKeyboard
import com.nada.tech.utility.justTry

open class BaseBottomSheet : BottomSheetDialogFragment(), OnShowListener {
    protected var navigation: NavigationActivity? = null
    protected var base: BaseActivity? = null
    protected var actionBar: ActionBarActivity? = null
    private var expanded = false
    private var showKeyboard = false
    protected var isApplyStyle = true
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isApplyStyle) setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) base = context
        if (context is ActionBarActivity) actionBar = context
        if (context is NavigationActivity) navigation = context
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(base)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        if (window != null) {
            if (showKeyboard) window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return setExpanded(dialog)!!
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }

    fun setKeyboard(isVisible: Boolean) {
        showKeyboard = isVisible
    }

    private fun setExpanded(dialog: Dialog?): Dialog? {
        if (dialog == null) return null
        val window = dialog.window
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setDimAmount(0.25f)
            if (expanded) {
//                window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
        dialog.setOnShowListener(this)
        return dialog
    }

    fun hideBottomSheet() {
        justTry {
            if (bottomSheetBehavior == null) return
            dismiss()
        }
    }

    override fun onShow(dialog: DialogInterface) {
        val bottomSheetDialog = dialog as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
            val layoutParams = bottomSheet.layoutParams
            if (!expanded) return
            bottomSheetBehavior?.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            if (layoutParams != null) layoutParams.height = getWindowHeight()
            bottomSheet.layoutParams = layoutParams
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    open fun show(activity: FragmentActivity): BaseBottomSheet? {
        return show(activity, TAG)
    }

    fun show(activity: FragmentActivity, tag: String?): BaseBottomSheet? {
        return if (activity.isFinishing || activity.isDestroyed) null else try {
            val fragmentManager = activity.supportFragmentManager
            val fragment = fragmentManager.findFragmentByTag(tag)
            if (fragment != null) return null
            showNow(activity.supportFragmentManager, tag)
            this
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        base?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


    companion object {
        private val TAG = BaseBottomSheet::class.java.simpleName
    }
}