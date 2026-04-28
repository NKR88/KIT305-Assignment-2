package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

const val HOUSE_INDEX = "Movie_Index"
val h_items = mutableListOf<House>()
const val FIREBASE_TAG = "FirebaseLogging"

const val HOUSE_ID:String = "HOUSE_ID"
const val ROOM_ID:String = "ROOM_ID" // i think i delcare it here incase i need it up here???
const val SPACE_ID:String = "SPACE_ID"

class MainActivity : AppCompatActivity() {
    private lateinit var ui: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get all houses
        //loadHouses()

        ui.lblMovieCount.text = "${h_items.size} Houses"
        ui.myList.adapter = MovieAdapter(houses = h_items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)

        ui.btnMenu.setOnClickListener { view ->

            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.uni_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_quote -> {
                        val i = Intent(this, QuoteActivity::class.java)
                        startActivity(i)
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

        // ai used to find the tool correct tool i wrote the rest
        ui.txtSearch.addTextChangedListener { text ->
            val filtered = h_items.filter {
                it.h_address.contains(text.toString(), ignoreCase = true) ||
                it.h_owner.contains(text.toString(), ignoreCase = true)
            }
            (ui.myList.adapter as? MovieAdapter)?.updateList(filtered)
        }
    }

    override fun onResume() {
        super.onResume()
        loadHouses()
    }

    inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class MovieAdapter(private val houses: MutableList<House>) :
        RecyclerView.Adapter<MovieHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MainActivity.MovieHolder {
            val ui = MyListItemBinding.inflate(
                layoutInflater,
                parent,
                false
            )   //inflate a new row from the my_list_item.xml
            return MovieHolder(ui)                                                            //wrap it in a ViewHolder
        }

        private var displayedHouses = houses.toMutableList()
        fun updateList(filtered: List<House>) {
            displayedHouses.clear()
            displayedHouses.addAll(filtered)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return displayedHouses.size
        }

        override fun onBindViewHolder(holder: MainActivity.MovieHolder, position: Int) {
            val house = displayedHouses[position]   //get the data at the requested position
            holder.ui.txtName.text = house.h_address
            holder.ui.txtYear.text = house.h_owner

            if (house.id != null) {

                holder.ui.btnHouseDelete.setOnClickListener {
                    androidx.appcompat.app.AlertDialog.Builder(holder.ui.root.context)
                        .setTitle("Delete")
                        .setMessage("${house.h_address}")
                        .setPositiveButton("Delete") { _, _ ->
                            deleteHouse(house)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                holder.ui.root.setOnClickListener {
                    val i = Intent(holder.ui.root.context, MainActivity2::class.java)
                    i.putExtra(HOUSE_ID, house.id)
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
            } else {
                throw NullPointerException("house.id is null") // i feel like a real dev :')
            }
        }
    }

    // used AI to learn about batch for firebase
    private fun deleteHouse(house: House) {
        val db = Firebase.firestore

        db.collection("houses")
            .document(house.id)
            .collection("rooms")
            .get()
            .addOnSuccessListener { rooms ->

                val batch = db.batch()

                // track how many room/space fetches are still pending
                var pending = rooms.size()

                // edge case: house has no rooms at all
                if (pending == 0) {
                    db.collection("houses").document(house.id).delete()
                        .addOnSuccessListener { loadHouses() }
                    return@addOnSuccessListener
                }

                for (room in rooms.documents) {
                    room.reference
                        .collection("spaces")
                        .get()
                        .addOnSuccessListener { spaces ->

                            for (space in spaces.documents) {
                                batch.delete(space.reference)
                            }
                            batch.delete(room.reference)

                            pending--

                            // only delete the house once ALL rooms are done
                            if (pending == 0) {
                                batch.commit().addOnSuccessListener {
                                    db.collection("houses").document(house.id).delete()
                                        .addOnSuccessListener {
                                            Log.d(FIREBASE_TAG, "House ID ${house.id} deleted")
                                            loadHouses()
                                        }
                                }
                            }
                        }
                }
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
                h_items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all houses ---")
                for (document in result) {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val house = document.toObject<House>()
                    house.id = document.id
                    Log.d(FIREBASE_TAG, house.toString())

                    h_items.add(house)
                }
                (ui.myList.adapter as? MovieAdapter)?.notifyDataSetChanged()
                (ui.myList.adapter as? MovieAdapter)?.updateList(h_items)
                ui.lblMovieCount.text = " ${h_items.size} Houses"
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error retrieving all houses")
            }
    }
}

