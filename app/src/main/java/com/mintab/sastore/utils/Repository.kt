package com.mintab.sastore.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mintab.sastore.model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


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
        RetrofitInstance.getInstance(null)?.getShoppingPosts()
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
        RetrofitInstance.getInstance(null)?.login(number, password)
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
        RetrofitInstance.getInstance(null)?.signUp(number, password, name)
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
        RetrofitInstance.getInstance(null)?.loginByCall(number, password)
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
        RetrofitInstance.getInstance(null)?.getProductDetails(name)
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
        RetrofitInstance.getInstance(null)?.getProductImages(name)
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
        RetrofitInstance.getInstance(null)!!.getAccountDetails(number)
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
                    Log.e(TAG, "onError account detail: ${e.message}")
                }

            })
        return accountDetailModel
    }

    public fun updateAccountDetails(
        accountDetailModel: AccountDetailModel,
        compositeDisposable: CompositeDisposable
    ): MutableLiveData<Int> {
        val liveData = MutableLiveData<Int>()
        RetrofitInstance.getInstance(null)!!
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
                    Log.e(TAG, "onError update: ${e.message}")
                }

            })
        return liveData
    }

    public fun pay(
        refID: String,
        number: String,
        price: String,
        purchaseDate: String,
        compositeDisposable: CompositeDisposable
    ): MutableLiveData<Int> {
        val liveData: MutableLiveData<Int> = MutableLiveData()
        RetrofitInstance.getInstance(null)!!.pay(refID, number, price, purchaseDate)
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
                    Log.e(TAG, "onError pay: ${e.message}")
                }
            })

        return liveData
    }

    /*
       fun uploadPicture(file: MultipartBody.Part, disposable: CompositeDisposable):MutableLiveData<UploadPicModel>{
           val liveData = MutableLiveData<UploadPicModel>()
           RetrofitInstance.getClient()!!.uploadPicture(file)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(object :SingleObserver<UploadPicModel>{
                   override fun onSubscribe(d: Disposable) {
                       disposable.add(d)
                   }
                   override fun onSuccess(t: UploadPicModel) {
                       liveData.value = t
                   }
                   override fun onError(e: Throwable) {
                       Log.e(TAG, "onError upload pic: ${e.message} ${e.localizedMessage} ${e.stackTrace} ${e.cause} ${e.suppressed}")
                   }
               })
           return liveData
       }*/
    public fun uploadPicture(
        file: File,
        disposable: CompositeDisposable
    ): MutableLiveData<UploadPicModel> {
        val liveData = MutableLiveData<UploadPicModel>()
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            .build()

        RetrofitInstance.getInstance(1)!!.uploadPicture(requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<UploadPicModel> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(t: UploadPicModel) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError pic upload: ${e.message}")
                }
            })
        return liveData
    }

    public fun uploadPicture1(file: File, disposable: CompositeDisposable):MutableLiveData<UploadPicModel> {
        val liveData = MutableLiveData<UploadPicModel>()
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            .build()
        RetrofitInstance.getInstance(1)!!.uploadPicture1(requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<UploadPicModel> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }
                override fun onSuccess(t: UploadPicModel) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ${e.message}", )
                }
            })
        return liveData
    }

    public fun uploadPictureByFileStack(
        file: RequestBody,
        fileType: String,
        disposable: CompositeDisposable
    ): MutableLiveData<UploadPicFileStackModel> {
        val liveData = MutableLiveData<UploadPicFileStackModel>()
        RetrofitInstance.getInstance(2)!!.uploadPictureByFileStack("A8jVws2WjR8mgiMNiRlpdz", file)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<UploadPicFileStackModel> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(t: UploadPicFileStackModel) {
                    liveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError upload filestack: ${e.message + e.stackTrace + e.cause}")
                }
            })
        return liveData
    }


}