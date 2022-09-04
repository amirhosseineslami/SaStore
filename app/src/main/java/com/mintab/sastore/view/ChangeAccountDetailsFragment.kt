package com.mintab.sastore.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.mintab.sastore.R
import com.mintab.sastore.databinding.FragmentChangeAccountDetailsBinding
import com.mintab.sastore.model.AccountDetailModel
import com.mintab.sastore.viewmodel.MainActivityViewModel
import com.mintab.sastore.worker.CheckEditTextsState
import com.google.android.material.textfield.TextInputLayout
import es.dmoral.toasty.Toasty
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

private const val TAG = "ChangeAccountDetailsFra"
class ChangeAccountDetailsFragment : Fragment() {
    private var isSubmitProcessing: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private lateinit var loadingImageView: GifImageView
    private lateinit var fragmentChangeAccountDetailsBinding: FragmentChangeAccountDetailsBinding
    private lateinit var submitBtn: Button
    private lateinit var args: ChangeAccountDetailsFragmentArgs
    private lateinit var accountDetailModel: AccountDetailModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSubmitProcessing.value = false
        // get account fragment parameters
        args = ChangeAccountDetailsFragmentArgs.fromBundle(requireArguments())
        val stringsParameters = args.parameters
        accountDetailModel = AccountDetailModel(
            stringsParameters[0],
            stringsParameters[1],
            stringsParameters[2],
            stringsParameters[3],
            stringsParameters[4]
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChangeAccountDetailsBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_change_account_details, container, false
            )
        return fragmentChangeAccountDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // observer
        isSubmitProcessing.observe(viewLifecycleOwner, submitProcessingObserver())

        // content
        loadingImageView =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountImageViewSubmitLoading
        submitBtn = fragmentChangeAccountDetailsBinding.fragmentChangeAccountButtonSubmit
        val replacementNumberEditText =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountTextInputLayoutReplacementPhoneNumber
        val passwordEditText =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountTextInputLayoutPassword
        val newPasswordEditText =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountTextInputLayoutNewPassword
        val postalCodeEditText =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountTextInputLayoutPostalCode
        val nameEditText =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountTextInputLayoutName
        val addressEditText =
            fragmentChangeAccountDetailsBinding.fragmentChangeAccountTextInputLayoutAddress


        // listener
       val listener = ChangeAccountDetailsFragmentEventListener(
            requireContext(),
            isSubmitProcessing,
            replacementNumberEditText,
            passwordEditText,
            newPasswordEditText,
            addressEditText,
            nameEditText,
            postalCodeEditText,
            accountDetailModel
        )

        // class objects
        val viewModel = MainActivityViewModel()
        fragmentChangeAccountDetailsBinding.viewModel = viewModel
        fragmentChangeAccountDetailsBinding.listener = listener
        fragmentChangeAccountDetailsBinding.model = accountDetailModel

    }

    private fun submitProcessingObserver(): Observer<in Boolean> {
        return Observer<Boolean> { t ->
            if (!t!!) {
                Log.i(TAG, "submitProcessingObserver: finished")
                // submit process finished
                loadingImageView.visibility = View.INVISIBLE
                submitBtn.text =
                    context?.getString(R.string.fragment_change_account_details_button_submit)
            } else {
                Log.i(TAG, "submitProcessingObserver: started")
                // submit process started
                val gifFromResource =
                    GifDrawable(resources, R.drawable.spinner_1s_200px_2)
                loadingImageView.setImageDrawable(gifFromResource)
                loadingImageView.visibility = View.VISIBLE
                submitBtn.text = ""
            }
        }

    }

    public class ChangeAccountDetailsFragmentEventListener(
        val context: Context,
        val isSubmitProcessing: MutableLiveData<Boolean>,
        val replacementNumberEditText: TextInputLayout,
        val passwordEditText: TextInputLayout,
        val newPasswordEditText: TextInputLayout,
        val addressEditTextInputLayout: TextInputLayout,
        val nameEditText: TextInputLayout,
        val postalCodeEditText: TextInputLayout,
        val accountDetailModel: AccountDetailModel
    ) {
        var checkEditTextsState = CheckEditTextsState(
            replacementNumberEditText,
            passwordEditText,
            nameEditText,
            addressEditTextInputLayout,
            newPasswordEditText,
            postalCodeEditText,
            context
        )

        public fun submitBtnListener(
            view: View,
            viewModel: MainActivityViewModel,
            accountDetailModel: AccountDetailModel
        ) {
            Log.i(TAG, "submitBtnListener: ")
            // check the edit texts state
            val isNumberValid = checkEditTextsState.checkIsNumberValid()
            val isPasswordValid = checkEditTextsState.checkIsPasswordValid()
            val isNameValid = checkEditTextsState.checkIsNameValid()
            val isAddressValid = checkEditTextsState.checkIsAddressValid()
            val isNewPasswordValid = checkEditTextsState.checkIsNewPasswordValid()
            val isPostalCodeValid = checkEditTextsState.checkIsPostalCodeValid()

            if (!isSubmitProcessing.value!! && isNumberValid && isPasswordValid && isNewPasswordValid && isNameValid && isAddressValid && isPostalCodeValid) {
                isSubmitProcessing.value = true

                viewModel.updateAccountDetails(accountDetailModel).observe(context as LifecycleOwner
                ) {
                    isSubmitProcessing.value = false
                    checkReceivedCode(view,it,passwordEditText)
                }

                // do if server doesn't response after 10 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Your Code
                    if (isSubmitProcessing.value!!) {
                        isSubmitProcessing.value = false
                        Toasty.error(
                            context,
                            com.mintab.sastore.R.string.toast_not_connected_to_database
                        ).show()
                    }
                }, 10000)
            }
        }

        private fun checkReceivedCode(view: View,code: Int, passwordEditText: TextInputLayout) {
            when(code){
                1000->{
                    Toasty.error(context,R.string.toast_not_connected_to_database).show()
                }
                1001->{
                    passwordEditText.error = context.getString(R.string.change_account_details_error_incorrect_password)
                }
                211->{
                    Toasty.error(context,R.string.change_account_details_error_not_successful).show()
                }
                216->{
                    Toasty.success(context,R.string.change_account_details_submit_successful).show()
                    Navigation.findNavController(view).popBackStack()
                }
            }
        }


    }


}