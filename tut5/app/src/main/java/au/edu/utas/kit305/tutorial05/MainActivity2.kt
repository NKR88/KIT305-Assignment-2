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
import au.edu.utas.kit305.tutorial05.databinding.RoomItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

const val ROOM_INDEX = "Room_Index"
val r_items = mutableListOf<Room>()

private var houseId : String = "HOUSE_ID"

class MainActivity2 : AppCompatActivity()
{
    private lateinit var ui : ActivityMain2Binding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(ui.root)

        houseId = intent.getStringExtra(HOUSE_ID)!! // put null check in MainActivity

        ui.lblRoomCount.text = "${r_items.size} Rooms"
        ui.myList.adapter = RoomAdapter(rooms = r_items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)

        ui.btnMenu.setOnClickListener { view ->

            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.uni_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_quote -> {

                    }
                    R.id.menu_add -> {
                        val i = Intent(this, RoomAdd::class.java)
                        i.putExtra(HOUSE_ID, houseId)
                        startActivity(i)
                    }
                }
                true
            }
            popup.show()
        }
        //get all Rooms
        loadRooms()
    }

    override fun onResume() {
        super.onResume()
        loadRooms()
    }

    inner class RoomHolder(var ui: RoomItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class RoomAdapter(private val rooms: MutableList<Room>) : RecyclerView.Adapter<RoomHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity2.RoomHolder {
            val ui = RoomItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return RoomHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return rooms.size
        }

        override fun onBindViewHolder(holder: MainActivity2.RoomHolder, position: Int) {
            val room = rooms[position]   //get the data at the requested position
            holder.ui.txtName.text = room.r_name

            if (room.id != null) {

                holder.ui.btnRoomDelete.setOnClickListener {

                    androidx.appcompat.app.AlertDialog.Builder(holder.ui.root.context)
                        .setTitle("Delete")
                        .setMessage("${room.r_name}")
                        .setPositiveButton("Delete") { _, _ ->
                            deleteRoom(room)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                holder.ui.root.setOnClickListener {
                    val i = Intent(holder.ui.root.context, MainActivity3::class.java)
                    i.putExtra(HOUSE_ID, houseId)
                    i.putExtra(ROOM_ID, room.id)!!
                    startActivity(i)
                    true
                }

                //holder.ui.root.setO
                holder.ui.root.setOnLongClickListener {
                    val i = Intent(holder.ui.root.context, RoomAdd::class.java)
                    i.putExtra(ROOM_INDEX, position)
                    i.putExtra(HOUSE_ID, houseId)
                    startActivity(i)
                    true
                }
            }
            else {
                throw NullPointerException("room.id is null") // i feel like a real dev :')

            }
        }
    }
    private fun deleteRoom(room: Room) {
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

        db.collection("houses")
            .document(houseId)
            .collection("rooms")
            .document(room.id)
            .delete()
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Room ID ${room.id} deleted")
                loadRooms()
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error deleting Room ID ${room.id}")
            }
    }
    private fun loadRooms() {
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")
        val roomsCollection = db.collection("houses").document(houseId).collection("rooms")

        ui.lblRoomCount.text = "Loading..."
        roomsCollection
            .get()
            .addOnSuccessListener { result ->
                r_items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all rooms ---")
                for (document in result) {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val room = document.toObject<Room>()
                    room.id = document.id
                    Log.d(FIREBASE_TAG, room.toString())

                    r_items.add(room)
                }
                (ui.myList.adapter as? RoomAdapter)?.notifyDataSetChanged()
                ui.lblRoomCount.text = " ${r_items.size} Rooms"
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error retrieving all rooms")
            }
    }
}

