package com.example.booksharing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.get
import com.example.booksharing.ui.firebase_data.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddBookActivity : AppCompatActivity() {
    private val booksCollection = Firebase.firestore.collection("books")
    private lateinit var editTextTitle: EditText
    private lateinit var editTextAuthor: EditText
    private lateinit var spinnerGenre: Spinner
    private lateinit var editTextDescription: EditText
    private lateinit var buttonAddBook: Button
    private var profileName: String? = null
    private var selectedGenre: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextAuthor = findViewById(R.id.editTextAuthor)
        spinnerGenre = findViewById<Spinner>(R.id.spinnerGenre)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonAddBook = findViewById(R.id.buttonAddBook)

        profileName = intent.getStringExtra("username")
        if (profileName.isNullOrEmpty()) {
            profileName = "DefaultUsername"
        }

        val genres = arrayOf("Fantasy", "Science Fiction", "Romance", "Mystery", "Thriller", "Non-fiction")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genres)
        spinnerGenre.adapter = adapter

        spinnerGenre.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Pobierz wybrany element
                selectedGenre = genres[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Wywoływane, gdy nic nie zostało wybrane
            }
        })

        buttonAddBook.setOnClickListener{
            val title = editTextTitle.text.toString()
            val author = editTextAuthor.text.toString()
            val genre = selectedGenre
            val description = editTextDescription.text.toString()
            val book = Book(title,author, genre!!,description, profileName!!,"Posiadana")
            addBook(book)
        }
        //Log.e("tag", profileName!!)
    }
    private fun addBook(book: Book) = CoroutineScope(Dispatchers.IO).launch{
        try{
            var counter = 0
            val querySnapshot = booksCollection
                .whereEqualTo("title",book.title)
                .whereEqualTo("author",book.author)
                .whereEqualTo("genre",book.genre)
                .whereEqualTo("description",book.description)
                .whereEqualTo("owner",book.owner).get().await()
            for(document in querySnapshot.documents){
                counter++
            }
            if(counter == 0){
                booksCollection.add(book).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@AddBookActivity,"Pomyślnie dodano książkę!",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else{
                throw Exception("Ta książka już istnieje w Twojej bibliotece!")
            }
        }
        catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@AddBookActivity,e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}