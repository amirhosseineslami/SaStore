package com.example.sastore.worker

class FillTheNullArrayString() {
    companion object{
        fun fillTheNullArrayString(strings:Array<String?>){
            for (i in strings.indices){
                if (strings[i] == null){
                    strings[i] = ""
                }
            }
        }
    }
}
