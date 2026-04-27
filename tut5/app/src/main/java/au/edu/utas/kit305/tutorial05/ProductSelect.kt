package au.edu.utas.kit305.tutorial05

import android.content.Intent
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


val p_items = mutableListOf<Product>()

class  ProductSelect : AppCompatActivity() {
    private lateinit var ui: ProductMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ProductMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.myList.layoutManager = LinearLayoutManager(this)

        ui.myList.adapter = ProductAdapter(p_items)
        // obviously all AI but i made sure to try to understand it
        Thread {
            val products = ProductApi.fetchProducts()

            runOnUiThread {
                p_items.clear()
                p_items.addAll(products)
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
                val i = Intent(holder.ui.root.context, MainActivity3::class.java)
                startActivity(i)
                true
            }
        }
    }
}