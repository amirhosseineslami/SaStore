package com.mintab.sastore.view

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
import com.mintab.sastore.R
import com.mintab.sastore.databinding.FragmentSignupBinding
import com.mintab.sastore.model.LoginOrSignUpModel
import com.mintab.sastore.viewmodel.LoginOrSignUpViewModel
import com.mintab.sastore.worker.CheckEditTextsState
import com.mintab.sastore.worker.LoginOrSignUpEditTextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.tapadoo.alerter.Alerter
import es.dmoral.toasty.Toasty
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

private const val TAG = "SignUpFragment"
private var isSignupProcessing: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

class SignUpFragment : Fragment() {
    lateinit var fragmentSignUpBinding: FragmentSignupBinding
    private lateinit var loadingImageView: GifImageView
    private lateinit var signUpBtn: Button
    private lateinit var numberEditText: TextInputLayout
    private lateinit var passwordEditText: TextInputLayout
    private lateinit var nameEditText: TextInputLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSignUpBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_signup, container, false
        )
        return fragmentSignUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariables()
    }

    private fun initializeVariables() {

        // model
        val signUpModel = LoginOrSignUpModel()
        fragmentSignUpBinding.model = signUpModel

        // view model
        val loginOrSignUpViewModel = LoginOrSignUpViewModel()
        fragmentSignUpBinding.viewModel = loginOrSignUpViewModel

        // components
        loadingImageView = fragmentSignUpBinding.imageViewSignupLoading
        signUpBtn = fragmentSignUpBinding.signupButton
        nameEditText = fragmentSignUpBinding.textInputLayoutSignupName
        passwordEditText = fragmentSignUpBinding.textInputLayoutSignupPassword
        numberEditText = fragmentSignUpBinding.textInputLayoutSignupNumber

        // observers
        isSignupProcessing.value = false
        isSignupProcessing.observe(context as LifecycleOwner, signUpProcessingObserver())


        // event listener
        val signUpFragmentEventListener = SignUpFragmentEventListener(
            requireActivity(),
            activity,
            numberEditText,
            passwordEditText,
            nameEditText
        )
        fragmentSignUpBinding.signUpFragmentEventListener = signUpFragmentEventListener

        // add textWatcher to remove error after user edition
        numberEditText.editText?.addTextChangedListener(LoginOrSignUpEditTextWatcher(numberEditText))
        passwordEditText.editText?.addTextChangedListener(LoginOrSignUpEditTextWatcher(passwordEditText))
        nameEditText.editText?.addTextChangedListener(LoginOrSignUpEditTextWatcher(nameEditText))

    }

    private fun signUpProcessingObserver(): Observer<in Boolean> {
        return Observer<Boolean> {
            if (!it) {
                // signup process is finished
                loadingImageView.visibility = View.INVISIBLE
                signUpBtn.text = context?.getString(com.mintab.sastore.R.string.signup_button_text)
            } else {
                // signup process is started
                val gifFromResource = GifDrawable(resources, R.drawable.spinner_1s_200px_2)
                loadingImageView.setImageDrawable(gifFromResource)
                loadingImageView.visibility = View.VISIBLE
                signUpBtn.text = ""
            }
        }
    }

    class SignUpFragmentEventListener(
        val context: Context,
        val activity: FragmentActivity?,
        val numberEditText: TextInputLayout,
        val passwordEditText: TextInputLayout,
        val nameEditText: TextInputLayout
    ) {

        public fun signUpBtnListener(
            view: View,
            viewModel: LoginOrSignUpViewModel,
            model: LoginOrSignUpModel
        ) {
            val checkEditTextsState = CheckEditTextsState(numberEditText, passwordEditText, nameEditText, context)
            context.hideKeyboard(view)
            val isNameValid =checkEditTextsState.checkIsNameValid()
            val isNumberValid = checkEditTextsState.checkIsNumberValid()
            val isPasswordValid = checkEditTextsState.checkIsPasswordValid()
            if (!isSignupProcessing.value!!  && isNameValid && isNumberValid && isPasswordValid) {
                isSignupProcessing.value = true
                signUpWithObservable(viewModel, model, view)

                // do if server doesn't response after 10 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Your Code
                    if (isSignupProcessing.value!!) {
                        isSignupProcessing.value = false
                        Toasty.error(
                            context,
                            com.mintab.sastore.R.string.toast_not_connected_to_database
                        ).show()
                    }
                }, 10000)
            }
        }

        private fun signUpWithObservable(
            viewModel: LoginOrSignUpViewModel,
            model: LoginOrSignUpModel,
            view: View
        ) {
            viewModel.signUp(model.getNumber(), model.getPassword(), model.getName()).observe(
                activity as LifecycleOwner
            ) { t ->
                isSignupProcessing.value = false
                Log.i(TAG, "onChanged: ${t}")
                checkReceivedCode(view, t!!, model)
            }
        }

        private fun checkReceivedCode(view: View, code: Int, model: LoginOrSignUpModel) {
            when (code) {
                1000 -> {
                    // not connected to the database
                    Toasty.error(context, R.string.toast_not_connected_to_database).show()
                }
                213 -> {
                    // this account is already created
                    Alerter.create(activity = activity!!)
                        .setText(R.string.alert_this_account_already_is_created)
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
                            activity.getString(R.string.alert_button_this_account_already_is_created),
                            com.tapadoo.alerter.R.style.AlertButton
                        ) {
                            goToLoginFragmentListener(view)
                            Alerter.hide()
                        }
                        .show()
                }
                214 -> {
                    // not successfully signed up the account
                    Toasty.error(context, R.string.toast_not_successful_signing_up).show()
                }
                215 -> {
                    // successfully signed up
                    val editoor = context.getSharedPreferences(
                        SHARED_PREFERENCES_KEY,
                        Context.MODE_PRIVATE
                    ).edit()
                    editoor.putString(SHARED_PREFERENCES_EXTRA_NUMBER_KEY, model.getNumber())
                    editoor.apply()

                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as LogInOrSignUpActivity).finish()
                    Toasty.success(activity!!, R.string.toast_signed_up_successfully).show()
                }

            }
        }

        public fun goToLoginFragmentListener(view: View) {
            Navigation.findNavController(view)
                .navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }

        fun Context.hideKeyboard(view: View) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}