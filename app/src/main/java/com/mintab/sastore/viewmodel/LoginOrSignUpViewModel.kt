package com.mintab.sastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mintab.sastore.utils.Repository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class LoginOrSignUpViewModel :ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    fun login(number:String, password:String):LiveData<Any>{
       return Repository.getInstance().login(number,password,compositeDisposable)
    }
   fun loginByCall(number: String,password: String):LiveData<Any>{
        return Repository.getInstance().loginByCall(number,password)
    }
    fun signUp(number: String,password: String,name:String):LiveData<Int>{
        return Repository.getInstance().signUp(number,password,name,compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()

    }
}