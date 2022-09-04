package com.mintab.sastore.view

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mintab.sastore.R
import com.mintab.sastore.adapter.ShoppingCartListAdapter
import com.mintab.sastore.databinding.FragmentNavBottomShoppingCartBinding
import com.mintab.sastore.model.ShoppingCartItemModel
import com.mintab.sastore.viewmodel.MainActivityViewModel
import com.zarinpal.ZarinPalBillingClient
import com.zarinpal.billing.purchase.Purchase
import com.zarinpal.billing.sku.SkuPurchased
import com.zarinpal.billing.sku.SkuQueryParams
import com.zarinpal.client.BillingClientStateListener
import com.zarinpal.client.ClientState
import com.zarinpal.provider.core.future.FutureCompletionListener
import com.zarinpal.provider.core.future.TaskResult
import com.zarinpal.provider.model.response.Receipt
import es.dmoral.toasty.Toasty


private const val TAG = "ShoppingCartNavigationF"

class ShoppingCartNavigationFragment : Fragment() {
    lateinit var binding: FragmentNavBottomShoppingCartBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var cartListAdapter: ShoppingCartListAdapter
    private lateinit var handler: Handler
    private lateinit var purchaseBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        // initialize views
        purchaseBtn = binding.fragmentShoppingCartButtonPay

        // prepare and set OnClick
        val listener = ShoppingCartNavigationEventListener(requireActivity(), mainActivityViewModel)
        binding.listener = listener

    }

    private fun totalPriceObserver(): Observer<in String> {
        return Observer<String> { t ->
            binding.fragmentShoppingCartTextviewFinalCost.text = t

            // set purchase button visibility
            purchaseBtn.visibility =
                if (t.toInt() > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
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

    public class ShoppingCartNavigationEventListener(
        val activity: Activity,
        val mainActivityViewModel: MainActivityViewModel
    ) {
        public fun purchaseBtnListener(view: View) {
            purchaseByNewWay()
        }

        private fun purchaseByNewWay() {

            val purchase = Purchase.newBuilder()
                .asPaymentRequest(
                    "f9881867-f9c5-4a24-b84f-3b94b38a80de",
                    //"71c705f8-bd37-11e6-aa0c-000c295eb8fc",
                    mainActivityViewModel.getTotalPrice().toLong(),
                    "https://minetaproject.000webhostapp.com",
                    "1000IRR Purchase"
                ).build()

            val client = ZarinPalBillingClient.newBuilder(activity)
                .enableShowInvoice()
                .setListener(stateListener)
                .setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                .enableShowInvoice()
                .build()

            // after payment
            client.launchBillingFlow(purchase, object : FutureCompletionListener<Receipt> {
                override fun onComplete(task: TaskResult<Receipt>) {
                    if (task.isSuccess) {
                        val receipt = task.success
                        Log.v("ZP_RECEIPT", receipt!!.transactionID)
                        Toasty.success(activity, "payment succeed:)", Toast.LENGTH_SHORT).show()
                        //here you can send receipt data to your server
                        //sentToServer(receipt)

                    } else {
                        Toast.makeText(activity, "payment failed! $task", Toast.LENGTH_SHORT).show()
                        task.failure?.printStackTrace()
                    }
                }
            })

            client.querySkuPurchased(skuQuery, object : FutureCompletionListener<List<SkuPurchased>> {
                override fun onComplete(task: TaskResult<List<SkuPurchased>>) {
                    if (task.isSuccess){
                        val skuPurchaseList = task.success
                        skuPurchaseList?.forEach {
                            Log.v("ZP_SKU_PURCHASED", "${it.authority} ${it.productId}")
                        }
                    }else{
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
                Toast.makeText(activity, "client succeed ${state.name}", Toast.LENGTH_SHORT).show()
            }

            override fun onClientServiceDisconnected() {
                //When Service disconnect
                Log.v("TAG_INAPP", "Billing client Disconnected")
                Toast.makeText(activity, "client is disconnected!", Toast.LENGTH_SHORT).show()
            }
        }
        val skuQuery = SkuQueryParams.newBuilder("MERCHANT_ID")
            .setSkuList(listOf("SKU_ID_000", "SKU_ID_001"))
            .orderByMobile("09944584624")
            .build()


    }

}
