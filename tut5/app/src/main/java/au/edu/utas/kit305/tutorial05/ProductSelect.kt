package au.edu.utas.kit305.tutorial05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ProductItemBinding
import au.edu.utas.kit305.tutorial05.databinding.ProductMainBinding

val p_items = mutableListOf<Product>()

class  ProductSelect : AppCompatActivity() {
    private lateinit var ui: ProductMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ProductMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.myList.layoutManager = LinearLayoutManager(this)

        ui.myList.adapter = ProductAdapter(p_items)

        Thread {
            try {
                val url = java.net.URL("https://utasbot.dev/kit305_2026/product")
                val connection = url.openConnection() as java.net.HttpURLConnection

                connection.requestMethod = "GET"
                connection.connect()

                val response = connection.inputStream.bufferedReader().readText()
                Log.d("API", response)

                val jsonObject = org.json.JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("data")

                val products = mutableListOf<Product>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val variantsArray = obj.getJSONArray("variants")
                    val variants = mutableListOf<String>()
                    for (j in 0 until variantsArray.length()) {
                        variants.add(variantsArray.getString(j))
                    }

                    fun optInt(key: String): Int? {
                        return if (obj.isNull(key)) null else obj.getInt(key)
                    }

                    val product = Product(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.getString("description"),
                        price_per_sqm = obj.getInt("price_per_sqm"),
                        min_width = optInt("min_width"),
                        max_width = optInt("max_width"),
                        max_panels = optInt("max_panels"),
                        min_height = optInt("min_height"),
                        max_height = optInt("max_height"),
                        imageUrl = obj.getString("imageUrl"),
                        category = obj.getString("category"),
                        variants = variants
                    )

                    products.add(product)
                }

                runOnUiThread {
                    p_items.clear()
                    p_items.addAll(products)
                    ui.myList.adapter?.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
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


            holder.ui.root.setOnClickListener {
                val i = Intent(holder.ui.root.context, MainActivity3::class.java)
                startActivity(i)
                true
            }
        }
    }
}