package au.edu.utas.kit305.tutorial05

import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ProductApi {

    fun fetchProducts(type : String): List<Product> {
        val products = mutableListOf<Product>()

        try {

            var url = URL("https://utasbot.dev/kit305_2026/product")

            if (type == "Window") {
                url = URL("https://utasbot.dev/kit305_2026/product?category=window")

            } else if (type == "Floor") {
                url = URL("https://utasbot.dev/kit305_2026/product?category=floor")

            }

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().readText()
            Log.d("API", response)

            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")

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

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return products
    }
}