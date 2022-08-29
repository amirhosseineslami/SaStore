package com.example.sastore.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.sastore.model.AccountDetailModel
import com.example.sastore.model.ProductImagesModel
import com.example.sastore.model.ProductModel
import com.example.sastore.model.ShoppingPostModel
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "Repository"

class Repository {
    companion object {
        var repositoryInstance: Repository? = null
        fun getInstance(): Repository {
            if (repositoryInstance == null) {
                repositoryInstance = Repository()
            }
            return repositoryInstance as Repository
        }
    }

    fun getShoppingPosts(compositeDisposable: CompositeDisposable): LiveData<List<ShoppingPostModel>> {
        var liveData = MutableLiveData<List<ShoppingPostModel>>()
        RetrofitInstance.getInstance()?.getShoppingPosts()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : SingleObserver<List<ShoppingPostModel>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: List<ShoppingPostModel>) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ${e.message}")
                }
            })
        return liveData
    }

    fun login(
        number: String,
        password: String,
        compositeDisposable: CompositeDisposable
    ): LiveData<Any> {
        val liveData: MutableLiveData<Any> = MutableLiveData()
        RetrofitInstance.getInstance()?.login(number, password)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : io.reactivex.rxjava3.core.SingleObserver<Any> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: Any) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "onError: ${e.message}")
                }

            })
        return liveData
    }

    fun signUp(
        number: String,
        password: String,
        name: String,
        compositeDisposable: CompositeDisposable
    ): LiveData<Int> {
        var liveData: MutableLiveData<Int> = MutableLiveData<Int>()
        RetrofitInstance.getInstance()?.signUp(number, password, name)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : SingleObserver<Int> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: Int) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    e.stackTrace
                    Log.i(
                        TAG,
                        "onError: " + e.message + e.stackTraceToString() + e.localizedMessage
                    )
                }

            })
        return liveData
    }

    fun loginByCall(
        number: String,
        password: String
    ): LiveData<Any> {
        val liveData: MutableLiveData<Any> = MutableLiveData()
        RetrofitInstance.getInstance()?.loginByCall(number, password)
            ?.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        liveData.value = response.body()
                    } else {
                        Log.i(TAG, "onResponse: ${response.message()} code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            })
        return liveData
    }

    public fun getProductDetails(
        name: String,
        compositeDisposable: CompositeDisposable
    ): MutableLiveData<ProductModel> {
        var productModel = MutableLiveData<ProductModel>()
        RetrofitInstance.getInstance()?.getProductDetails(name)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(object : SingleObserver<ProductModel> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: ProductModel) {
                    productModel.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: product detail ${e.message}")
                }
            })
        return productModel
    }

    public fun getProductDetailImages(
        name: String,
        compositeDisposable: CompositeDisposable
    ): MutableLiveData<List<ProductImagesModel>> {
        val productImagesModel = MutableLiveData<List<ProductImagesModel>>()
        RetrofitInstance.getInstance()?.getProductImages(name)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(object : SingleObserver<List<ProductImagesModel>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: List<ProductImagesModel>) {
                    productImagesModel.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: product detail image ${e.message}")
                }

            })
        return productImagesModel
    }

    public fun getAccountDetails(
        number: String,
        compositeDisposable: CompositeDisposable
    ): MutableLiveData<AccountDetailModel> {
        val accountDetailModel = MutableLiveData<AccountDetailModel>()
        RetrofitInstance.getInstance()!!.getAccountDetails(number)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<AccountDetailModel> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: AccountDetailModel) {
                    accountDetailModel.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ${e.message}")
                }

            })
        return accountDetailModel
    }

    public fun updateAccountDetails(
        accountDetailModel: AccountDetailModel,
        compositeDisposable: CompositeDisposable
    ): MutableLiveData<Int> {
        val liveData = MutableLiveData<Int>()
        RetrofitInstance.getInstance()!!
            .updateAccountDetails(
                accountDetailModel.getName(),
                accountDetailModel.getNumber(),
                accountDetailModel.getPassword(),
                accountDetailModel.getAddress(),
                accountDetailModel.getReplacementNumber(),
                accountDetailModel.getPostalCode(),
                accountDetailModel.getNewPassword()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(t: Int) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ${e.message}")
                }

            })
        return liveData
    }

}