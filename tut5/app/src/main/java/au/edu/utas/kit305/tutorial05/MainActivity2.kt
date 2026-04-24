package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMain2Binding
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

const val HOUSE_INDEX = "Movie_Index"
val items = mutableListOf<House>()
const val FIREBASE_TAG = "FirebaseLogging"

class MainActivity2 : AppCompatActivity()
{
    private lateinit var ui : ActivityMain2Binding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.lblMovieCount.text = "${items.size} Houses"
        ui.myList.adapter = MovieAdapter(houses = items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)

        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

        ui.btnMenu.setOnClickListener { view ->

            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.house_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_quote -> {

                    }
                    R.id.menu_add -> {
                        val i = Intent(this, HouseAdd::class.java)
                        startActivity(i)
                    }
                }
                true
            }
            popup.show()
        }
        //get all movies
        loadHouses()
    }

    override fun onResume() {
        super.onResume()
        loadHouses()
    }

    inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class MovieAdapter(private val houses: MutableList<House>) : RecyclerView.Adapter<MovieHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.MovieHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return MovieHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return houses.size
        }

        override fun onBindViewHolder(holder: MainActivity.MovieHolder, position: Int) {
            val house = houses[position]   //get the data at the requested position
            holder.ui.txtName.text = house.h_address
            holder.ui.txtYear.text = house.h_owner

            holder.ui.btnHouseDelete.setOnClickListener {
                deleteHouse(house)
                loadHouses()
            }

            holder.ui.root.setOnClickListener {
                val i = Intent(holder.ui.root.context, MovieDetails::class.java)
                i.putExtra(HOUSE_INDEX, position)
                startActivity(i)
                true
            }

            //holder.ui.root.setO
            holder.ui.root.setOnLongClickListener {
                val i = Intent(holder.ui.root.context, HouseAdd::class.java)
                i.putExtra(HOUSE_INDEX, position)
                startActivity(i)
                true
            }
        }
    }
    private fun deleteHouse(house: House) {
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

        db.collection("houses")
            .document(house.id)
            .delete()
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "House ID ${house.id} deleted")
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error deleting House ID ${house.id}")
            }
    }
    private fun loadHouses() {
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")
        val housesCollection = db.collection("houses")

        ui.lblMovieCount.text = "Loading..."
        housesCollection
            .get()
            .addOnSuccessListener { result ->
                items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all houses ---")
                for (document in result) {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val house = document.toObject<House>()
                    house.id = document.id
                    Log.d(FIREBASE_TAG, house.toString())

                    items.add(house)
                }
                (ui.myList.adapter as? MovieAdapter)?.notifyDataSetChanged()
                ui.lblMovieCount.text = " ${items.size} Houses"
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error retrieving all houses")
            }
    }
}

