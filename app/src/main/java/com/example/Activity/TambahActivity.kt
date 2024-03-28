package com.example.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Model.ModelSinopsisi
import com.example.myapplication.databinding.ActivityTambahBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TambahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahBinding

    private lateinit var Dreference: DatabaseReference
    private lateinit var Sreference: StorageReference

    private var selectedImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etRilisT.filters = arrayOf(InputFilter.LengthFilter(4))

        Dreference = FirebaseDatabase.getInstance().reference.child("Film")

        Sreference = FirebaseStorage.getInstance().reference.child("images")

        binding.btnImgT.setOnClickListener {
            openImgchoosser()
        }

        binding.btnKembaliT.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnSimpanT.setOnClickListener {
            save()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImgUri = data.data
            binding.ivResutlT.setImageURI(selectedImgUri)
        }
    }

    private fun openImgchoosser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun save() {
        val judul = binding.etJudulT.text.toString().trim()
        val sutradara = binding.etSutradaraT.text.toString().trim()
        val rilis = binding.etRilisT.text.toString().trim()
        val durasi = binding.etDurasiT.text.toString().trim()
        val genre = binding.etGenreT.text.toString().trim()
        val sinopsis = binding.etSinopsisT.text.toString().trim()

        if (judul.isEmpty()) {
            binding.etJudulT.error = "Wajib Diisi"
            binding.etJudulT.requestFocus()
            return
        }

        if (sutradara.isEmpty()) {
            binding.etSutradaraT.error = "Wajib Diisi"
            binding.etSutradaraT.requestFocus()
            return
        }

        if (rilis.length != 4 || rilis.isEmpty()) {
            binding.etRilisT.error = "Wajib Diisi dan maksimal 4 digit"
            binding.etRilisT.requestFocus()
            return
        }

        if (rilis.toInt() > Calendar.getInstance().get(Calendar.YEAR)) {
            binding.etRilisT.error = "Tahun tidak boleh lebih dari tahun sekarang"
            binding.etRilisT.requestFocus()
            return
        }

        if (durasi.isEmpty()) {
            binding.etDurasiT.error = "Wajib Diisi"
            binding.etDurasiT.requestFocus()
            return
        }

        if (genre.isEmpty()) {
            binding.etGenreT.error = "Wajib Diisi"
            binding.etGenreT.requestFocus()
            return
        }

        if (sinopsis.isEmpty()) {
            binding.etSinopsisT.error = "Wajib Diisi"
            binding.etSinopsisT.requestFocus()
            return
        }

        if (selectedImgUri == null) {
            showError("Pilih Gambar terlebih dahulu")
            return
        }

        val imageRef = Sreference.child(judul + "_" + System.currentTimeMillis() + ".jpg")
        val uploadTask = imageRef.putFile(selectedImgUri!!)

        uploadTask.addOnCompleteListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->

                val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.now()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))

                val filmId = Dreference.push().key
                val tgluplooad = formattedDate
                val film = ModelSinopsisi(filmId, tgluplooad , uri.toString(), judul, sutradara, rilis, durasi, genre, sinopsis )

                filmId?.let { Dreference.child(it).setValue(film) }
                Toast.makeText(applicationContext, "Berhasil Menambahkan Film : ${judul}", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Gagal Mengupload Gambar", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(applicationContext, "Terjadi Kesalahan Menambahkan Data", Toast.LENGTH_LONG).show()
        }
    }

    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}