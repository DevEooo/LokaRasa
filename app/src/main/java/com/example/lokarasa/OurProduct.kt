package com.example.lokarasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class OurProduct : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ourproduk)

        db = FirebaseFirestore.getInstance()

        // bottom nav icons
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

        // load products from Firestore into GridLayout
        loadProducts()
    }

    private fun loadProducts() {
        val gridLayout = findViewById<GridLayout>(R.id.gridProduk)
        gridLayout.removeAllViews() // clear previous cards

        db.collection("products").get().addOnSuccessListener { result ->
            for (doc in result) {
                val product = doc.toObject(ProductModel::class.java)

                // inflate card layout (fresh view every loop)
                val cardView = LayoutInflater.from(this)
                    .inflate(R.layout.item_card_produk, gridLayout, false)

                // bind views
                val favBtn = cardView.findViewById<ImageView>(R.id.btnFavorite)
                val img = cardView.findViewById<ImageView>(R.id.imgProduct)
                val name = cardView.findViewById<TextView>(R.id.txtProductName)
                val price = cardView.findViewById<TextView>(R.id.txtProductPrice)
                val store = cardView.findViewById<TextView>(R.id.txtStoreName)
                val phone = cardView.findViewById<TextView>(R.id.txtPhone)
                val desc = cardView.findViewById<TextView>(R.id.txtDescription)

                // set values
                name.text = product.productName
                price.text = "Rp ${product.price}"
                store.text = product.storeName
                phone.text = product.phonenum
                desc.text = product.description
                Glide.with(this).load(product.imgUrl).into(img)

                // favorite button
                favBtn.setOnClickListener {
                    val favData = hashMapOf(
                        "id" to product.id,
                        "productName" to product.productName,
                        "price" to product.price,
                        "storeName" to product.storeName,
                        "phonenum" to product.phonenum,
                        "description" to product.description,
                        "imgUrl" to product.imgUrl
                    )
                    db.collection("favorites").add(favData)
                        .addOnSuccessListener { favBtn.setImageResource(R.drawable.heart1) }
                        .addOnFailureListener { it.printStackTrace() }
                }

                // finally add to grid
                gridLayout.addView(cardView)
            }
        }.addOnFailureListener { it.printStackTrace() }
    }
}
