package com.mintab.sastore.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

data class AccountDetailModel(
    private var name: String?,
    private var number: String?,
    private var password: String?,
    private var address: String?,
    private var postalcode: String?,
    private var replacementnumber: String?,
    private var newpassword: String?
) : BaseObservable() {
    constructor(name: String,address: String,postalcode: String,replacementnumber: String,number: String) : this(name,number,"",address,postalcode,replacementnumber,"")


    @Bindable
    fun getName(): String?{
        return name
    }
    fun setName(name: String){
        this.name = name
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.name)
    }
    @Bindable
    fun getNumber(): String? {
        return number
    }
    fun setNumber(number: String){
        this.number = number
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.number)
    }
    @Bindable
    fun getPassword(): String? {
        return password
    }
    fun setPassword(password: String){
        this.password = password
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.password)
    }
    @Bindable
    fun getPostalCode(): String? {
        return postalcode
    }
    fun setPostalCode(postalcode: String){
        this.postalcode = postalcode
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.postalCode)
    }
    @Bindable
    fun getAddress(): String? {
        return address
    }
    fun setAddress(address: String){
        this.address = address
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.address)
    }
    @Bindable
    fun getNewPassword(): String? {
        return newpassword
    }
    fun setNewPassword(newPassword: String){
        this.newpassword = newPassword
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.newPassword)
    }
    @Bindable
    fun getReplacementNumber(): String? {
        return replacementnumber
    }
    fun setReplacementNumber(replacementnumber: String){
        this.replacementnumber = replacementnumber
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.replacementNumber)
    }
}