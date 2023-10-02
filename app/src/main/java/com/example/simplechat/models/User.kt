package com.example.simplechat.models

/**
 * Data class representing a user in the chat application.
 *
 * This class defines the structure of a user object, including their name, email, and unique user ID (UID).
 * It provides both an empty constructor required for Firebase and a constructor to initialize user data.
 *
 * @property name The name of the user.
 * @property email The email address of the user.
 * @property uid The unique user ID (UID) of the user.
 */
class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var fcmToken:String? = null
    /**
     * Empty constructor required for Firebase.
     */
    constructor() {}

    /**
     * Constructor to initialize user data.
     *
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param uid The unique user ID (UID) of the user.
     */
    constructor(name: String?, email: String?, uid: String?,fcmToken: String?) {
        this.email = email
        this.name = name
        this.uid = uid
        this.fcmToken = fcmToken
    }
}
