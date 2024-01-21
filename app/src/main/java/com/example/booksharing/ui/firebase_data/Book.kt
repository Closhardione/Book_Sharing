package com.example.booksharing.ui.firebase_data

data class Book(
    var title: String="",
    var author: String="",
    var genre: String="",
    var description: String="",
    var owner: String="",
    var currentState: String=""
){
     fun toStringOwned(): String{
        return "\"$title\"  $author $genre - $currentState\n$description"
    }
    fun toStringBorrow(): String{
        return "\"$title\" - $author \n" +
                "$description\n" +
                "Właściciel: $owner"
    }
}
