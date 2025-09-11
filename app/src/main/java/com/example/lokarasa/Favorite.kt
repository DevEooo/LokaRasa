package com.example.lokarasa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class Favorite : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var container: LinearLayout
    private lateinit var jumlahBarang: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorit)

        db = FirebaseFirestore.getInstance()

        // container tempat produk ditambahkan
        container = findViewById(R.id.favoriteContainer)
        jumlahBarang = findViewById(R.id.jumlahBarang)

        // bottom nav
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

        // load favorites
        loadFavorites()
    }

    @SuppressLint("MissingInflatedId")
    private fun loadFavorites() {
        val listContainer = findViewById<LinearLayout>(R.id.favoriteContainer)

        db.collection("favorites").get().addOnSuccessListener { result ->
            var count = 0
            listContainer.removeAllViews() // clear old cards

            for (doc in result) {
                val product = doc.toObject(ProductModel::class.java)
                count++

                // inflate fresh card for each favorite
                val cardView = LayoutInflater.from(this)
                    .inflate(R.layout.item_favorite_product, listContainer, false)

                val img = cardView.findViewById<ImageView>(R.id.productImage)
                val title = cardView.findViewById<TextView>(R.id.productTitle)
                val price = cardView.findViewById<TextView>(R.id.productPrice)
                val qty = cardView.findViewById<TextView>(R.id.productQty)
                val btnEdit = cardView.findViewById<Button>(R.id.btnEdit)
                val btnDelete = cardView.findViewById<Button>(R.id.btnDelete)

                title.text = product.productName
                price.text = "Rp ${product.price}"
                qty.text = "Jumlah: ${if (product.quantity > 0) product.quantity else 1}"
                Glide.with(this).load(product.imgUrl).into(img)

                // open edit activity
                btnEdit.setOnClickListener {
                    if (product.id.isNotEmpty()) {
                        val intent = Intent(this, EditProduct::class.java)
                        intent.putExtra("productId", product.id)
                        intent.putExtra("current_quantity", 1) // or get actual quantity if saved
                        val REQUEST_CODE_EDIT = 1001
                        startActivityForResult(intent, REQUEST_CODE_EDIT)
                    } else {
                        Toast.makeText(this, "Product ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }

                // delete favorite
                btnDelete.setOnClickListener {
                    db.collection("favorites").document(doc.id).delete()
                    listContainer.removeView(cardView)
                    jumlahBarang.text = "Jumlah Barang = ${--count}"
                }

                // finally, add card only once
                listContainer.addView(cardView)
            }

            jumlahBarang.text = "Jumlah Barang = $count"
        }
    }
}

