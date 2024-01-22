package com.example.booksharing.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.Account
import com.example.booksharing.ui.firebase_data.Book
import com.example.booksharing.ui.firebase_data.ExchangeHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchFragment : Fragment() {
    private val bookCollection = Firebase.firestore.collection("books")
    private val accountCollection = Firebase.firestore.collection("accounts")
    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")

    private lateinit var textViewChose:TextView
    private lateinit var spinnerGenre: Spinner
    private lateinit var buttonSearch: Button
    private lateinit var listViewBooks: ListView
    private lateinit var listAdapterSearch: ArrayAdapter<String>
    private lateinit var account:Account
    private lateinit var nearbyUsersList: MutableList<Account>
    private lateinit var foundBooks: MutableList<String>
    private var selectedGenre: String? = null
    private var profileName:String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search,container,false)
        profileName = arguments?.getString("username")
        if (profileName != null) {
            Log.d("TAG", profileName!!)
        }
        textViewChose = view.findViewById(R.id.textViewChose)
        buttonSearch = view.findViewById(R.id.buttonSearch)
        listViewBooks = view.findViewById(R.id.listViewBooks)
        listAdapterSearch = ArrayAdapter(this.requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listViewBooks.adapter = listAdapterSearch
        spinnerGenre = view.findViewById(R.id.spinnerGenre)
        val genres = arrayOf("Fantasy", "Science Fiction", "Romance", "Mystery", "Thriller", "Non-fiction")
        val adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, genres)
        spinnerGenre.adapter = adapter

        spinnerGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedGenre = genres[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        listViewBooks.setOnItemClickListener { parent, view, position, id ->
            val selectedBook = listAdapterSearch.getItem(position)

            val regexPattern = "\"(.*?)\" - (.*?)\\s*\\n(.*?)\\s*\\nWłaściciel: (.*)".toRegex()

            val matchResult = regexPattern.find(selectedBook.toString())

            if (matchResult != null) {
                val title = matchResult.groupValues[1]

                val author = matchResult.groupValues[2]

                val description = matchResult.groupValues[3]

                val owner = matchResult.groupValues[4]

                Log.d("string","Title: $title")
                Log.d("string","Author: $author")
                Log.d("string","Description: $description")
                Log.d("string","Owner: $owner")
                createExchange(title,author,owner, profileName!!)
            } else {
                Log.d("string","Nieprawidłowy format danych książki.")
            }
        }

        buttonSearch.setOnClickListener{
            getUser()
        }
        return view
    }
    private fun createExchange(title:String,author:String,owner:String,borrower:String) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val currentDate = Date()
            val date = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
            val dateString = date.format(currentDate)
            val exchange = ExchangeHistory(owner,borrower,title,author,dateString,"oczekuje",0)
            exchangeHistoryCollection.add(exchange)
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),"Wysłano ofertę pożyczenia książki",Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun getUser()= CoroutineScope(Dispatchers.IO).launch{
        try{
            val querySnapshot = accountCollection.whereEqualTo("username",profileName).get().await()
            for(document in querySnapshot){
                account= document.toObject()
            }
            searchNearbyUsers()
        }
        catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun searchNearbyUsers() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val currentUserLocation = Pair(account.latitude.toDouble(), account.longitude.toDouble())

            val querySnapshot = accountCollection.get().await()

            nearbyUsersList = mutableListOf()

            for (document in querySnapshot) {
                val user = document.toObject(Account::class.java)
                val userLocation = Pair(user.latitude.toDouble(), user.longitude.toDouble())

                val distance = calculateDistance(currentUserLocation, userLocation)

                if (distance <= 10.0) {
                    if(account.username!=user.username){
                        nearbyUsersList.add(user)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                for (nearbyUser in nearbyUsersList) {
                    Log.d("TAG", "Nearby User: ${nearbyUser.username}")
                    findRelatedBooks()
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun findRelatedBooks() = CoroutineScope(Dispatchers.IO).launch{
        try{
            foundBooks = mutableListOf()
            foundBooks.clear()
            for (user in nearbyUsersList) {
                val querySnapshot = bookCollection.whereEqualTo("owner", user.username)
                    .whereEqualTo("genre", selectedGenre)
                    .whereEqualTo("currentState","Posiadana").get().await()
                for (document in querySnapshot) {
                    val book = document.toObject<Book>()
                    foundBooks.add(book.toStringBorrow())
                }
            }
            withContext(Dispatchers.Main){
                listAdapterSearch.clear()
                listAdapterSearch.addAll(foundBooks)
                listAdapterSearch.notifyDataSetChanged()
            }

        }
        catch (e:Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun calculateDistance(location1: Pair<Double, Double>, location2: Pair<Double, Double>): Double {
        val lat1 = location1.first
        val lon1 = location1.second
        val lat2 = location2.first
        val lon2 = location2.second

        val dlat = lat2 - lat1
        val dlon = lon2 - lon1

        return Math.sqrt(dlat * dlat + dlon * dlon)
    }
}
