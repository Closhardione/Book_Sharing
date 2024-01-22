package com.example.booksharing.ui.borrowed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.booksharing.R
import com.example.booksharing.adapter.CustomAdapterSecond
import com.example.booksharing.ui.firebase_data.ExchangeHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class BorrowedFragment : Fragment() {
    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")
    private lateinit var textViewBorrowed: TextView
    private lateinit var listViewBorrowed: ListView
    private lateinit var foundExchanges: MutableList<ExchangeHistory>
    private lateinit var listAdapterBorrowed: CustomAdapterSecond
    private var profileName:String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_borrowed, container, false)
        profileName = arguments?.getString("username")
        if (profileName != null) {
            Log.d("TAG", profileName!!)
        }
        textViewBorrowed= view.findViewById(R.id.textViewBorrowed)
        listViewBorrowed = view.findViewById(R.id.listViewBorrowed)
        listAdapterBorrowed = CustomAdapterSecond(requireContext(), R.layout.list_item_layout2, mutableListOf())
        listViewBorrowed.adapter = listAdapterBorrowed

        showExchanges()
        return view
    }
    private fun showExchanges() = CoroutineScope(Dispatchers.IO).launch{
        try {
            foundExchanges = mutableListOf()
            val querySnapshot = exchangeHistoryCollection.whereEqualTo("bookOwner",profileName)
                .whereEqualTo("state","pożyczona").get().await()
            for(document in querySnapshot){
                val exchange = document.toObject<ExchangeHistory>()
                foundExchanges.add(exchange)
            }
            withContext(Dispatchers.Main) {
                listAdapterBorrowed.clear()
                listAdapterBorrowed.addAll(foundExchanges)
                listAdapterBorrowed.notifyDataSetChanged()
            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}