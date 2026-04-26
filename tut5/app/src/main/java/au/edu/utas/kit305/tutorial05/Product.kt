package au.edu.utas.kit305.tutorial05

import com.google.firebase.firestore.Exclude

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price_per_sqm: Int,
    val min_width: Int?,
    val max_width: Int?,
    val max_panels: Int?,
    val min_height: Int?,
    val max_height: Int?,
    val imageUrl: String,
    val category: String,
    val variants: List<String>
)