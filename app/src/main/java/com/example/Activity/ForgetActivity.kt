package com.example.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityForgetBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetBinding
    private lateinit var Fauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Fauth = FirebaseAuth.getInstance()

        binding.tvback.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnReset.setOnClickListener {
            forgetFunction()
        }
    }

    private fun forgetFunction() {
        val email = binding.inputEmailF.text.toString().trim()

        if (email.isEmpty()) {
            binding.inputEmailF.error = "Email tidak boleh Kosong"
            binding.inputEmailF.requestFocus()
            return

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmailF.error = "Email Tidak Valid"
            binding.inputEmailF.requestFocus()
            return

        } else {
            Fauth.sendPasswordResetEmail(email).addOnCompleteListener{ forgetTask ->
                if (forgetTask.isSuccessful){
                    Toast.makeText(applicationContext, "Silahkan Cek email : ${email} untuk reset", Toast.LENGTH_LONG).show()
                    Intent(this, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(applicationContext, "Terjadi Kesalahan : pada saat mereset password", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}