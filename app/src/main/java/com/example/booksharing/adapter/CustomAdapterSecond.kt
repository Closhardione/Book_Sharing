package com.example.booksharing.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.ExchangeHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CustomAdapterSecond(context: Context, private val resource: Int, private val data: List<ExchangeHistory>) : ArrayAdapter<ExchangeHistory>(context, resource, data) {

    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")
    private val booksCollection = Firebase.firestore.collection("books")
    private var selectedGrade: String? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)

            viewHolder = ViewHolder()
            viewHolder.textView = view.findViewById(R.id.textViewItem)
            viewHolder.buttonEnd = view.findViewById(R.id.buttonEnd)
            viewHolder.spinnerEnd = view.findViewById(R.id.spinnerEnd)
            val grades = arrayOf("1","2","3","4","5")
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, grades)
            viewHolder.spinnerEnd.adapter = adapter

            viewHolder.spinnerEnd.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedGrade = grades[position]

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })

            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val exchangeHistory = getItem(position)

        viewHolder.textView.text = exchangeHistory?.getExchangeDescription()
        viewHolder.buttonEnd.setOnClickListener {
            exchangeHistory?.let { endExchange(it) }
        }

        return view
    }
    private fun endExchange(exchangeHistory: ExchangeHistory) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var querySnapshot = exchangeHistoryCollection
                    .whereEqualTo("title", exchangeHistory.title)
                    .whereEqualTo("author", exchangeHistory.author)
                    .whereEqualTo("date", exchangeHistory.date)
                    .whereEqualTo("bookOwner", exchangeHistory.bookOwner)
                    .whereEqualTo("bookborrower", exchangeHistory.bookborrower).get().await()
                for (document in querySnapshot) {
                    exchangeHistoryCollection.document(document.id).update("state", "zakończona")
                    exchangeHistoryCollection.document(document.id).update("ownerGrade", selectedGrade.toString().toInt())
                }
                querySnapshot = booksCollection.whereEqualTo("title",exchangeHistory.title)
                    .whereEqualTo("author",exchangeHistory.author)
                    .whereEqualTo("owner",exchangeHistory.bookOwner).get().await()
                for(document in querySnapshot){
                    booksCollection.document(document.id).update("currentState","Posiadana")
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(context,"Zakończono wymianę!", Toast.LENGTH_SHORT).show()
                }

            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    private class ViewHolder {
        lateinit var textView: TextView
        lateinit var buttonEnd: Button
        lateinit var spinnerEnd:Spinner
    }
}
