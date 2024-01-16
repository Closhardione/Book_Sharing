package com.example.booksharing.ui.firebase_data

data class Account(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var library: List<Int>
)
