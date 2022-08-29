package com.example.sastore.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sastore.R
import com.example.sastore.adapter.MyViewPagerAdapter
import com.example.sastore.customview.ProductCounterView
import com.example.sastore.databinding.FragmentPostsItemDetailsBinding
import com.example.sastore.model.ProductModel
import com.example.sastore.model.ShoppingCartItemModel
import com.example.sastore.viewmodel.MainActivityViewModel

private const val TAG = "PostsItemDetailsFragmen"

class PostsItemDetailsFragment : Fragment() {
    lateinit var fragmentPostsItemDetailsBinding: FragmentPostsItemDetailsBinding
    lateinit var postsItemDetailsFragmentArgs: PostsItemDetailsFragmentArgs
    lateinit var viewModel: MainActivityViewModel
    lateinit var productCounterView: ProductCounterView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostsItemDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_posts_item_details, container, false
        )

        postsItemDetailsFragmentArgs = PostsItemDetailsFragmentArgs.fromBundle(requireArguments())

        return fragmentPostsItemDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        prepareProductDetailsViewToShow()
        val listener = PostItemDetailFragmentEventListener()
        fragmentPostsItemDetailsBinding.listener = listener
        fragmentPostsItemDetailsBinding.viewmodel = viewModel

        // initialize views
        productCounterView = fragmentPostsItemDetailsBinding.fragmentProductDetailProductCounter
    }

    private fun prepareProductDetailsViewToShow() {
        // prepare product text
        viewModel.getProductDetails(name = postsItemDetailsFragmentArgs.name.trim())
            .observe(
                viewLifecycleOwner
            ) { t ->
                fragmentPostsItemDetailsBinding.fragmentProductDetailConstantTextViewPrice.visibility =
                    View.VISIBLE
                fragmentPostsItemDetailsBinding.productDetailButtonAddToCart.visibility =
                    View.VISIBLE
                checkTheProductCartNumber(t)
                fragmentPostsItemDetailsBinding.model = t
            }

        // prepare product images
        viewModel.getProductImages(postsItemDetailsFragmentArgs.name.trim())
            .observe(
                viewLifecycleOwner
            ) { t ->
                val myViewPagerAdapter = MyViewPagerAdapter(t!!)
                fragmentPostsItemDetailsBinding.imageurl = t[0].imageurl
                fragmentPostsItemDetailsBinding.productDetailImageSlider.adapter =
                    myViewPagerAdapter
            }
    }

    private fun checkTheProductCartNumber(productModel: ProductModel) {
        val index = viewModel.isProductExistInCartList(
            ShoppingCartItemModel(
                productModel.name,
                productModel.price,
                "",
                "",
                "0"
            )
        )
        if (index != -1) {
            productCounterView.visibility = View.VISIBLE
            viewModel.getShoppingCartItemLiveData().observe(
                viewLifecycleOwner
            ) { t ->
                productCounterView.setProductCountStr(t!![index].numberOfProduct)
            }
        }
    }

    class PostItemDetailFragmentEventListener() {
        public fun addToCartBtnListener(
            model: ProductModel,
            viewModel: MainActivityViewModel,
            imageurl: String
        ) {
            Log.i(TAG, "addToCartBtnListener: ${model.name} ${model.price} $imageurl")
            viewModel.addProductToShoppingCartList(
                ShoppingCartItemModel(
                    model.name,
                    model.price,
                    imageurl,
                    model.description,
                    "1"
                )
            )
        }
    }
}