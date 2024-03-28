package com.example.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityEditBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class EditActivity : AppCompatActivity() {

    private lateinit var filmId: String
    private lateinit var binding: ActivityEditBinding

    private lateinit var Dreference: DatabaseReference
    private lateinit var Sreference: StorageReference

    private var selectedUri: Uri? = null
    private var oldImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Sreference = FirebaseStorage.getInstance().reference.child("images")

        binding.etRilisE.filters = arrayOf(InputFilter.LengthFilter(4))

        filmId = intent.getStringExtra("id").toString()
        if (filmId.isNotEmpty()) {
            fetchFilm(filmId)
        } else {
            showDataNotFoundDialog()
        }

        binding.btnUpdateE.setOnClickListener {
            UpdateItem()
        }

        binding.btnImgE.setOnClickListener {
            uploadImageChooser()
        }

        binding.btnKembaliT.setOnClickListener {
            finish()
        }

    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private fun uploadImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedUri = data.data
            Glide.with(this)
                .load(selectedUri)
                .into(binding.ivResutlE)
        }
    }

    private fun fetchFilm(filmId: String) {
        Dreference = FirebaseDatabase.getInstance().getReference("Film").child(filmId)
        Dreference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val judul = snapshot.child("judul").getValue(String::class.java)
                    val sutradara = snapshot.child("sutradara").getValue(String::class.java)
                    val sinopsisi = snapshot.child("sinopsis").getValue(String::class.java)
                    val durasi = snapshot.child("durasi").getValue(String::class.java)
                    val tahun = snapshot.child("tahun").getValue(String::class.java)
                    val imgUrl = snapshot.child("imgUrl").getValue(String::class.java)
                    val genre = snapshot.child("genre").getValue(String::class.java)

                    binding.etJudulE.setText(judul)
                    binding.etSinopsisE.setText(sinopsisi)
                    binding.etSutradaraE.setText(sutradara)
                    binding.etDurasiE.setText(durasi)
                    binding.etRilisE.setText(tahun)
                    binding.etGenreE.setText(genre)
                    oldImageUrl = imgUrl
                    imgUrl?.let {
                        Glide.with(this@EditActivity)
                            .load(it)
                            .into(binding.ivResutlE)
                    }
                } else {
                    showDataNotFoundDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showDataNotFoundDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Data Tidak Ditemukan")
        builder.setMessage("Maaf,  data tidak ditemukan")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            onBackPressed()
        }
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun UpdateItem() {
        val newJudul = binding.etJudulE.text.toString().trim()
        val newDurasi = binding.etDurasiE.text.toString().trim()
        val newTahun = binding.etRilisE.text.toString().trim()
        val newSutradara = binding.etSutradaraE.text.toString().trim()
        val newSinopsis = binding.etSinopsisE.text.toString().trim()
        val newGenre = binding.etGenreE.text.toString().trim()

        if (newJudul.isEmpty()) {
            binding.etJudulE.error = "Wajib Diisi"
            binding.etJudulE.requestFocus()
            return

        } else if (newSutradara.isEmpty()) {
            binding.etSutradaraE.error = "Wajib Diisi"
            binding.etSutradaraE.requestFocus()
            return

        } else if (newTahun.length != 4 || newTahun.isEmpty()) {
            binding.etRilisE.error = "Wajib Diisi dan maksimal 4 digit"
            binding.etRilisE.requestFocus()
            return

        } else if (newTahun.toInt() > Calendar.getInstance().get(Calendar.YEAR)) {
            binding.etRilisE.error = "Tahun tidak boleh lebih dari tahun sekarang"
            binding.etRilisE.requestFocus()
            return

        } else if (newDurasi.isEmpty()) {
            binding.etDurasiE.error = "Wajib Diisi"
            binding.etDurasiE.requestFocus()
            return

        } else if (newGenre.isEmpty()) {
            binding.etGenreE.error = "Wajib Diisi"
            binding.etGenreE.requestFocus()
            return

        } else if (newSinopsis.isEmpty()) {
            binding.etSinopsisE.error = "Wajib Diisi"
            binding.etSinopsisE.requestFocus()
            return

        } else if (selectedUri != null) {
            val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            val tgluplooad = formattedDate
            uploadNewImageAndData(
                tgluplooad,
                newJudul,
                newSutradara,
                newTahun,
                newDurasi,
                newGenre,
                newSinopsis
            )
        } else {
            val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            val tglupload = formattedDate
            updatDataOnly(
                tglupload,
                newJudul,
                newSutradara,
                newTahun,
                newDurasi,
                newGenre,
                newSinopsis
            )
        }
    }

    private fun uploadNewImageAndData(
        tglupload: String?,
        judul: String,
        sutradara: String,
        tahun: String,
        durasi: String,
        genre: String,
        sinopsis: String
    ) {
        val imageRef = Sreference.child(judul + "_" + System.currentTimeMillis() + ".jpg")
        val uploadTask = imageRef.putFile(selectedUri!!)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val downloadUri = task.result
                val imageurl = downloadUri.toString()

                deleteOldImage(oldImageUrl)
                updateDatafilm(
                    tglupload,
                    judul,
                    sutradara,
                    tahun,
                    durasi,
                    genre,
                    sinopsis,
                    imageurl
                )

            } else {
                Toast.makeText(this, "Gagal Mengupload Gambar", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun updatDataOnly(
        tglupload: String?,
        judul: String,
        sutradara: String,
        tahun: String,
        durasi: String,
        genre: String,
        sinopsis: String
    ) {
        updateDatafilm(tglupload, judul, sutradara, tahun, durasi, genre, sinopsis, oldImageUrl)
    }

    private fun updateDatafilm(
        tglupload: String?,
        judul: String,
        sutradara: String,
        tahun: String,
        durasi: String,
        genre: String,
        sinopsis: String,
        imageUrl: String?
    ) {
        val updateData = HashMap<String, Any>()
        tglupload?.let { updateData["tglupload"] = it }
        updateData["judul"] = judul
        updateData["sutradara"] = sutradara
        updateData["tahun"] = tahun
        updateData["durasi"] = durasi
        updateData["genre"] = genre
        updateData["sinopsis"] = sinopsis
        imageUrl?.let { updateData["imgUrl"] = it }

        Dreference.updateChildren(updateData)
            .addOnCompleteListener {
                Toast.makeText(this, "Berhasil Mengupdate Data Film", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Mengupdate Data Film", Toast.LENGTH_SHORT).show()
            }

        val intent = Intent()
        intent.putExtra("judul", judul)
        intent.putExtra("sutradara", sutradara)
        intent.putExtra("tahun", tahun)
        intent.putExtra("durasi", durasi)
        intent.putExtra("sinopsis", sinopsis)
        intent.putExtra("genre", genre)
        intent.putExtra("tglupload", tglupload)
        intent.putExtra("imgUrl", imageUrl)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun deleteOldImage(imageUrl: String?) {
        if (imageUrl != null) {
            val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            oldImageRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Gambar Berhasil Di Hapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Menghapus Gambar", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}