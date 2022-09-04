package com.mintab.sastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.mintab.sastore.R
import com.mintab.sastore.databinding.PostShoppingItemLayoutBinding
import com.mintab.sastore.model.ShoppingPostModel
import com.mintab.sastore.view.HomeNavigationFragmentDirections

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