package com.example.sastore.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.sastore.databinding.FragmentLogInBinding
import com.example.sastore.model.LoginOrSignUpModel
import com.example.sastore.viewmodel.LoginOrSignUpViewModel
import com.example.sastore.worker.CheckEditTextsState
import com.example.sastore.worker.LoginOrSignUpEditTextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.tapadoo.alerter.Alerter
import es.dmoral.toasty.Toasty
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView


private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {
    private lateinit var numberEditText: TextInputLayout
    private lateinit var passwordEditText: TextInputLayout
    private var isLoginProcessing: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    lateinit var fragmentLogInBinding: FragmentLogInBinding
    private lateinit var loadingImageView: GifImageView
    private lateinit var loginBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLogInBinding = DataBindingUtil.inflate(
            inflater,
            com.example.sastore.R.layout.fragment_log_in, container, false
        )
        return fragmentLogInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariables()
    }

    private fun initializeVariables() {

        // model
        val loginModel = LoginOrSignUpModel()
        fragmentLogInBinding.model = loginModel

        // view model
        val loginOrSignUpViewModel = LoginOrSignUpViewModel()
        fragmentLogInBinding.viewModel = loginOrSignUpViewModel


        // components
        loadingImageView = fragmentLogInBinding.imageViewLoginLoading
        loginBtn = fragmentLogInBinding.loginButton
        numberEditText = fragmentLogInBinding.textInputLayoutLoginNumber
        passwordEditText = fragmentLogInBinding.textInputLayoutLoginPassword

        // observer
        isLoginProcessing.value = false
        isLoginProcessing.observe(context as LifecycleOwner, loginProcessingObserver())

        // event listener
        val loginFragmentEventListener =
            LoginFragmentEventListener(
                requireContext(),
                activity,
                isLoginProcessing,
                numberEditText,
                passwordEditText
            )
        fragmentLogInBinding.loginFragmentEventListener = loginFragmentEventListener

        // add textWatcher to remove error after user edition
        numberEditText.editText?.addTextChangedListener(LoginOrSignUpEditTextWatcher(numberEditText))
        passwordEditText.editText?.addTextChangedListener(LoginOrSignUpEditTextWatcher(passwordEditText))

    }
    private fun loginProcessingObserver(): Observer<in Boolean> {
        return Observer<Boolean> {
            if (!it) {
                // login process finished
                loadingImageView.visibility = View.INVISIBLE
                loginBtn.text = context?.getString(com.example.sastore.R.string.login_button_text)
            } else {
                // login process started
                val gifFromResource =
                    GifDrawable(resources, com.example.sastore.R.drawable.spinner_1s_200px_2)
                loadingImageView.setImageDrawable(gifFromResource)
                loadingImageView.visibility = View.VISIBLE
                loginBtn.text = ""
            }
        }
    }

    public class LoginFragmentEventListener(
        val context: Context,
        val activity: FragmentActivity?,
        val isLoginProcessing: MutableLiveData<Boolean>,
        val numberEditText: TextInputLayout,
        val passwordEditText: TextInputLayout
    ) {
        var checkEditTextsState = CheckEditTextsState(numberEditText,passwordEditText,context)
        public fun loginBtnListener(
            view: View,
            viewModel: LoginOrSignUpViewModel,
            model: LoginOrSignUpModel
        ) {
            context.hideKeyboard(view)

            // check the validations of input
            val isNumberValid = checkEditTextsState.checkIsNumberValid()
            val isPasswordValid = checkEditTextsState.checkIsPasswordValid()
            if (!isLoginProcessing.value!! && isNumberValid && isPasswordValid) {
                isLoginProcessing.value = true
                loginWithObservable(viewModel, model, view)

                // do if server doesn't response after 10 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Your Code
                    if (isLoginProcessing.value!!) {
                        isLoginProcessing.value = false
                        Toasty.error(
                            context,
                            com.example.sastore.R.string.toast_not_connected_to_database
                        ).show()
                    }
                }, 10000)
            }
        }


        private fun loginWithObservable(
            viewModel: LoginOrSignUpViewModel,
            model: LoginOrSignUpModel,
            view: View
        ) {
            viewModel.login(model.getNumber(), model.getPassword())
                .observe(context as LifecycleOwner, object : Observer<Any> {
                    override fun onChanged(t: Any?) {
                        isLoginProcessing.value = false
                        Log.i(TAG, "onChanged: ${t.toString()}")
                        checkReceivedCode(view, t!! as Double, model)
                    }
                })
        }


        public fun goToSignUpFragmentListener(view: View) {
            Navigation.findNavController(view)
                .navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        fun checkReceivedCode(view: View, receivedCode: Double, model: LoginOrSignUpModel) {
            Log.i(TAG, "checkReceivedCode: $receivedCode")
            val code = receivedCode.toInt()
            when (code) {
                1000 -> {
                    // isn't connected to database
                    Toasty.error(
                        context,
                        com.example.sastore.R.string.toast_not_connected_to_database
                    )
                        .show()
                }
                210 -> {
                    // there is no any account Toasty.warning(context,R.string.alert_no_account_exists).show()
                    Alerter.create(activity = activity!!)
                        .setText(com.example.sastore.R.string.alert_no_account_exists)
                        .setLayoutGravity(Gravity.BOTTOM)
                        .setDismissable(true)
                        .enableClickAnimation(true)
                        .enableSwipeToDismiss()
                        .hideIcon()
                        .setTitleTypeface(Typeface.SERIF)
                        .setElevation(8f)
                        .setOnClickListener(object : View.OnClickListener {
                            override fun onClick(p0: View?) {
                                Alerter.hide()
                            }
                        })
                        .addButton(
                            context.getString(com.example.sastore.R.string.alert_no_any_account_exists_button_text),
                            com.tapadoo.alerter.R.style.AlertButton
                        ) {
                            goToSignUpFragmentListener(view)
                            Alerter.hide()
                        }
                        .show()
                }
                211 -> {
                    // password is wrong
                    Toasty.error(context, com.example.sastore.R.string.toast_wrong_password)
                        .show()
                }
                212 -> {
                    // login successful
                    val editoor = context.getSharedPreferences(
                        SHARED_PREFERENCES_KEY_NAME,
                        Context.MODE_PRIVATE
                    ).edit()
                    editoor.putString(SHARED_PREFERENCES_NUMBER_KEY, model.getNumber())
                    editoor.apply()

                    Toasty.success(context, com.example.sastore.R.string.toast_login_succeed)
                        .show()
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as LogInOrSignUpActivity).finish()
                }
            }

        }

        fun Context.hideKeyboard(view: View) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}