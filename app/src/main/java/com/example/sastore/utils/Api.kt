package com.example.sastore.utils

import com.example.sastore.model.AccountDetailModel
import com.example.sastore.model.ProductImagesModel
import com.example.sastore.model.ProductModel
import com.example.sastore.model.ShoppingPostModel
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.*
import kotlin.jvm.internal.Intrinsics

interface Api {

    @FormUrlEncoded
    @POST("Login.php")
    public fun login(
        @Field("number") number: String,
        @Field("password") password: String
    ): Single<Any>


    @FormUrlEncoded
    @POST("Login.php")
    public fun loginByCall(
        @Field("number") number: String,
        @Field("password") password: String
    ): Call<Any>


    @FormUrlEncoded
    @POST("SignUp.php")
    public fun signUp(
        @Field("number") number: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Single<Int>

    @GET("PostProduct.php")
    public fun getShoppingPosts(): Single<List<ShoppingPostModel>>

    @GET("Products.php")
    public fun getProductDetails(@Query("name") name: String): Single<ProductModel>

    @GET("Images.php")
    public fun getProductImages(@Query("name") name: String): Single<List<ProductImagesModel>>

    @GET("AccountDetails.php")
    public fun getAccountDetails(
        @Query("number") number: String
    ): Single<AccountDetailModel>

    @FormUrlEncoded
    @POST("UpdateAccount.php")
    public fun updateAccountDetails(
        @Field("name") name: String?,
        @Field("number")number: String?,
        @Field("password")password: String?,
        @Field("address")address:String?,
        @Field("replacementnumber")replacementNumber:String?,
        @Field("postalcode")postalCode:String?,
        @Field("newpassword")newPassword:String?
    ):Single<Int>
}