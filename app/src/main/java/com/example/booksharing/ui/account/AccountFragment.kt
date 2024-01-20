package com.example.booksharing.ui.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.booksharing.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AccountFragment : Fragment() {
    //private val bookCollection = Firebase.firestore.collection("books")
    private lateinit var usernameTextView: TextView
    private lateinit var buttonAddBook: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        buttonAddBook = view.findViewById(R.id.buttonAddBook)

        val profileName = arguments?.getString("profileName")
        if (profileName != null) {
            Log.d("TAG", profileName)
        }
        else{
            Log.e("tag","Error accessing username!")
        }
        buttonAddBook.setOnClickListener{

        }
        usernameTextView = view.findViewById(R.id.textViewUsername)
        usernameTextView.setText(profileName)
        return view
    }


}