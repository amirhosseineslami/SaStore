package com.example.sastore.view

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sastore.R
import com.example.sastore.adapter.ShoppingCartListAdapter
import com.example.sastore.databinding.FragmentNavBottomShoppingCartBinding
import com.example.sastore.model.ShoppingCartItemModel
import com.example.sastore.viewmodel.MainActivityViewModel
import com.zarinpal.ZarinPalBillingClient
import com.zarinpal.billing.purchase.Purchase
import com.zarinpal.client.BillingClientStateListener
import com.zarinpal.client.ClientState
import com.zarinpal.provider.core.future.FutureCompletionListener
import com.zarinpal.provider.core.future.TaskResult
import com.zarinpal.provider.model.response.Receipt
import es.dmoral.toasty.Toasty
import java.util.jar.Manifest

private const val TAG = "ShoppingCartNavigationF"

class ShoppingCartNavigationFragment : Fragment() {
    lateinit var binding: FragmentNavBottomShoppingCartBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    public lateinit var cartListAdapter: ShoppingCartListAdapter
    private lateinit var handler: Handler
    private var lastShoppingCartItemList: ArrayList<ShoppingCartItemModel> =
        ArrayList<ShoppingCartItemModel>()
    private var totalPriceAmount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nav_bottom_shopping_cart,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel =
            ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        binding.shoppingCartListRecyclerView.layoutManager = LinearLayoutManager(context)
        // prepare cart list adapter
        handler = Handler(Looper.getMainLooper())
        cartListAdapter =
            ShoppingCartListAdapter(viewLifecycleOwner, mainActivityViewModel, handler)
        binding.shoppingCartListRecyclerView.adapter = cartListAdapter
        mainActivityViewModel.getShoppingCartItemLiveData()
            .observe(viewLifecycleOwner, shoppingCartItemObserver())
        mainActivityViewModel.getTotalPriceLiveData()
            .observe(viewLifecycleOwner, totalPriceObserver())

        // prepare and set OnClick
        val listener = ShoppingCartNavigationEventListener(requireActivity())
        binding.listener = listener

    }

    private fun totalPriceObserver(): Observer<in String> {
        return Observer<String> { t ->
            binding.fragmentShoppingCartTextviewFinalCost.text = t
            totalPriceAmount = t.toInt()
        }
    }

    private fun shoppingCartItemObserver(): Observer<List<ShoppingCartItemModel>> {
        return Observer<List<ShoppingCartItemModel>> {

            if (mainActivityViewModel.isAnyNewProductRemovedOrAddedToTheShoppingCartList) {
                Log.i(TAG, "shoppingCartItemObserver: something is added or removed")
                cartListAdapter =
                    ShoppingCartListAdapter(viewLifecycleOwner, mainActivityViewModel, handler)
                cartListAdapter.setShoppingCartItemsList(it!! as ArrayList<ShoppingCartItemModel> /* = java.util.ArrayList<com.example.sastore.model.ShoppingCartItemModel> */)
                binding.shoppingCartListRecyclerView.adapter = cartListAdapter
            }

            cartListAdapter.setShoppingCartItemsList(it as ArrayList<ShoppingCartItemModel> /* = java.util.ArrayList<com.example.sastore.model.ShoppingCartItemModel> */)
            //binding.shoppingCartListRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    class ShoppingCartNavigationEventListener(private val activity: Activity) {
        public fun purchaseBtnListener(view: View) {
            val client = ZarinPalBillingClient.newBuilder(activity)
                .enableShowInvoice()
                .setListener(stateListener)
                .setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                .build()

            // after payment
            client.launchBillingFlow(purchase, object : FutureCompletionListener<Receipt> {
                override fun onComplete(task: TaskResult<Receipt>) {
                    if (task.isSuccess) {
                        val receipt = task.success
                        Log.v("ZP_RECEIPT", receipt!!.transactionID)
                        Toasty.success(activity,"payment succeed:)",Toast.LENGTH_SHORT).show()
                        //here you can send receipt data to your server
                        //sentToServer(receipt)

                    } else {
                        Toast.makeText(activity,"payment failed!",Toast.LENGTH_SHORT).show()
                        task.failure?.printStackTrace()
                    }
                }
            })
        }

        // before payment client
        private val stateListener = object : BillingClientStateListener {
            override fun onClientSetupFinished(state: ClientState) {
                //Observing client states
                Log.i(TAG, "onClientSetupFinished: ${state.name}")

            }

            override fun onClientServiceDisconnected() {
                Log.v("TAG_INAPP", "Billing client Disconnected")
                Toast.makeText(activity,"client is disconnected!",Toast.LENGTH_SHORT).show()
                //When Service disconnect
            }
        }
        val purchase = Purchase.newBuilder()
            .asPaymentRequest(
                "f9881867-f9c5-4a24-b84f-3b94b38a80de",
                1000,
                "return:\\com.example.sastore",
                "1000IRR Purchase"
            ).build()



    }

}

