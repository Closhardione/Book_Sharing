package com.example.booksharing.ui.firebase_data

data class Book(
    var title: String,
    var author: String,
    var genre: String,
    var description: String,
    var owner: String,
    var currentState: String
)
