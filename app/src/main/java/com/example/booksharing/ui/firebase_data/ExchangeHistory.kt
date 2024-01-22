package com.example.booksharing.ui.firebase_data


data class ExchangeHistory(
    var bookOwner:String = "",
    var bookborrower:String = "",
    var title: String = "",
    var author: String = "",
    var date: String = "",
    var state:String = "",
    var ownerGrade:Int = 0
){
    fun getExchangeDescription():String{
        return "Użytkownik: $bookborrower chce pożyczyć Twoją książkę: \n \"$title\" - $author\n$date"
    }
    fun getExchangeEndDescription():String{
        return "Pożyczono książkę: \"$title\" - $author\n Od: $bookOwner\n Ocena właściciela: $ownerGrade"
    }
    fun getExchangePendingDescription():String{
        return "Użytkownik: $bookborrower pożyczył książkę: \n \"$title\" - $author\n$date"
    }
}
