package com.example.sastore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.sastore.R
import com.example.sastore.databinding.PostShoppingItemLayoutBinding
import com.example.sastore.model.ShoppingPostModel
import com.example.sastore.view.HomeNavigationFragmentDirections
import com.google.android.material.navigation.NavigationBarView

public var shoppingPostModelsList = ArrayList<ShoppingPostModel>()
class ShoppingPostsListAdapter : RecyclerView.Adapter<ShoppingPostsListAdapter.Holder>() {

    class Holder(val layoutBinding: PostShoppingItemLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {
        init {
            layoutBinding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    Navigation.findNavController(it)
                        .navigate(HomeNavigationFragmentDirections
                            .actionHomeNavigationFragmentToPostsItemDetailsFragment(
                        shoppingPostModelsList[position].name))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val postShoppingItemLayoutBinding: PostShoppingItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.post_shopping_item_layout, parent, false
        )
        return Holder(postShoppingItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.layoutBinding.model = shoppingPostModelsList[position]
    }

    override fun getItemCount(): Int {
        return shoppingPostModelsList.size
    }

    public fun setShoppingPostModelList(shoppingPostModelList: List<ShoppingPostModel>) {
        shoppingPostModelsList =
            shoppingPostModelList as ArrayList<ShoppingPostModel> /* = java.util.ArrayList<com.example.sastore.model.ShoppingPostModel> */
        notifyDataSetChanged()
    }

}