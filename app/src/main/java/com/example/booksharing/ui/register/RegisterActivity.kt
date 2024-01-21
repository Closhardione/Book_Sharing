package com.example.booksharing.ui.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.booksharing.MainActivity
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.Account
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPasswordRepeat: EditText
    private lateinit var buttonRegister: Button
    private lateinit var buttonReturn: Button
    private var latitudeValue = ""
    private var longitudeValue = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonReturn = findViewById(R.id.buttonReturn)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        buttonRegister.setOnClickListener{
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val passwordRepeat = editTextPasswordRepeat.text.toString()
            getLocation(username, email, password, passwordRepeat)
        }

        buttonReturn.setOnClickListener{
            startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
            finish()
        }
    }
    private fun getLocation(username:String,email:String,password:String,passwordRepeat:String){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitudeValue = location.latitude.toString()
                longitudeValue = location.longitude.toString()
            }
            Log.e("tag", latitudeValue)
            Log.e("tag", longitudeValue)

            if (password != passwordRepeat) {
                Toast.makeText(this@RegisterActivity, "Hasła muszą być identyczne!", Toast.LENGTH_LONG).show()
            } else {
                Log.e("tag", latitudeValue)
                Log.e("tag", longitudeValue)
                val account = Account(username, email, password, latitudeValue, longitudeValue)
                registerAccount(account)
            }
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
                    // finish()
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