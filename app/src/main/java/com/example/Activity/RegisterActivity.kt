package com.example.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var Fauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Fauth = FirebaseAuth.getInstance()

        binding.tvlogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnRegister.setOnClickListener {
            signUpfunction()
        }

    }

    private fun signUpfunction() {
        val email = binding.inputEmailR.text.toString().trim()
        val pass = binding.inputPassR.text.toString().trim()
        val confirm = binding.inputConfirmR.text.toString().trim()

        if (email.isEmpty()) {
            binding.inputEmailR.error = "Email tidak boleh Kosong"
            binding.inputEmailR.requestFocus()
            return

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmailR.error = "Email Tidak Valid"
            binding.inputEmailR.requestFocus()
            return

        } else if (pass.isEmpty()) {
            binding.inputPassR.error = "Password tidak boleh Kosong"
            binding.inputPassR.requestFocus()
            return

        } else if (pass.length < 8) {
            binding.inputPassR.error = "Masukkan password minimal 8 Karakter"
            binding.inputPassR.requestFocus()
            return

        } else if (pass != confirm) {
            binding.inputConfirmR.error = "Password yang anda masukkan tidak valid"
            binding.inputConfirmR.requestFocus()
            return
        } else {
            registerUser(email, pass)
        }
    }

    @Suppress("DEPRECATION")
    private fun registerUser(email: String, pass: String) {
        Fauth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (result != null && result.signInMethods == null && result.signInMethods!!.isNotEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Alamat Email Telah Digunakan",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Fauth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { registerTask ->
                            if (registerTask.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Berhasil Mendaftarkan email : ${email}",
                                    Toast.LENGTH_LONG
                                ).show()
                                Intent(this, LoginActivity::class.java).also {
                                    it.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(it)
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Gagal Mendaftarkan ${email}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Terjadi kesalahan pada saat memeriksa email",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

}