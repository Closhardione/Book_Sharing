package com.example.booksharing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booksharing.ui.firebase_data.Account
import com.example.booksharing.ui.register.RegisterActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val accountCollection = Firebase.firestore.collection("accounts")
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonRegister = findViewById(R.id.buttonRegister)

        buttonLogin.setOnClickListener{
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            loginUser(email, password)
        }

        buttonRegister.setOnClickListener{
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            var counter = 0
            val querySnapshot = accountCollection
                .whereEqualTo("email", email)
                .whereEqualTo("password", password).get().await()

            var sb= "TEST"
            for (document in querySnapshot.documents) {
                val account = document.toObject<Account>()
                sb = account?.username.toString()
                counter++
            }

            if (counter != 0) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Poprawnie zalogowano!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, AppActivity::class.java)
                    intent.putExtra("username", sb)
                    startActivity(intent)
                    finish()
                }
            } else {
                throw Exception("Nie znaleziono konta o podanym adresie email i haśle")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}