package com.mintab.sastore.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.mintab.sastore.R
import com.mintab.sastore.databinding.ShoppingCartItemLayoutBinding
import com.mintab.sastore.model.ShoppingCartItemModel
import com.mintab.sastore.viewmodel.MainActivityViewModel

private const val TAG = "ShoppingCartListAdapter"

class ShoppingCartListAdapter(
    var lifecycleOwner: LifecycleOwner,
    var mainActivityViewModel: MainActivityViewModel,
    var mainHandler: Handler
) : RecyclerView.Adapter<ShoppingCartListAdapter.Holder>() {
    private var shoppingCartItemList: ArrayList<ShoppingCartItemModel> = ArrayList()
    private var firstShoppingCartItemList: ArrayList<ShoppingCartItemModel>? = null

    class Holder(val shoppingCartItemLayoutBinding: ShoppingCartItemLayoutBinding) :
        RecyclerView.ViewHolder(shoppingCartItemLayoutBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ShoppingCartItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.shopping_cart_item_layout, parent, false
        )
        return Holder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.shoppingCartItemLayoutBinding.number = shoppingCartItemList[position].numberOfProduct
        holder.shoppingCartItemLayoutBinding.model = shoppingCartItemList[position]
        holder.shoppingCartItemLayoutBinding.shoppingCartItemProductCounter.getProductCountLiveData()
            .observe(
                lifecycleOwner
            ) {
                mainActivityViewModel.setProductCount(it, shoppingCartItemList?.get(position)!!)
                if (it==0) {
                    //shoppingCartItemList.remove(shoppingCartItemList[position])
                    notifyItemRemoved(position)
                }
                for (i in shoppingCartItemList.indices){
                    Log.i(TAG, shoppingCartItemList[i].name)
                }
                //mainHandler.post(notifyTheProductListChanges(it, position))
                //(notifyTheProductListChanges(it, position))
            }
    }

    private fun notifyTheProductListChanges(productCount: Int, position: Int): Runnable {
        return Runnable {
            Log.i(TAG, "product count: $productCount")
            if (productCount == 0) {
                Log.i(TAG, "notifydata")
                notifyItemRemoved(position)
                notifyItemRangeChanged(0, firstShoppingCartItemList!!.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return shoppingCartItemList.size
    }

    fun setShoppingCartItemsList(shoppingCartItemList: ArrayList<ShoppingCartItemModel>) {
        this.shoppingCartItemList = shoppingCartItemList
        if (firstShoppingCartItemList == null// || firstShoppingCartItemList!!.size < shoppingCartItemList.size
        ) {
            firstShoppingCartItemList = shoppingCartItemList
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}