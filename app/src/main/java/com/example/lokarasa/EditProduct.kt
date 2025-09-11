package com.example.lokarasa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditProduct : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_produk)

        val FavIcons = findViewById<ImageView>(R.id.FavIcons)
        val Home = findViewById<ImageView>(R.id.Homepage)
        val ShopIcons = findViewById<ImageView>(R.id.ShopIcons)
        val Notes = findViewById<ImageView>(R.id.NotesIcon)

        val etJumlahProduk = findViewById<EditText>(R.id.etJumlahProduk)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnBatal)
        val btnIncrement = findViewById<Button>(R.id.btnIncrement)
        val btnDecrement = findViewById<Button>(R.id.btnDecrement)

        db = FirebaseFirestore.getInstance()

        // Ambil data awal dari intent
        val productId = intent.getStringExtra("productId") ?: ""
        val currentQty = intent.getIntExtra("current_quantity", 1)

        etJumlahProduk.setText(currentQty.toString())

        // Increment / Decrement
        btnIncrement.setOnClickListener {
            val current = etJumlahProduk.text.toString().toIntOrNull() ?: 1
            etJumlahProduk.setText((current + 1).toString())
        }

        btnDecrement.setOnClickListener {
            val current = etJumlahProduk.text.toString().toIntOrNull() ?: 1
            if (current > 1) etJumlahProduk.setText((current - 1).toString())
        }

        // Save changes
        btnSave.setOnClickListener {
            val input = etJumlahProduk.text.toString()
            val newQty = input.toIntOrNull()

            if (newQty == null || newQty <= 0) {
                Toast.makeText(this, "Jumlah produk harus lebih dari 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (productId.isNotEmpty()) {
                // Update quantity in products collection
                db.collection("products").document(productId)
                    .update("quantity", newQty)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Jumlah produk diperbarui!", Toast.LENGTH_SHORT).show()

                        // Also update the quantity in favorites if exists
                        db.collection("favorites")
                            .whereEqualTo("id", productId)
                            .get()
                            .addOnSuccessListener { result ->
                                for (doc in result) {
                                    db.collection("favorites").document(doc.id)
                                        .update("quantity", newQty)
                                }
                            }

                        val resultIntent = Intent()
                        resultIntent.putExtra("productId", productId)
                        resultIntent.putExtra("new_quantity", newQty)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal update: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Product ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener { finish() }

        // Navigasi menu bawah
        FavIcons.setOnClickListener { startActivity(Intent(this, Favorite::class.java)) }
        Home.setOnClickListener { startActivity(Intent(this, Home::class.java)) }
        ShopIcons.setOnClickListener { startActivity(Intent(this, OurProduct::class.java)) }
        Notes.setOnClickListener { startActivity(Intent(this, AddForm::class.java)) }
    }
}
