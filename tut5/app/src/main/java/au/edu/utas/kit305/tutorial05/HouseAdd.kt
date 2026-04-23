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

        ui.btnHouseAdd.setOnClickListener { view ->
            //add some data (comment this out after running the program once and confirming your data is there)
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
                    Log.d(FIREBASE_TAG, "Document created with id ${it.id}")
                    newHouse.id = it.id
                }
                .addOnFailureListener {
                    Log.e(FIREBASE_TAG, "Error writing document", it)
                }
            finish()
        }
        ui.btnHouseCancel.setOnClickListener { view ->
            finish()
        }
    }
}