package com.nada.tech.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import android.widget.TextView.BufferType

class TextView : AppCompatTextView {
    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context!!, attrs, defStyleAttr)

    val string: String
        get() {
            val charSequence = super.getText() ?: return ""
            return charSequence.toString()
        }

//    override fun setText(charSequence: CharSequence, type: BufferType) {
//        var text = charSequence
//        if (text.isNotEmpty()) {
//            text = text[0].toString().uppercase() + text.subSequence(1, text.length)
//        }
//        super.setText(text, type)
//    }
}