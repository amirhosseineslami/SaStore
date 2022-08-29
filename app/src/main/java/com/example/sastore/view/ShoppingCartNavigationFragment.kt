package com.example.sastore.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sastore.R
import com.example.sastore.adapter.ShoppingCartListAdapter
import com.example.sastore.adapter.shoppingPostModelsList
import com.example.sastore.databinding.FragmentNavBottomShoppingCartBinding
import com.example.sastore.model.ShoppingCartItemModel
import com.example.sastore.viewmodel.MainActivityViewModel
private const val TAG = "ShoppingCartNavigationF"
class ShoppingCartNavigationFragment : Fragment(){
    lateinit var binding:FragmentNavBottomShoppingCartBinding
    private lateinit var mainActivityViewModel:MainActivityViewModel
    public lateinit var cartListAdapter: ShoppingCartListAdapter
    private lateinit var handler:Handler
    private var lastShoppingCartItemList:ArrayList<ShoppingCartItemModel> = ArrayList<ShoppingCartItemModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_bottom_shopping_cart,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        binding.shoppingCartListRecyclerView.layoutManager = LinearLayoutManager(context)
        // prepare cart list adapter
        handler = Handler(Looper.getMainLooper())
        cartListAdapter = ShoppingCartListAdapter(viewLifecycleOwner,mainActivityViewModel,handler)
        binding.shoppingCartListRecyclerView.adapter = cartListAdapter
        mainActivityViewModel.getShoppingCartItemLiveData().observe(viewLifecycleOwner,shoppingCartItemObserver())
        mainActivityViewModel.getTotalPriceLiveData().observe(viewLifecycleOwner,totalPriceObserver())

    }

    private fun totalPriceObserver(): Observer<in String> {
        return Observer<String> {
                t -> binding.fragmentShoppingCartTextviewFinalCost.text = t
        }
    }

    private fun shoppingCartItemObserver(): Observer<List<ShoppingCartItemModel>> {
       return Observer<List<ShoppingCartItemModel>> {
           Log.i(TAG, "shoppingCartItemObserver: ${lastShoppingCartItemList.size} ${it.size}")
           if (lastShoppingCartItemList != it){
               Log.i(TAG, "shoppingCartItemObserver: something is added or removed")
               cartListAdapter = ShoppingCartListAdapter(viewLifecycleOwner,mainActivityViewModel,handler)
               cartListAdapter.setShoppingCartItemsList(it!! as ArrayList<ShoppingCartItemModel> /* = java.util.ArrayList<com.example.sastore.model.ShoppingCartItemModel> */)
               binding.shoppingCartListRecyclerView.adapter = cartListAdapter
               lastShoppingCartItemList = it as ArrayList<ShoppingCartItemModel>
           }
           cartListAdapter.setShoppingCartItemsList(it!! as ArrayList<ShoppingCartItemModel> /* = java.util.ArrayList<com.example.sastore.model.ShoppingCartItemModel> */)
           //binding.shoppingCartListRecyclerView.adapter?.notifyDataSetChanged()
       }
    }

}