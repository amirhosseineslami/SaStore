package com.mintab.sastore.model

data class UploadPicModel(var status:Boolean, var data:Data) {

    data class Data(var file:File)
    data class File(var url: Url,var metadata:Metadata)
    data class Url(var full:String, var short:String)
    data class Metadata(var id:String, var name:String, var size:Size)
    data class Size(var bytes:Int,var readable:String)
}