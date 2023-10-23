package com.example.simplechat.models

import com.google.firebase.Timestamp

class Status {
    private var imageUri:String?=null
    private var message:String?=null
    private var timeStamp:Long?=null



    constructor(){}

    constructor(image:String?,message:String?,ts: Long?){
        this.message   = message
        this.timeStamp = ts
        this.imageUri = image
    }

    fun getImageUri():String?{
        return this.imageUri
    }
    fun getMessage():String?{
        return this.message
    }
    fun getTimeStamp():Long?{
        return this.timeStamp
    }

    fun setMessage(message: String?){
        this.message = message
    }
    fun setTimeStamp(ts: Long?){
        this.timeStamp = ts
    }
    fun setImageUri(image: String?){
        this.imageUri = image
    }


}