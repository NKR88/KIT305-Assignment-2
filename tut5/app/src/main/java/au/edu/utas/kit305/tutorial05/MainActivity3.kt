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
import au.edu.utas.kit305.tutorial05.databinding.SpaceItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

const val SPACE_INDEX = "Space_Index"
val s_items = mutableListOf<Space>()

private var houseId : String = "HOUSE_ID"
private var roomId : String = "ROOM_ID"

class MainActivity3 : AppCompatActivity()
{
    private lateinit var ui : ActivityMain2Binding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(ui.root)

        houseId = intent.getStringExtra(HOUSE_ID)!! // put null check in MainActivity
        roomId = intent.getStringExtra(ROOM_ID)!! // put null check in MainActivity


        ui.lblSpaceCount.text = "${s_items.size} Spaces"
        ui.myList.adapter = SpaceAdapter(spaces = s_items)

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
                        val i = Intent(this, SpaceAdd::class.java)
                        i.putExtra(HOUSE_ID, houseId)
                        startActivity(i)
                    }
                }
                true
            }
            popup.show()
        }
        //get all Spaces
        loadSpaces()
    }

    override fun onResume() {
        super.onResume()
        loadSpaces()
    }

    inner class SpaceHolder(var ui: SpaceItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class SpaceAdapter(private val spaces: MutableList<Space>) : RecyclerView.Adapter<SpaceHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity2.SpaceHolder {
            val ui = SpaceItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return SpaceHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return spaces.size
        }

        override fun onBindViewHolder(holder: MainActivity2.SpaceHolder, position: Int) {
            val space = spaces[position]   //get the data at the requested position
            holder.ui.txtName.text = space.s_name

            if (space.id != null) {

                holder.ui.btnSpaceDelete.setOnClickListener {

                    androidx.appcompat.app.AlertDialog.Builder(holder.ui.root.context)
                        .setTitle("Delete")
                        .setMessage("${space.s_name}")
                        .setPositiveButton("Delete") { _, _ ->
                            deleteSpace(space)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                holder.ui.root.setOnClickListener {
                    val i = Intent(holder.ui.root.context, MainActivity2::class.java)
                    i.putExtra(HOUSE_ID, houseId)
                    i.putExtra(ROOM_ID, space.id)!!
                    startActivity(i)
                    true
                }

                //holder.ui.root.setO
                holder.ui.root.setOnLongClickListener {
                    val i = Intent(holder.ui.root.context, SpaceAdd::class.java)
                    i.putExtra(ROOM_INDEX, position)
                    i.putExtra(HOUSE_ID, houseId)
                    startActivity(i)
                    true
                }
            }
            else {
                throw NullPointerException("space.id is null") // i feel like a real dev :')

            }
        }
    }
    private fun deleteSpace(space: Space) {
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

        db.collection("houses")
            .document(houseId)
            .collection("spaces")
            .document(space.id)
            .delete()
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Space ID ${space.id} deleted")
                loadSpaces()
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error deleting Space ID ${space.id}")
            }
    }
    private fun loadSpaces() {
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")
        val spacesCollection = db.collection("houses").document(houseId).collection("spaces")

        ui.lblSpaceCount.text = "Loading..."
        spacesCollection
            .get()
            .addOnSuccessListener { result ->
                s_items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all spaces ---")
                for (document in result) {
                    //Log.d(FIREBASE_TAG, document.toString())
                    val space = document.toObject<Space>()
                    space.id = document.id
                    Log.d(FIREBASE_TAG, space.toString())

                    s_items.add(space)
                }
                (ui.myList.adapter as? SpaceAdapter)?.notifyDataSetChanged()
                ui.lblSpaceCount.text = " ${s_items.size} Spaces"
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error retrieving all spaces")
            }
    }
}

