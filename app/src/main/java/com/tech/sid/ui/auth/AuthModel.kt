package com.tech.sid.ui.auth

 data class AuthModelLogin(
    val message: String,
    val success: Boolean,
    val token: String,
    val user: User
)

data class User(
    var DOB: String,
    var _id: String,
    var countryCode: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var profileImage: String
)


data class AuthModelSign(
    val message: String,
    val success: Boolean,
    val user: User
)


