package au.edu.utas.kit305.tutorial05

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import au.edu.utas.kit305.tutorial05.databinding.SpaceAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class  SpaceAdd : AppCompatActivity() {
    private lateinit var ui: SpaceAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = SpaceAddBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val spaceID = intent.getIntExtra(SPACE_INDEX, -1)
        val houseId = intent.getStringExtra(HOUSE_ID)!! // null checking already handled
        val roomId = intent.getStringExtra(ROOM_ID)!! // null checking already handled

        Log.d("Intent", "Intent ${spaceID}")

        if (spaceID != -1) {
            val spaceObject = s_items[spaceID]
            Log.d("DEBUG", spaceObject.s_width.toString())
            ui.spaceEditName.setText(spaceObject.s_name)
            ui.spaceEditWidth.setText(spaceObject.s_width.toString())
            ui.spaceEditHeight.setText(spaceObject.s_height.toString())

            // i dont think you should be able to edit if its a floor or window doesnt makes any sense
            // so i havent included it
            val items = listOf("${spaceObject.s_type}")
            val adapter = ArrayAdapter(
                this,
                R.layout.simple_spinner_item,
                items
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ui.spaceEditType.adapter = adapter

            Log.d("DEBUG", spaceObject.s_width.toString())

            ui.btnSpaceAdd.setText("Edit")
            ui.btnSpaceAdd.setOnClickListener { view ->
                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val newSpace = Space(
                    s_name = ui.spaceEditName.text.toString(),
                    s_width = ui.spaceEditWidth.text.toString().toIntOrNull() ?: 0,
                    s_height = ui.spaceEditHeight.text.toString().toIntOrNull() ?: 0,
                    s_product = ui.btnSpaceProduct.text.toString(),
                    s_type = ui.spaceEditType.selectedItem.toString()
                )
                db.collection("houses")
                    .document(houseId)
                    .collection("rooms")
                    .document(roomId)
                    .collection("spaces")
                    .document(spaceObject.id)
                    .update(
                        "s_name", newSpace.s_name,
                        "s_width", newSpace.s_width,
                        "s_height", newSpace.s_height
                    )
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Space ID ${spaceObject.id} updated")
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error updating Space ID", it)
                    }
            }
        } else {
            val items = listOf("Select", "Window", "Floor")

            val adapter = ArrayAdapter(
                this,
                R.layout.simple_spinner_item,
                items
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ui.spaceEditType.adapter = adapter

            ui.btnSpaceAdd.setOnClickListener { view ->
                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val name = ui.spaceEditName.text.toString()
                val width = ui.spaceEditWidth.text.toString().toIntOrNull()
                val height = ui.spaceEditHeight.text.toString().toIntOrNull()
                val product = ui.btnSpaceAdd.text.toString()
                val type = ui.spaceEditType.selectedItem.toString()

                Log.d("DEBUG", type)

                if (name == "" ||
                    width == 0 ||
                    height == 0 ||
                    type == "Select"
                ) {
                    androidx.appcompat.app.AlertDialog.Builder(ui.root.context)
                        .setTitle("Invalid")
                        .setMessage("Fill out everything to continue")
                        .setPositiveButton("Ok", null)
                        .show()
                } else {

                    val i = Intent(this.ui.root.context, Product::class.java)
                    i.putExtra(HOUSE_ID, au.edu.utas.kit305.tutorial05.houseId)
                    i.putExtra(ROOM_ID, au.edu.utas.kit305.tutorial05.roomId)
                    i.putExtra(SPACE_ID, space.id)!!
                    startActivity(i)
                    true

                    val newSpace = Space(
                        s_name = ui.spaceEditName.text.toString(),
                        s_width = ui.spaceEditWidth.text.toString().toIntOrNull() ?: 0,
                        s_height = ui.spaceEditHeight.text.toString().toIntOrNull() ?: 0,
                        s_product = ui.btnSpaceProduct.text.toString(),
                        s_type = ui.spaceEditType.selectedItem.toString()
                    )
                    val spacesCollection =
                        db.collection("houses")
                            .document(houseId)
                            .collection("rooms")
                            .document(roomId)
                            .collection("spaces")
                    spacesCollection
                        .add(newSpace)
                        .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Space created with id ${it.id}")
                            newSpace.id = it.id
                            finish()
                        }
                        .addOnFailureListener {
                            Log.e(FIREBASE_TAG, "Error writing Space", it)
                        }
                }
            }
        }
        ui.btnSpaceCancel.setOnClickListener { view -> finish() }
    }
}