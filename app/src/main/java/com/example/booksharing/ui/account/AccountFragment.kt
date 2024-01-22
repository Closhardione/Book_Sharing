package com.example.booksharing.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.booksharing.AddBookActivity
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.ExchangeHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class AccountFragment : Fragment() {
    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")
    private lateinit var usernameTextView: TextView
    private lateinit var textViewGrade: TextView
    private lateinit var buttonAddBook: Button
    private lateinit var listViewExchange: ListView
    private lateinit var listAdapterAccount: ArrayAdapter<String>
    private lateinit var foundExchanges: MutableList<String>
    private var profileName:String? = null
    private var finalGrade:Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        buttonAddBook = view.findViewById(R.id.buttonAddBook)
        usernameTextView = view.findViewById(R.id.textViewUsername)
        textViewGrade = view.findViewById(R.id.textViewGrade)

        listViewExchange = view.findViewById(R.id.listViewExchanges)
        listAdapterAccount = ArrayAdapter(this.requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listViewExchange.adapter = listAdapterAccount

        profileName = arguments?.getString("username")
        if (profileName != null) {
            Log.d("TAG", profileName!!)
            usernameTextView.text = profileName
        }
        else{
            Log.e("tag","Error accessing username!")
        }
        showExchanges()
        buttonAddBook.setOnClickListener{
            val intent = Intent(activity, AddBookActivity::class.java)
            intent.putExtra("username", profileName)
            startActivity(intent)
        }
        return view
    }
    private fun showExchanges() = CoroutineScope(Dispatchers.IO).launch{
        try {
            foundExchanges = mutableListOf()
            var counter =0
            val querySnapshot = exchangeHistoryCollection.whereEqualTo("bookborrower",profileName)
                .whereEqualTo("state","zakończona").get().await()
            for(document in querySnapshot){
                val exchange = document.toObject<ExchangeHistory>()
                foundExchanges.add(exchange.getExchangeEndDescription())
                counter++
                finalGrade+=exchange.ownerGrade
                Log.e("tag",exchange.getExchangeEndDescription())
            }
            finalGrade /= counter
            val decimalFormat = DecimalFormat("#.#")
            val formattedGrade = decimalFormat.format(finalGrade)
            withContext(Dispatchers.Main) {
                if(finalGrade.isNaN()){
                    textViewGrade.text = "Średnia ocena użytkowników: brak danych"
                    listAdapterAccount.clear()
                    listAdapterAccount.addAll(foundExchanges)
                    listAdapterAccount.notifyDataSetChanged()
                }
                else{
                    textViewGrade.text = "Średnia ocena użytkowników: $formattedGrade"
                    listAdapterAccount.clear()
                    listAdapterAccount.addAll(foundExchanges)
                    listAdapterAccount.notifyDataSetChanged()
                }

            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}