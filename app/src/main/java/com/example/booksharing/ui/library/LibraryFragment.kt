package com.example.booksharing.ui.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LibraryFragment : Fragment() {
    private val bookCollection = Firebase.firestore.collection("books")
    private lateinit var textViewLibrary: TextView
    private lateinit var listViewBooks: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private var profileName:String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_library,container,false)
        profileName = arguments?.getString("username")
        textViewLibrary = view.findViewById(R.id.textViewLibrary)
        listViewBooks = view.findViewById(R.id.listViewBooks)
        listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listViewBooks.adapter = listAdapter
        if (profileName != null) {
            Log.d("TAG", profileName!!)
            getBookList()
        }
        else{
            Log.e("tag","Error accessing username!")
        }


        return view
    }
    private fun getBookList() = CoroutineScope(Dispatchers.IO).launch{
        try{
            val bookList = mutableListOf<String>()
            if (profileName != null) {
                Log.d("TAG", profileName!!)
            }
            else{
                Log.e("tag","Error accessing username!")
            }
            val querySnapshot = bookCollection.whereEqualTo("owner",profileName).get().await()
            for(document in querySnapshot){
                val book  = document.toObject<Book>()
                bookList.add(book.toStringOwned())
            }
            withContext(Dispatchers.Main){
                listAdapter.clear()
                listAdapter.addAll(bookList)
                listAdapter.notifyDataSetChanged()
            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(activity,e.message, Toast.LENGTH_LONG).show()
                Log.e("tag",e.message.toString())
            }
        }
    }

}
