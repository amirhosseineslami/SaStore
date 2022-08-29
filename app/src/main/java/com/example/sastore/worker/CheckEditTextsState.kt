package com.example.sastore.worker

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.EditText
import com.example.sastore.R
import com.google.android.material.textfield.TextInputLayout

private const val TAG = "CheckEditTextsState"

class CheckEditTextsState(
    val numberEditText: TextInputLayout,
    val passwordEditText: TextInputLayout,
    val nameEditText: TextInputLayout?,
    val addressEditText: TextInputLayout?,
    val newPasswordEditText: TextInputLayout?,
    val postalCodeEditText: TextInputLayout?,
    val context: Context
) {
    constructor(
        numberEditText: TextInputLayout,
        passwordEditText: TextInputLayout,
        context: Context
    ) : this(
        numberEditText,
        passwordEditText,
        null,
        null,
        null,
        null,
        context
    )

    constructor(
        numberEditText: TextInputLayout,
        passwordEditText: TextInputLayout,
        nameEditText: TextInputLayout,
        context: Context
    ) : this(
        numberEditText,
        passwordEditText,
        nameEditText,
        null,
        null,
        null,
        context
    )

    private fun isItNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    private fun checkSpaceContainingError(editText: EditText, p0: CharSequence): Boolean {
        var hasSpaceError = false
        editText.error = null
        // does it contain spaces?
        if (doesContainSpace(p0)) {
            hasSpaceError = true
            if (editText == passwordEditText) {
                editText.error =
                    context.getString(R.string.login_edittext_password_error_space)
            } else {
                editText.error =
                    context.getString(R.string.login_edittext_number_error_space)
            }
        } else {
            editText.error = null
        }
        return hasSpaceError
    }

    fun doesContainSpace(charSequence: CharSequence): Boolean {
        var hasSpace = false
        for (char in charSequence) {
            if (char == ' ') {
                hasSpace = true
                break
            }
        }
        return hasSpace
    }


    public fun checkIsPasswordValid(): Boolean {
        var isPasswordValid = false
        val p0 = passwordEditText.editText?.text
        if (passwordEditText.editText?.text!!.isNotEmpty()) {
            passwordEditText.error = null
            if (p0!!.length < 6) {
                Log.i(TAG, "length is less than 6")
                passwordEditText.error =
                    context?.getString(R.string.login_edittext_password_error_minimum_quantity)
            } else {
                if (!checkSpaceContainingError(passwordEditText.editText!!, p0)) {
                    Log.i(TAG, "not space")
                    passwordEditText.error = null
                    if (p0.length > 16) {
                        Log.i(TAG, "more than max")
                        passwordEditText.error =
                            context?.getString(R.string.login_edittext_password_error_maximum_quantity)
                    } else {
                        Log.i(TAG, "valid")
                        // finally is valid
                        passwordEditText.error = null
                        isPasswordValid = true
                    }
                }
            }
        } else {
            // password edittext is empty
            passwordEditText.error =
                context?.getString(R.string.login_edittext_password_error_minimum_quantity)
        }
        return isPasswordValid
    }


    public fun checkIsNumberValid(): Boolean {
        var isNumberValid = false
        val p0 = numberEditText.editText!!.text
        // user is putting number
        if (numberEditText.editText!!.text.isNotEmpty()) {
            numberEditText.error = null
            if (p0?.length!! != 11) {
                numberEditText.error = context?.getString(R.string.login_edittext_number_error)
            } else {
                if (!checkSpaceContainingError(numberEditText.editText!!, p0)) {
                    numberEditText.error = null
                    // is it numeric
                    if (!isItNumeric(p0.toString())) {
                        numberEditText.error =
                            context?.getString(R.string.login_edittext_number_error_not_numeric)
                    } else {
                        // finally number format is valid
                        isNumberValid = true
                    }
                }
            }
        } else {
            // number edittext is empty
            numberEditText.error = context?.getString(R.string.login_edittext_number_error)
        }

        return isNumberValid
    }

    public fun checkIsNameValid(): Boolean {
        var isValid = false
        if (nameEditText != null) {
            Log.i(TAG, "checkIsNameValid: checking")
            val nameText = nameEditText.editText?.text.toString()
            if (nameText.length < 3) {
                Log.i(TAG, "checkIsNameValid: not min")
                nameEditText.error =
                    context.getString(R.string.signup_edittext_error_name_minimum)
            } else {
                if (nameText.length > 10) {
                    Log.i(TAG, "checkIsNameValid: alot")
                    nameEditText.error =
                        context.getString(R.string.signup_edittext_error_name_maximum)
                } else {
                    Log.i(TAG, "checkIsNameValid: true")
                    isValid = true
                }
            }
        }
        return isValid
    }

    public fun checkIsAddressValid(): Boolean {
        var isValid = false
        if (addressEditText != null) {
            val addressText = addressEditText.editText?.text.toString()
            if (addressText.length < 5) {
                addressEditText.error =
                    context.getString(R.string.change_account_details_edittext_error_address_minimum)
            } else {
                if (addressText.length > 30) {
                    addressEditText.error =
                        context.getString(R.string.change_account_details_edittext_error_address_maximum)
                } else {
                    isValid = true
                }
            }
        }
        return isValid
    }

    public fun checkIsPostalCodeValid(): Boolean {
        var isValid = false
        if (postalCodeEditText != null) {
            val postalCodeText = postalCodeEditText.editText?.text.toString()
            if (postalCodeText.length != 10) {
                postalCodeEditText.error =
                    context.getString(R.string.change_account_details_edittext_error_postal_code)
            } else {
                isValid = true
            }
        }
        return isValid
    }


    public fun checkIsNewPasswordValid(): Boolean {
        var isPasswordValid = false
        val p0 = newPasswordEditText!!.editText?.text
        if (newPasswordEditText.editText?.text!!.isNotEmpty()) {
            newPasswordEditText.error = null
            if (p0!!.length < 6) {
                Log.i(TAG, "length is less than 6")
                newPasswordEditText.error =
                    context?.getString(R.string.login_edittext_password_error_minimum_quantity)
            } else {
                if (!checkSpaceContainingError(newPasswordEditText.editText!!, p0)) {
                    Log.i(TAG, "not space")
                    newPasswordEditText.error = null
                    if (p0.length > 16) {
                        Log.i(TAG, "more than max")
                        newPasswordEditText.error =
                            context?.getString(R.string.login_edittext_password_error_maximum_quantity)
                    } else {
                        Log.i(TAG, "valid")
                        // finally is valid
                        newPasswordEditText.error = null
                        isPasswordValid = true
                    }
                }
            }
        } else {
            // new password edittext is empty
            isPasswordValid = true
        }
        return isPasswordValid
    }


}