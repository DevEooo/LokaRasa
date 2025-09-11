package com.example.lokarasa

data class ProductModel(
    var id: String = "",
    var productName: String = "",
    var price: Double = 0.0,
    var storeName: String = "",
    var phonenum: String = "",
    var description: String = "",
    var imgUrl: String = "",
    var quantity: Int = 1
)
