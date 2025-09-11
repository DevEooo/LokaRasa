package com.example.lokarasa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddForm : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStoreName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDescription: EditText
    private lateinit var imgProduct: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnSave: Button

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference.child("product_images")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_tambah)

        val FavIcons = findViewById<ImageView>(R.id.FavIcons)
        val Home = findViewById<ImageView>(R.id.Homepage)
        val ShopIcons = findViewById<ImageView>(R.id.ShopIcons)
        val Notes = findViewById<ImageView>(R.id.NotesIcon)

        etProductName = findViewById(R.id.etNamaProduk)
        etPrice = findViewById(R.id.etHargaProduk)
        etStoreName = findViewById(R.id.etNamaToko)
        etPhone = findViewById(R.id.etNoTelepon)
        etDescription = findViewById(R.id.etKeterangan)
        imgProduct = findViewById(R.id.previewImage)
        btnChooseImage = findViewById(R.id.btnUploadGambar)
        btnSave = findViewById(R.id.btnSimpan)

        btnChooseImage.setOnClickListener {
            openFileChooser()
        }

        btnSave.setOnClickListener {
            saveProduct()
        }

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
            startActivity(Intent(this, Article::class.java))
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null) {
            imageUri = data.data
            imgProduct.setImageURI(imageUri)
        }
    }

    private fun saveProduct() {
        val name = etProductName.text.toString()
        val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val store = etStoreName.text.toString()
        val phone = etPhone.text.toString()
        val desc = etDescription.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please fill product name", Toast.LENGTH_SHORT).show()
            return
        }

        val id = UUID.randomUUID().toString()

        if (imageUri != null) {
            val fileRef = storageRef.child("$id.jpg")

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        saveToFirestore(id, name, price, store, phone, desc, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No image selected → save with empty URL
            saveToFirestore(id, name, price, store, phone, desc, "")
        }
    }

    private fun saveToFirestore(
        id: String,
        name: String,
        price: Double,
        store: String,
        phone: String,
        desc: String,
        imgUrl: String
    ) {
        val product = ProductModel(
            id = id,
            productName = name,
            price = price,
            storeName = store,
            phonenum = phone,
            description = desc,
            imgUrl = imgUrl
        )

        firestore.collection("products")
            .document(id)
            .set(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
