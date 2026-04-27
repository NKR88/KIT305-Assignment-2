package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ProductItemBinding
import au.edu.utas.kit305.tutorial05.databinding.ProductMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.ceil


val p_items = mutableListOf<Product>()

class  ProductSelect : AppCompatActivity() {
    private lateinit var ui: ProductMainBinding

    private lateinit var spaceId: String
    private lateinit var houseId: String
    private lateinit var roomId: String
    private var sqrM = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ProductMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        spaceId = intent.getStringExtra(SPACE_ID)!!
        houseId = intent.getStringExtra(HOUSE_ID)!! // null checking already handled
        roomId = intent.getStringExtra(ROOM_ID)!! // null checking already handled

        val spaceWidth = intent.getIntExtra("WIDTH", 0)
        val spaceHeight = intent.getIntExtra("HEIGHT", 0)
        val spaceType = intent.getStringExtra("TYPE")

        sqrM = ceil((spaceWidth/1000.0) * (spaceHeight/1000.0)).toInt()

        ui.myList.layoutManager = LinearLayoutManager(this)

        ui.myList.adapter = ProductAdapter(p_items)
        // obviously all AI but i made sure to try to understand it
        Thread {
            Log.d("DEBUG", "This is space type -> ${spaceType}")
            val products = ProductApi.fetchProducts(spaceType!!)
            var filtered = products

            if (spaceType == "Window") {
                filtered = products.filter {

                    // if window then these wont be null
                    val minH = it.min_height!!
                    val maxH = it.max_height!!
                    val minW = it.min_width!!
                    val maxW = it.max_width!!

                    spaceHeight in minH..maxH && (
                            spaceWidth in minW..maxW || (
                                spaceWidth > maxW &&
                                run {
                                    val minPanels = Math.ceil(spaceWidth.toDouble() / maxW).toInt()
                                    val maxPanels = Math.floor(spaceWidth.toDouble() / minW).toInt()
                                    minPanels <= maxPanels
                                }
                            ) || (
                                minW == maxW &&
                                spaceWidth % minW == 0
                            )
                    )
                }
            }

            runOnUiThread {
                p_items.clear()
                p_items.addAll(filtered)
                ui.myList.adapter?.notifyDataSetChanged()
            }
        }.start()

    }
    inner class ProductHolder(var ui: ProductItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class ProductAdapter(private val products: MutableList<Product>) : RecyclerView.Adapter<ProductHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSelect.ProductHolder {
            val ui = ProductItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return ProductHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return products.size
        }

        override fun onBindViewHolder(holder: ProductSelect.ProductHolder, position: Int) {
            val product = products[position]   //get the data at the requested position

            holder.ui.txtName.text = product.name
            holder.ui.txtDesc.text = product.description
            holder.ui.txtPrice.text = "$${product.price_per_sqm} per sqr metre"

            Glide.with(holder.ui.root.context)
                .load(product.imageUrl)
                .centerCrop()
                .into(holder.ui.imageView)

            // ai used made sure to ask it questions so i understand what is happenig
            val items = product.variants
            val spinnerAdapter = ArrayAdapter(
                holder.ui.root.context,
                android.R.layout.simple_spinner_item,
                items
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            holder.ui.spinnerColor.adapter = spinnerAdapter

            holder.ui.root.setOnClickListener {

                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val price = product.price_per_sqm * sqrM

                db.collection("houses")
                    .document(houseId)
                    .collection("rooms")
                    .document(roomId)
                    .collection("spaces")
                    .document(spaceId)
                    .update(
                        "s_product", product.id,
                        "s_variant", holder.ui.spinnerColor.selectedItem.toString(),
                        "s_price", price,
                        "s_product_n", product.name
                    )
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Space Prod has ${product.id} id")
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error updating Space Prod ID", it)
                    }
            }
        }
    }
}