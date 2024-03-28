package com.example.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.Model.ModelSinopsisi
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDetailBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DetailActivity : AppCompatActivity() {

    private lateinit var Dreference: DatabaseReference
    private lateinit var binding: ActivityDetailBinding

    private val listFilm: ArrayList<ModelSinopsisi> = arrayListOf<ModelSinopsisi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setValueDetail()

        binding.ivDback.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.icEdit.setOnClickListener {
            val filmId = intent.getStringExtra("id").toString()
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("id", filmId)
            startActivityForResult(intent, EDIT_ACTIVITY_REQUEST_CODE)
        }

        binding.icHapus.setOnClickListener {
            hapusFilm()
        }
    }

    companion object {
        const val EDIT_ACTIVITY_REQUEST_CODE = 1
    }

    private fun setValueDetail() {
        Glide.with(this)
            .load(intent.getStringExtra("imgUrl"))
            .placeholder(R.drawable.image_default)
            .into(binding.ivDfilm)
        Glide.with(this)
            .load(intent.getStringExtra("imgUrl"))
            .placeholder(R.drawable.image_default)
            .into(binding.ivDfilm2)
        binding.tvDJudul.text = intent.getStringExtra("judul")
        binding.tvDsutradara.text = intent.getStringExtra("sutradara")
        binding.tvDtahun.text = intent.getStringExtra("tahun")
        binding.tvDdurasi.text = intent.getStringExtra("durasi")
        binding.tvDgenre.text = intent.getStringExtra("genre")
        binding.tvDsinopsis.text = intent.getStringExtra("sinopsis")
    }


    private fun hapusFilm() {
        val filmId = intent.getStringExtra("id").toString()
        val imageurl = intent.getStringExtra("imgUrl").toString()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus item ini?")
        builder.setPositiveButton("Ya") { dialog, which ->
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageurl)
            storageReference.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Gambar Berhasil Dihapus", Toast.LENGTH_SHORT).show()

                    Dreference = FirebaseDatabase.getInstance().getReference("Film").child(filmId)
                    val mTask = Dreference.removeValue()

                    mTask.addOnSuccessListener {
                        Toast.makeText(this, "DAta Film Berhasil dihapus", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                        .addOnFailureListener { fail ->
                            val refreshDialog = AlertDialog.Builder(this)
                            refreshDialog.setTitle("Gagal Menghapus Data")
                            refreshDialog.setMessage("Terjadi Kesalahan pada saat ingin menghapus data film")
                            refreshDialog.setPositiveButton("Ya") { dialog, which ->
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            refreshDialog.setNegativeButton("Tidak") { dialog, which ->
//                                Batal Menghapus Data
                            }
                            refreshDialog.show()
                        }
                }
                .addOnFailureListener {
                    val refreshDialog = AlertDialog.Builder(this)
                    refreshDialog.setTitle("Gagal Menghapus Gambar")
                    refreshDialog.setMessage("Gagal Menghapus Gambar. Coba lagi?")
                    refreshDialog.setPositiveButton("Ya") { dialog, which ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    refreshDialog.setNegativeButton("Tidak") { dialog, which ->
//                                Batal Menghapus Data
                    }
                    refreshDialog.show()
                }
        }
            builder.setNegativeButton("Tidak"){dialog, which ->
//                Batal Penghapusan
            }
        builder.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updateJudul = data?.getStringExtra("judul")
            val updateSutradara = data?.getStringExtra("sutradara")
            val updateTahun = data?.getStringExtra("tahun")
            val updateDurasi = data?.getStringExtra("durasi")
            val updateSinopsis = data?.getStringExtra("sinopsis")
            val updateGenre = data?.getStringExtra("genre")
            val updatetgl = data?.getStringExtra("tglupload")
            val updateImgUrl = data?.getStringExtra("imgUrl")

            binding.tvDJudul.text = updateJudul
            binding.tvDsutradara.text = updateSutradara
            binding.tvDtahun.text = updateTahun
            binding.tvDdurasi.text = updateDurasi
            binding.tvDgenre.text = updateGenre
            binding.tvDsinopsis.text = updateSinopsis
            Glide.with(this)
                .load(updateImgUrl)
                .placeholder(R.drawable.image_default)
                .into(binding.ivDfilm)
            Glide.with(this)
                .load(updateImgUrl)
                .placeholder(R.drawable.image_default)
                .into(binding.ivDfilm2)
        }
    }
}