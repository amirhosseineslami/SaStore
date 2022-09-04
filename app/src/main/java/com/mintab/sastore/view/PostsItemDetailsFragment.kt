package com.mintab.sastore.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mintab.sastore.R
import com.mintab.sastore.adapter.MyViewPagerAdapter
import com.mintab.sastore.customview.ProductCounterView
import com.mintab.sastore.databinding.FragmentPostsItemDetailsBinding
import com.mintab.sastore.model.ProductModel
import com.mintab.sastore.model.ShoppingCartItemModel
import com.mintab.sastore.viewmodel.MainActivityViewModel

private const val TAG = "PostsItemDetailsFragmen"

class PostsItemDetailsFragment : Fragment() {
    lateinit var fragmentPostsItemDetailsBinding: FragmentPostsItemDetailsBinding
    lateinit var postsItemDetailsFragmentArgs: PostsItemDetailsFragmentArgs
    lateinit var viewModel: MainActivityViewModel
    lateinit var productCounterView: ProductCounterView
    var isTheFirstTimeFragmentRun = true
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
        // initialize views
        productCounterView = fragmentPostsItemDetailsBinding.fragmentProductDetailProductCounter

        viewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        prepareProductDetailsViewToShow()
        val listener =
            PostItemDetailFragmentEventListener(
                productCounterView,
                viewLifecycleOwner,
                viewModel
            )
        fragmentPostsItemDetailsBinding.listener = listener
        fragmentPostsItemDetailsBinding.viewmodel = viewModel

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
                prepareTheCounterObserver(t)
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

    private fun prepareTheCounterObserver(productModel: ProductModel) {
        val itemModel = ShoppingCartItemModel(productModel.name, productModel.price, "", "", "0")
        val index = viewModel.isProductExistInCartList(itemModel)
        if (index != -1) {
            viewModel.getShoppingCartItemLiveData()
                .observe(
                    viewLifecycleOwner,
                    shoppingCartNumberChangesObserver(index, productCounterView)
                )
            productCounterView.visibility = View.VISIBLE
            productCounterView.getProductCountLiveData()
                .observe(viewLifecycleOwner, productCounterObserver(itemModel))
        }
    }

    private fun productCounterObserver(shoppingCartItemModel: ShoppingCartItemModel): Observer<in Int> {
        return Observer<Int> {
            viewModel.setProductCount(it, shoppingCartItemModel)
            if (it == 0) {
                productCounterView.visibility = View.INVISIBLE
            }
        }
    }

    private fun shoppingCartNumberChangesObserver(
        index: Int,
        productCounterView: ProductCounterView
    ): Observer<ArrayList<ShoppingCartItemModel>> {
        return Observer<ArrayList<ShoppingCartItemModel>> {
            if (!productCounterView.isVisible) {
                productCounterView.visibility = View.VISIBLE
                productCounterView.setProductCountStr(it!![index].numberOfProduct)
            }
        }
    }


    class PostItemDetailFragmentEventListener(
        val productCounterView: ProductCounterView,
        val viewLifecycleOwner: LifecycleOwner,
        val viewModel: MainActivityViewModel
    ) {

        public fun addToCartBtnListener(
            model: ProductModel,
            viewModel: MainActivityViewModel,
            imageurl: String
        ) {


            // set counter button visibility
            val shoppingCartItemModel = ShoppingCartItemModel(model.name,model.price,imageurl,"","1")
            val index = viewModel.isProductExistInCartList(shoppingCartItemModel)
            if (index == -1){
                viewModel.addProductToShoppingCartList(shoppingCartItemModel)
            }
            if (!productCounterView.isVisible) {
                prepareTheCounterObserver(model)
            } else {
                productCounterView.setProductCountStr((productCounterView.getProductCount()!! + 1).toString())
            }
        }

        private fun prepareTheCounterObserver(productModel: ProductModel) {
            val itemModel =
                ShoppingCartItemModel(productModel.name, productModel.price, "", "", "0")
            val index = viewModel.isProductExistInCartList(itemModel)
            if (index != -1) {
                viewModel.getShoppingCartItemLiveData()
                    .observe(
                        viewLifecycleOwner,
                        shoppingCartNumberChangesObserver(index, productCounterView)
                    )
                productCounterView.getProductCountLiveData()
                    .observe(viewLifecycleOwner, productCounterObserver(itemModel))
            }
        }

        private fun productCounterObserver(shoppingCartItemModel: ShoppingCartItemModel): Observer<in Int> {
            return Observer<Int> {
                Log.i(TAG, "productCounterObserver: number:$it")
                viewModel.setProductCount(it, shoppingCartItemModel)
                if (it == 0) {
                    productCounterView.visibility = View.INVISIBLE
                }
            }
        }

        private fun shoppingCartNumberChangesObserver(
            index: Int,
            productCounterView: ProductCounterView
        ): Observer<ArrayList<ShoppingCartItemModel>> {
            return Observer<ArrayList<ShoppingCartItemModel>> {
                if (!productCounterView.isVisible) {
                    productCounterView.visibility = View.VISIBLE
                }
            }
        }

    }
}