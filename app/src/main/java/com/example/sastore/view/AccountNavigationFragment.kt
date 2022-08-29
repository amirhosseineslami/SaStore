package com.example.sastore.view

import android.content.Context
import android.database.Observable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.Navigation
import com.example.sastore.R
import com.example.sastore.databinding.FragmentNavBottomAccountBinding
import com.example.sastore.databinding.FragmentNavBottomHomeBinding
import com.example.sastore.model.AccountDetailModel
import com.example.sastore.viewmodel.MainActivityViewModel
import com.example.sastore.worker.CheckEditTextsState
import com.example.sastore.worker.FillTheNullArrayString
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import es.dmoral.toasty.Toasty
import pl.droidsonroids.gif.GifDrawable

class AccountNavigationFragment : Fragment() {
    lateinit var binding: FragmentNavBottomAccountBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nav_bottom_account,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel =
            ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]

        // get account details
        val sharedPreferences = requireActivity().getSharedPreferences(
            SHARED_PREFERENCES_KEY_NAME,
            Context.MODE_PRIVATE
        )
        val number = sharedPreferences.getString(SHARED_PREFERENCES_NUMBER_KEY, null)
        mainActivityViewModel.getAccountDetails(number!!)
            .observe(viewLifecycleOwner, object : Observer<AccountDetailModel> {
                override fun onChanged(t: AccountDetailModel?) {
                    binding.model = t
                }
            })
        // set Listener
        val listener = AccountNavigationFragmentEventListener()
        binding.listener = listener


    }
    public class AccountNavigationFragmentEventListener(){
        public fun editButtonListener(view: View, accountDetailModel: AccountDetailModel){
            val strings:Array<String?> = arrayOf(
                accountDetailModel.getName(),
                accountDetailModel.getAddress(),
                accountDetailModel.getPostalCode(),
                accountDetailModel.getReplacementNumber(),
                accountDetailModel.getNumber()
            )
            FillTheNullArrayString.fillTheNullArrayString(strings)
            Navigation.findNavController(view).navigate(AccountNavigationFragmentDirections
                .actionAccountNavigationFragmentToChangeAccountDetailsFragment(strings as Array<String>))
        }
    }
}