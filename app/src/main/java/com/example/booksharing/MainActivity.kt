package com.example.booksharing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.booksharing.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() { private lateinit var editTextEmail: EditText
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
            startActivity(Intent(this@MainActivity,AppActivity::class.java))
        }
        buttonRegister.setOnClickListener{
            startActivity(Intent(this@MainActivity,RegisterActivity::class.java))
        }
    }
}