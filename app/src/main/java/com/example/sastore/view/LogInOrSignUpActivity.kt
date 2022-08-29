package com.example.sastore.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sastore.R
import com.example.sastore.databinding.ActivityLogInOrSignUpBinding
import com.example.sastore.viewmodel.LoginOrSignUpViewModel
import kotlin.math.log

private const val TAG = "LogInOrSignUpActivity"
public const val SHARED_PREFERENCES_KEY_NAME = "sharedPreferences"
public const val SHARED_PREFERENCES_NUMBER_KEY = "number"

class LogInOrSignUpActivity : AppCompatActivity() {
    lateinit var activityLogInOrSignUpBinding: ActivityLogInOrSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLogInOrSignUpBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_log_in_or_sign_up
        )
        var sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY_NAME, MODE_PRIVATE)
        if (sharedPreferences.getString(SHARED_PREFERENCES_NUMBER_KEY,null)==null) {

        }else{
startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}