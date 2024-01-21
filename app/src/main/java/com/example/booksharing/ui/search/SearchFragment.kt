package com.example.booksharing.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.booksharing.R

class SearchFragment : Fragment() {
    private lateinit var textViewChose:TextView
    private lateinit var spinnerGenre: Spinner
    private lateinit var buttonSearch: Button
    private lateinit var listViewBooks: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private var selectedGenre: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search,container,false)
        textViewChose = view.findViewById(R.id.textViewChose)
        buttonSearch = view.findViewById(R.id.buttonSearch)
        listViewBooks = view.findViewById(R.id.listViewBooks)
        listAdapter = ArrayAdapter(this.requireContext(), R.layout.find_books_item, mutableListOf())
        listViewBooks.adapter = listAdapter

        spinnerGenre = view.findViewById(R.id.spinnerGenre)
        val genres = arrayOf("Fantasy", "Science Fiction", "Romance", "Mystery", "Thriller", "Non-fiction")
        val adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, genres)
        spinnerGenre.adapter = adapter

        spinnerGenre.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedGenre = genres[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        return view
    }
}