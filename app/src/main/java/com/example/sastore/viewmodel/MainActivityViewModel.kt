package com.example.sastore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sastore.model.*
import com.example.sastore.utils.Repository
import io.reactivex.rxjava3.disposables.CompositeDisposable

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {
    private var shoppingCartItemList = ArrayList<ShoppingCartItemModel>()
    private var shoppingCartItemLiveData = MutableLiveData<ArrayList<ShoppingCartItemModel>>()
    private var totalPriceLiveData = MutableLiveData<String>()
    private var compositeDisposable = CompositeDisposable()

    public var isAnyNewProductRemovedOrAddedToTheShoppingCartList = false
    public fun getShoppingPosts(): LiveData<List<ShoppingPostModel>> {
        return Repository.getInstance().getShoppingPosts(compositeDisposable)
    }

    public fun getProductDetails(name: String): LiveData<ProductModel> {
        return Repository.getInstance().getProductDetails(name, compositeDisposable)
    }

    public fun getProductImages(name: String): LiveData<List<ProductImagesModel>> {
        return Repository.getInstance().getProductDetailImages(name, compositeDisposable)
    }

    public fun getAccountDetails(number: String): LiveData<AccountDetailModel> {
        return Repository.getInstance().getAccountDetails(number, compositeDisposable)
    }

    public fun updateAccountDetails(accountDetailModel: AccountDetailModel): LiveData<Int> {
        return Repository.getInstance()
            .updateAccountDetails(accountDetailModel, compositeDisposable)
    }

    public fun addProductToShoppingCartList(shoppingCartItemModel: ShoppingCartItemModel) {
        val productIndex = isProductExistInCartList(shoppingCartItemModel)
        if (productIndex == -1) {
            isAnyNewProductRemovedOrAddedToTheShoppingCartList = true
            // adding new product to list
            shoppingCartItemList.add(shoppingCartItemModel)
            shoppingCartItemLiveData.value = shoppingCartItemList
            Log.i(TAG, "addProductToShoppingCartList: added number:${shoppingCartItemModel.numberOfProduct}")
        } else {
            isAnyNewProductRemovedOrAddedToTheShoppingCartList = false
            // adding existed product
            //val newNumber = shoppingCartItemList[productIndex].numberOfProduct.toInt() + 1
            shoppingCartItemList[productIndex].numberOfProduct = (shoppingCartItemList[productIndex].numberOfProduct.toInt()+1).toString()
            shoppingCartItemLiveData.value = shoppingCartItemList
            Log.i(TAG, "addProductToShoppingCartList: exist number: ${shoppingCartItemList[productIndex].numberOfProduct}")
        }
        setTotalPriceLiveData()
    }

    public fun setProductCount(count: Int, shoppingCartItemModel: ShoppingCartItemModel) {
        val position = isProductExistInCartList(shoppingCartItemModel)
        if (count == 0) {
            isAnyNewProductRemovedOrAddedToTheShoppingCartList = true
            shoppingCartItemList.removeAt(position)
            shoppingCartItemLiveData.value = shoppingCartItemList
        } else {
            isAnyNewProductRemovedOrAddedToTheShoppingCartList = false
            shoppingCartItemList[position].numberOfProduct = count.toString()
            shoppingCartItemLiveData.value = shoppingCartItemList
        }
        setTotalPriceLiveData()
    }

    public fun getShoppingCartItemLiveData(): MutableLiveData<ArrayList<ShoppingCartItemModel>> {
        return shoppingCartItemLiveData
    }

    public fun isProductExistInCartList(shoppingCartItemModel: ShoppingCartItemModel): Int {
        var index = -1
        for (i in shoppingCartItemList.indices) {
            if (shoppingCartItemList[i].name == shoppingCartItemModel.name) {
                index = i
                break
            }
        }
        return index
    }


    public fun getTotalPrice():String{
        var totalPrice = 0
        for (cartItemModel in shoppingCartItemList){
            totalPrice+= cartItemModel.price.toInt()*cartItemModel.numberOfProduct.toInt()
        }
        return totalPrice.toString()
    }
    public fun setTotalPriceLiveData() {
        totalPriceLiveData.value = getTotalPrice()
    }
    public fun getTotalPriceLiveData():MutableLiveData<String> {
        return totalPriceLiveData
    }

    public fun pay(refID:String,number: String,price:String,purchaseDate:String):MutableLiveData<Int>{
        return Repository.getInstance().pay(refID, number, price, purchaseDate, compositeDisposable)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}