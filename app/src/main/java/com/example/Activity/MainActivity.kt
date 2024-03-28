package com.example.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.Adapter.ImageSliderAdapter
import com.example.Adapter.ItemAdapter
import com.example.Model.ModelImage
import com.example.Model.ModelSinopsisi
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var Adapter: ImageSliderAdapter
    private lateinit var dots: ArrayList<TextView>

    private var listFilm: ArrayList<ModelSinopsisi> = arrayListOf<ModelSinopsisi>()

    private val imageList: MutableList<ModelImage> = mutableListOf()
    private var currentPage = 0
    private val handler = Handler()

    private lateinit var Dreference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageList.add(ModelImage(R.drawable.f4))
        imageList.add(ModelImage(R.drawable.f2))
        imageList.add(ModelImage(R.drawable.f3))

        Adapter = ImageSliderAdapter(imageList)
        binding.viewPager.adapter = Adapter
        dots = ArrayList()
        setIndikator()

        val layoutManager = GridLayoutManager(this, 2)
        binding.RvMain.layoutManager = layoutManager
        binding.RvMain.setHasFixedSize(true)
        getFilmItem()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                seletedDot(position)
                super.onPageSelected(position)
            }
        })

        handler.postDelayed(autoSlideRunnable, 3000)

        binding.icTambah.setOnClickListener {
            Intent(this, TambahActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.icExit.setOnClickListener {
            exit()
        }

    }

    private fun exit() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Logout")
            setMessage("Apakah Anda Yakin ingin Logout?")
            setPositiveButton("Ya", DialogInterface.OnClickListener(){dialog, id ->
                FirebaseAuth.getInstance().signOut()
                navigateToLoginPage()
            })

            setNegativeButton("Batal", DialogInterface.OnClickListener(){dialog, id ->
                dialog.dismiss()
            })
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun navigateToLoginPage() {
        val currentuser = FirebaseAuth.getInstance().currentUser
        if (currentuser == null){
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun getFilmItem() {
        binding.RvMain.visibility = View.GONE
        binding.tvNoData.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        Dreference = FirebaseDatabase.getInstance().getReference("Film")

        Dreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listFilm.clear()
                if (snapshot.exists()) {
                    for (menuSnap in snapshot.children) {
                        val FilmData = menuSnap.getValue(ModelSinopsisi::class.java)
                        listFilm.add(FilmData!!)
                    }

                    if (listFilm.isNotEmpty()) {
                        // Tampilkan RecyclerView jika ada data
                        binding.RvMain.visibility = View.VISIBLE
                        binding.tvNoData.visibility = View.GONE
                    } else {
                        // Tampilkan teks "Tidak ada Film Yang tersedia" jika tidak ada data
                        binding.RvMain.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }

                    val itemAdapter = ItemAdapter(listFilm)
                    binding.RvMain.adapter = itemAdapter

                    itemAdapter.setOnItemClickListener(object : ItemAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra("id", listFilm[position].id)
                            intent.putExtra("judul", listFilm[position].judul)
                            intent.putExtra("sutradara", listFilm[position].sutradara)
                            intent.putExtra("tglupload", listFilm[position].tglupload)
                            intent.putExtra("durasi", listFilm[position].durasi)
                            intent.putExtra("genre", listFilm[position].genre)
                            intent.putExtra("imgUrl", listFilm[position].imgUrl)
                            intent.putExtra("tahun", listFilm[position].tahun)
                            intent.putExtra("sinopsis", listFilm[position].sinopsis)
                            startActivity(intent)
                        }
                    })

                    binding.progressBar.visibility = View.GONE
                } else {
                    // Tampilkan teks "Tidak ada Film Yang tersedia" jika snapshot tidak ada
                    binding.RvMain.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }


    private fun seletedDot(position: Int) {
        for (i in 0 until imageList.size) {
            if (i == position)
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.white))
            else
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun setIndikator() {
        for (i in 0 until imageList.size) {
            dots.add(TextView(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                dots[i].text = Html.fromHtml("&#9679")
            }
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    private val autoSlideRunnable: Runnable = object : Runnable {
        override fun run() {
            if (currentPage == imageList.size) {
                currentPage = 0
            }
            binding.viewPager.setCurrentItem(currentPage++, true)
            handler.postDelayed(this, 3000)
        }
    }

}