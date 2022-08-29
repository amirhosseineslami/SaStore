package com.example.sastore.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.sastore.BR

data class LoginOrSignUpModel(
    private var name: String,
    private var number: String,
    private var password: String
):  BaseObservable() {
    constructor() : this("","","")

    fun setName(name: String) {
        this.name = name
        notifyPropertyChanged(BR.name)
    }

    @Bindable
    fun getName(): String {
        return name
    }

    fun setNumber(number: String) {
        this.number = number
        notifyPropertyChanged(BR.number)
    }

    @Bindable
    fun getNumber(): String {
        return number
    }

    fun setPassword(password: String) {
        this.password = password
        notifyPropertyChanged(BR.password)
    }

    @Bindable
    fun getPassword(): String {
        return password
    }
}