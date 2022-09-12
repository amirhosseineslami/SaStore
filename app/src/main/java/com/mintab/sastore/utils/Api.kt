package com.mintab.sastore.utils

import com.mintab.sastore.model.*
import io.reactivex.rxjava3.core.Single
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


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
        @Field("number") number: String?,
        @Field("password") password: String?,
        @Field("address") address: String?,
        @Field("replacementnumber") replacementNumber: String?,
        @Field("postalcode") postalCode: String?,
        @Field("newpassword") newPassword: String?
    ): Single<Int>

    @FormUrlEncoded
    @POST("Pay.php")
    public fun pay(
        @Field("refID") refID: String,
        @Field("number") number: String,
        @Field("price") price: String,
        @Field("purchasedate") purchaseDate: String
    ): Single<Int>


    /*
        @Multipart
        @POST("upload")
        public fun uploadPicture(
            @Field("file") file: File
        ):Single<UploadPicModel>
    */
/*
    @Multipart
    @POST("upload")
    public fun uploadPicture(@Part file: MultipartBody.Part): Single<UploadPicModel>
    */

    @POST("upload")
    fun uploadPicture(@Body file: RequestBody?): Single<UploadPicModel>


    @Multipart
    @POST("upload")
    fun uploadPicture1(
        @Part("file") file: RequestBody
    ): Single<UploadPicModel>



    @Headers("Content-Type: image/jpeg")
    @POST("S3")
    public fun uploadPictureByFileStack(
        @Query("key") key: String,
        @Body body: RequestBody
    ): Single<UploadPicFileStackModel>

}