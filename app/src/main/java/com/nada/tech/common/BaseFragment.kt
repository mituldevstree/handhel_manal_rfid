package com.nada.tech.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    var mContext: Context? = null
    var base: BaseActivity? = null
    var navigation: NavigationActivity? = null
    var rootView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (context is BaseActivity) {
            base = context
        }
        if (context is NavigationActivity) {
            navigation = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
    }

    fun showSnackBar(message: String?) {
        if (base == null) return
        base?.showSnackBar(rootView, message)
    }

    fun showSnackBar(view: View?, message: String?) {
        if (base == null) return
        base?.showSnackBar(view, message)
    }

    fun getDummyList(length: Int): List<String> {
        val list: MutableList<String> =
            ArrayList()
        for (i in 0 until length) {
            list.add("Dummy Data $i")
        }
        return list
    }
}