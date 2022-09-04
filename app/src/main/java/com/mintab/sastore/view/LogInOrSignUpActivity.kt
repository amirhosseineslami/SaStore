package com.mintab.sastore.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mintab.sastore.R
import com.mintab.sastore.databinding.ActivityLogInOrSignUpBinding

private const val TAG = "LogInOrSignUpActivity"
public const val SHARED_PREFERENCES_KEY = "sharedPreferences"
public const val SHARED_PREFERENCES_EXTRA_NUMBER_KEY = "number"

class LogInOrSignUpActivity : AppCompatActivity() {
    lateinit var activityLogInOrSignUpBinding: ActivityLogInOrSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLogInOrSignUpBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_log_in_or_sign_up
        )
        var sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
        if (sharedPreferences.getString(SHARED_PREFERENCES_EXTRA_NUMBER_KEY,null)==null) {

        }else{
startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}