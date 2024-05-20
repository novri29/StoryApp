package com.nov.storyapp.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.nov.storyapp.R
import com.nov.storyapp.helper.ValidEmail

class EditTextEmail @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validate()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Add the validate method
    fun validate(): Boolean {
        return if (text?.let { email -> !ValidEmail(email.toString()) } == true) {
            error = context.getString(R.string.error_invalid_email)
            setBackgroundResource(R.drawable.edit_text_error)
            false
        } else {
            error = null
            true
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean = true
}
