package com.mintab.sastore.worker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mintab.sastore.R
import com.mintab.sastore.view.LogInOrSignUpActivity
import com.mintab.sastore.view.SHARED_PREFERENCES_BIOMETRIC_FINGERPRINT_KEY
import com.mintab.sastore.view.SHARED_PREFERENCES_EXTRA_NUMBER_KEY
import com.mintab.sastore.view.SHARED_PREFERENCES_KEY
import es.dmoral.toasty.Toasty

class PrepareFingerprint(
    val context: Context,
    val isInLoggingState: Boolean,
    val fingerPrintSwitch: SwitchCompat?
) {
    public fun ActivateFingerprint() {
        val negativeButtonText: String
        if (isInLoggingState) {
            negativeButtonText =
                context.getString(R.string.fragment_navdrawer_settings_logging_negative_button_text)
        } else {
            negativeButtonText =
                context.getString(R.string.fragment_navdrawer_settings_setting_negative_button_text)
        }

        val promptInfo: BiometricPrompt.PromptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.fragment_navdrawer_settings_toast_text_biometricinfo_title))
                .setSubtitle(context.getString(R.string.fragment_navdrawer_settings_toast_text_biometricinfo_subtitle))
                .setNegativeButtonText(negativeButtonText)
                .build()
        prepareFingerPrintSettings().authenticate(promptInfo)
    }

    private fun prepareFingerPrintSettings(
    ): BiometricPrompt {
        val biometricManager: BiometricManager = BiometricManager.from(context)
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt: BiometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            biometricPromptCallback(context)
        )

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                Toasty.success(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_SUCCESS),
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_HW_UNAVAILABLE),
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_NO_HARDWARE),
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_NONE_ENROLLED),
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED),
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_UNSUPPORTED),
                    Toast.LENGTH_SHORT
                ).show()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_STATUS_UNKNOWN),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return biometricPrompt
    }

    public fun biometricPromptCallback(context: Context): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toasty.success(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_biometric_authenticate_succeed),
                    Toast.LENGTH_SHORT
                ).show()
                if (fingerPrintSwitch != null){
                    fingerPrintSwitch.isChecked = false
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_CANCELED -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_CANCELED),
                            Toasty.LENGTH_SHORT
                        ).show()
                        if (isInLoggingState) {
                            (context as Activity).finish()
                        }
                    }
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                        sendUserToSettings()
                    }
                    BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_HW_NOT_PRESENT),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_HW_UNAVAILABLE),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_LOCKOUT -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_LOCKOUT),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_LOCKOUT_PERMANENT),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_NEGATIVE_BUTTON),
                            Toasty.LENGTH_SHORT
                        ).show()
                        if (isInLoggingState) {
                            context.startActivity(
                                Intent(
                                    context,
                                    LogInOrSignUpActivity::class.java
                                )
                            )
                            val editor = context.getSharedPreferences(
                                SHARED_PREFERENCES_KEY,
                                Context.MODE_PRIVATE
                            ).edit()
                            editor.remove(SHARED_PREFERENCES_EXTRA_NUMBER_KEY)
                            editor.remove(SHARED_PREFERENCES_BIOMETRIC_FINGERPRINT_KEY)
                            editor.apply()
                            (context as Activity).finish()
                        }
                    }
                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_NO_SPACE -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_NO_SPACE),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_TIMEOUT -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_TIMEOUT),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_UNABLE_TO_PROCESS),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPrompt.ERROR_USER_CANCELED -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_USER_CANCELED),
                            Toasty.LENGTH_SHORT
                        ).show()
                        if (isInLoggingState) {
                            (context as Activity).finish()
                        }
                    }
                    BiometricPrompt.ERROR_VENDOR -> {
                        Toasty.warning(
                            context,
                            context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_VENDOR),
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }
                if (fingerPrintSwitch != null){
                    fingerPrintSwitch.isChecked = false
                }
            }

            private fun sendUserToSettings() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                    intent.putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    context.startActivity(intent)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.startActivity(Intent(Settings.ACTION_FINGERPRINT_ENROLL))
                } else {
                    context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toasty.warning(
                    context,
                    context.getString(R.string.fragment_navdrawer_settings_toast_text_biometric_authenticate_failed),
                    Toast.LENGTH_SHORT
                ).show()
                if (fingerPrintSwitch != null){
                    fingerPrintSwitch.isChecked = false
                }
            }
        }
    }
}
