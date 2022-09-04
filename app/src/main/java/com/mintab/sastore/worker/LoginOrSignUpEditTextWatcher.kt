package com.mintab.sastore.worker

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class LoginOrSignUpEditTextWatcher(val editText: TextInputLayout) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (editText.error != null){
            editText.error = null
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

}
