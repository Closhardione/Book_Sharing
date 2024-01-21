package com.example.booksharing.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.booksharing.MainActivity
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.Account
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
//zmień wpisaywane dane, dodaj nr telefonu i lokalizację
class RegisterActivity : AppCompatActivity() {
    private val accountCollection = Firebase.firestore.collection("accounts")
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPasswordRepeat: EditText
    private lateinit var buttonRegister: Button
    private lateinit var buttonReturn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonReturn = findViewById(R.id.buttonReturn)

        buttonRegister.setOnClickListener{
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val passwordRepeat = editTextPasswordRepeat.text.toString()
            if(password!=passwordRepeat){
                Toast.makeText(this@RegisterActivity,"Hasła muszą być identyczne!",Toast.LENGTH_LONG).show()
            }
            else{
                val account = Account(username,email,password)
                registerAccount(account)
            }
        }

        buttonReturn.setOnClickListener{
            startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
            finish()
        }
    }
    private fun registerAccount(account:Account) = CoroutineScope(Dispatchers.IO).launch {
        try{
            var counter = 0
            val querySnapshot = accountCollection.whereEqualTo("username",account.username).get().await()
            for(document in querySnapshot.documents){
                counter++
            }
            if(counter == 0){
                accountCollection.add(account).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterActivity,"Zarejestrowano!",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                    finish()
                }
            }
            else{
                throw Exception("Użyj innej nazwy użytkownika!")
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@RegisterActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }
}