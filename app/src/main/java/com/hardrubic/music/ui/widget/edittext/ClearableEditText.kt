package com.hardrubic.music.ui.widget.edittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import com.hardrubic.music.R

class ClearableEditText : AppCompatEditText, OnTouchListener, OnFocusChangeListener {

    private var icDeleteDrawable: Drawable? = null

    private var t: OnTouchListener? = null
    private var f: OnFocusChangeListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        icDeleteDrawable = compoundDrawables[2]
        if (icDeleteDrawable == null) {
            icDeleteDrawable = resources.getDrawable(R.mipmap.ic_delete_text)
        }
        icDeleteDrawable!!.setBounds(0, 0, icDeleteDrawable!!.intrinsicWidth, icDeleteDrawable!!.intrinsicHeight)
        setClearIconVisible(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isFocused) {
                    setClearIconVisible(!TextUtils.isEmpty(s))
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val tappedX = event.x > width - paddingRight - icDeleteDrawable!!.intrinsicWidth
            if (tappedX) {
                if (event.action == MotionEvent.ACTION_UP) {
                    setText("")
                }
                return true
            }
        }

        return if (t != null) {
            t!!.onTouch(v, event)
        } else false
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(!TextUtils.isEmpty(text))
        } else {
            setClearIconVisible(false)
        }

        f?.let {
            it.onFocusChange(v, hasFocus)
        }
    }

    protected fun setClearIconVisible(visible: Boolean) {
        val wasVisible = compoundDrawables[2] != null
        if (visible != wasVisible) {
            val x = if (visible) icDeleteDrawable else null
            setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], x, compoundDrawables[3])
        }
    }
}
