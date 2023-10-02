package com.example.simplechat.models


class Message {
    var message: String? = null
    var senderUid: String? = null

    var imageUrl: String? = null


constructor()
    constructor(message: String?, senderUid: String?, imageUrl: String? = "") {
        this.message = message
        this.senderUid = senderUid

        this.imageUrl = imageUrl
    }
}