package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.RoomAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class  RoomAdd : AppCompatActivity() {
    private lateinit var ui: RoomAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = RoomAddBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val roomID = intent.getIntExtra(ROOM_INDEX, -1)
        val houseId = intent.getStringExtra(HOUSE_ID)!! // null checking already handled
        Log.d("Intent", "Intent ${roomID}")

        if (roomID != -1) {
            val roomObject = r_items[roomID]
            ui.roomEditName.setText(roomObject.r_name)

            ui.btnRoomAdd.setText("Edit")
            ui.btnRoomAdd.setOnClickListener { view ->
                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val newRoom = Room(
                    r_name = ui.roomEditName.text.toString(),
                )
                db.collection("houses")
                    .document(houseId)
                    .collection("rooms")
                    .document(roomObject.id)
                    .update(
                        "r_name", newRoom.r_name
                    )
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Room ID ${roomObject.id} updated")
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error updating Room ID", it)
                    }
            }
        }
        else {
            ui.btnRoomAdd.setOnClickListener { view ->
                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val newRoom = Room(
                    r_name = ui.roomEditName.text.toString()
                )
                val roomsCollection = db.collection("houses").document(houseId).collection("rooms")
                roomsCollection
                    .add(newRoom)
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Room created with id ${it.id}")
                        newRoom.id = it.id
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error writing Room", it)
                    }
            }
        }
        ui.btnRoomCancel.setOnClickListener { view ->
            finish()
        }
    }
}