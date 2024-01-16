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

class RegisterActivity : AppCompatActivity() {
    private val accountCollection = Firebase.firestore.collection("accounts")
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPasswordRepeat: EditText
    private lateinit var buttonRegister: Button
    private lateinit var buttonReturn: Button
    private var foundId: Boolean = false
    private var id: Int = 1;
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
                findAvailableId(id)
                while(foundId){
                    id++
                    findAvailableId(id)
                }
                val account = Account(id,username,email,password)
                registerAccount(account)
                startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                finish()
            }
        }

        buttonReturn.setOnClickListener{
            startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
            finish()
        }
    }
    private fun findAvailableId(id:Int) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = accountCollection.whereEqualTo("id",id).get().await()
            var counter = 0
            for(document in querySnapshot.documents){
                counter++
            }
            if (counter>0){
                foundId = true
            }
        }
        catch(e:Exception) {foundId=false}
    }
    private fun registerAccount(account:Account) = CoroutineScope(Dispatchers.IO).launch {
        try{
            accountCollection.add(account).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@RegisterActivity,"Zarejestrowano!",Toast.LENGTH_SHORT).show()
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@RegisterActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }
}