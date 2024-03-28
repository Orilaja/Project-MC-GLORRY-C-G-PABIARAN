package com.example.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var Fauth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Fauth = FirebaseAuth.getInstance()

        binding.btnSingup.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvforget.setOnClickListener {
            val intent = Intent(this,ForgetActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            signinfunction()
        }
    }

    private fun signinfunction() {
        val email = binding.inputEmailL.text.toString().trim()
        val pass = binding.inputPassL.text.toString().trim()

        if (email.isEmpty()){
            binding.inputEmailL.error = "Email tidak boleh kosong"
            binding.inputEmailL.requestFocus()
            return

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.inputEmailL.error = "Email tidak valid"
            binding.inputEmailL.requestFocus()
            return

        } else if (pass.isEmpty()){
            binding.inputPassL.error = "Password tidak boleh kosong"
            binding.inputPassL.requestFocus()
            return
        } else {
            loginUser(email,pass)
        }
    }

    private fun loginUser(email: String, pass: String) {
        Fauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener (this){ task ->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Welcome '_'",Toast.LENGTH_LONG).show()
                Intent(this, MainActivity::class.java).also{
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            } else {
                Toast.makeText(applicationContext, "Email atau Password Salah",Toast.LENGTH_LONG).show()
            }
        }
    }

}