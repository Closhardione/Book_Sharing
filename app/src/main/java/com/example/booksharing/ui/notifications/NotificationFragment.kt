package com.example.booksharing.ui.notifications

import CustomAdapter
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
import com.example.booksharing.ui.firebase_data.ExchangeHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationFragment : Fragment() {
    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")
    private lateinit var textViewNotification:TextView
    private lateinit var listViewNotification: ListView
    private var profileName:String? = null
    private lateinit var foundExchanges: MutableList<ExchangeHistory>
    private lateinit var listAdapter: CustomAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        textViewNotification = view.findViewById(R.id.textViewNotification)
        profileName = arguments?.getString("username")
        if (profileName != null) {
            Log.d("TAG", profileName!!)
        }
        else{
            Log.e("tag","Error accessing username!")
        }
        listViewNotification = view.findViewById(R.id.listViewNotification)
        listAdapter = CustomAdapter(requireContext(), R.layout.list_item_layout, mutableListOf())
        listViewNotification.adapter = listAdapter
        showExchanges()

        return view
    }
    private fun showExchanges() = CoroutineScope(Dispatchers.IO).launch{
        try {
            foundExchanges = mutableListOf()
            val querySnapshot = exchangeHistoryCollection.whereEqualTo("bookOwner",profileName)
                .whereEqualTo("state","oczekuje").get().await()
            for(document in querySnapshot){
                val exchange = document.toObject<ExchangeHistory>()
                foundExchanges.add(exchange)
            }
            withContext(Dispatchers.Main) {
                listAdapter.clear()
                listAdapter.addAll(foundExchanges)
                listAdapter.notifyDataSetChanged()
            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
