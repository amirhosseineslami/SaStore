package com.mintab.sastore.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mintab.sastore.R
import com.mintab.sastore.databinding.FragmentNavdrawerSettingsBinding
import com.mintab.sastore.worker.PrepareFingerprint
import es.dmoral.toasty.Toasty

private const val TAG = "SettingsNavDrawerFragme"
public const val SHARED_PREFERENCES_BIOMETRIC_FINGERPRINT_KEY = "biometricFingerPrint"

class SettingsNavDrawerFragment : Fragment() {
    private lateinit var binding: FragmentNavdrawerSettingsBinding
    private var biometricSwitchState: Boolean? = null
    private var firstBiometricSwitchState: Boolean? = null
    private lateinit var fingerPrintSwitch: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNavdrawerSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // views
        fingerPrintSwitch = binding.fragmentNavdrawerSettingsSwitchBiometric

        if (requireContext().getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(
                    SHARED_PREFERENCES_BIOMETRIC_FINGERPRINT_KEY, false
                )
        ) {
            binding.fragmentNavdrawerSettingsSwitchBiometric.isChecked = true
        }
        firstBiometricSwitchState = fingerPrintSwitch.isChecked

        // listeners
        val listener =
            SettingsNavDrawerFragmentEventListener(
                biometricSwitchState,
                requireContext(),
                //biometricManager,
                //biometricPrompt,
                fingerPrintSwitch
            )
        binding.listener = listener

    }

    public class SettingsNavDrawerFragmentEventListener(
        var biometricSwitchState: Boolean?,
        val context: Context,
        //val biometricManager: BiometricManager,
        //val biometricPrompt: BiometricPrompt,
        val fingerPrintSwitch: SwitchCompat
    ) {
        private lateinit var biometricPrompt: BiometricPrompt
        var biometricManager: BiometricManager = BiometricManager.from(context)

        init {
            val executor = ContextCompat.getMainExecutor(context)
            biometricPrompt = BiometricPrompt(
                context as FragmentActivity,
                executor,
                biometricPromptCallback(context)
            )
        }

        private fun prepareFingerPrintSettings(
            context: Context,
            biometricManager: BiometricManager,
            biometricPrompt: BiometricPrompt
        ) {

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
                    fingerPrintSwitch.isChecked = true
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
                        }
                        BiometricPrompt.ERROR_VENDOR -> {
                            Toasty.warning(
                                context,
                                context.getString(R.string.fragment_navdrawer_settings_toast_text_BIOMETRIC_ERROR_VENDOR),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                    fingerPrintSwitch.isChecked = false
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
                    fingerPrintSwitch.isChecked = false
                }
            }
        }

        public fun biometricSwitchOnChanged(p0: CompoundButton?, isChecked: Boolean) {
            if (p0!!.isPressed) {
                if (isChecked) {

                    val promptInfo: BiometricPrompt.PromptInfo =
                        BiometricPrompt.PromptInfo.Builder()
                            .setTitle(context.getString(R.string.fragment_navdrawer_settings_toast_text_biometricinfo_title))
                            .setSubtitle(context.getString(R.string.fragment_navdrawer_settings_toast_text_biometricinfo_subtitle))
                            .setNegativeButtonText("cancel")
                            .build()
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    biometricSwitchState = false
                    Log.i(TAG, "biometricSwitchOnChanged: $biometricSwitchState")
                }
            }
        }


    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
        if (firstBiometricSwitchState != fingerPrintSwitch.isChecked) {
            Log.i(TAG, "onStop: $biometricSwitchState")
            val editor =
                requireContext().getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                    .edit()
            editor.putBoolean(
                SHARED_PREFERENCES_BIOMETRIC_FINGERPRINT_KEY,
                fingerPrintSwitch.isChecked
            )
            editor.apply()
        }
    }
}