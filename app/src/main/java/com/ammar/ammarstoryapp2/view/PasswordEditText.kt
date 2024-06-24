package com.ammar.ammarstoryapp2.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.ammar.ammarstoryapp2.R

class PasswordEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(){
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(character: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    character.toString().isEmpty() -> {
                        setError(context.getString(R.string.password_harus_diisi), null)
                    }
                    character.toString().length < 8 -> {
                        setError(context.getString(R.string.password_tidak_boleh_kurang_dari_8_karakter), null)
                    }
                    else -> {
                        error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}