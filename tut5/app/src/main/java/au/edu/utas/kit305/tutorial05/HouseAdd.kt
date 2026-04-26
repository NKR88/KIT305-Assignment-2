package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.HouseAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class  HouseAdd : AppCompatActivity() {
    private lateinit var ui: HouseAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = HouseAddBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val houseID = intent.getIntExtra(HOUSE_INDEX, -1)
        Log.d("Intent", "Intent ${houseID}")

        if (houseID != -1) {
            val houseObject = h_items[houseID]
            ui.houseEditOwner.setText(houseObject.h_owner)
            ui.houseEditAddress.setText(houseObject.h_address)

            ui.btnHouseAdd.setText("Edit")
            ui.btnHouseAdd.setOnClickListener { view ->
                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val newHouse = House(
                    h_owner = ui.houseEditOwner.text.toString(),
                    h_address = ui.houseEditAddress.text.toString()
                )
                db.collection("houses")
                    .document(houseObject.id)
                    .update(
                        "h_owner", newHouse.h_owner,
                        "h_address", newHouse.h_address
                    )
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "House ID ${houseObject.id} updated")
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error updating House ID", it)
                    }
            }
        }
        else {
            ui.btnHouseAdd.setOnClickListener { view ->
                val db = Firebase.firestore
                Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

                val newHouse = House(
                    h_owner = ui.houseEditOwner.text.toString(),
                    h_address = ui.houseEditAddress.text.toString()
                )
                val housesCollection = db.collection("houses")
                housesCollection
                    .add(newHouse)
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "House created with id ${it.id}")
                        newHouse.id = it.id
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error writing House", it)
                    }
            }
        }
        ui.btnHouseCancel.setOnClickListener { view ->
            finish()
        }
    }
}