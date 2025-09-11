package com.example.lokarasa

//noinspection SuspiciousImport
import android.annotation.SuppressLint
import com.example.lokarasa.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ganti sesuai nama package-mu

class Article : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articel)

        val FavIcons = findViewById<ImageView>(R.id.FavIcons)
        val Home = findViewById<ImageView>(R.id.Homepage)
        val ShopIcons = findViewById<ImageView>(R.id.ShopIcons)
        val Notes = findViewById<ImageView>(R.id.NotesIcon)

        FavIcons.setOnClickListener {
            startActivity(Intent(this, Favorite::class.java))
        }

        Home.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        ShopIcons.setOnClickListener {
            startActivity(Intent(this, OurProduct::class.java))
        }

        Notes.setOnClickListener {
            startActivity(Intent(this, AddForm::class.java))
        }
    }
}