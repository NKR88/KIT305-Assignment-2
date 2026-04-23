package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.HouseAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class  HouseAdd : AppCompatActivity() {
    private lateinit var ui : HouseAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = HouseAddBinding.inflate(layoutInflater)
        setContentView(ui.root)

//        //TODO: read in movie details and display on this screen
//        //get movie object using id from intent
//        val movieID = intent.getIntExtra(MOVIE_INDEX, -1)
//        val movieObject = items[movieID]
//        //TODO: you'll need to set txtTitle, txtYear, txtDuration yourself
//        ui.txtTitle.setText(movieObject.title)
//        ui.txtYear.setText(movieObject.year.toString())
//        ui.txtDuration.setText(movieObject.duration.toString())
//
//        val db = Firebase.firestore
//        val moviesCollection = db.collection("movies")
//
//        ui.btnSave.setOnClickListener {
//            //get the user input
//            movieObject.title = ui.txtTitle.text.toString()
//            movieObject.year = ui.txtYear.text.toString().toInt() //good code would check this is really an int
//            movieObject.duration = ui.txtDuration.text.toString().toFloat() //good code would check this is really a float
//
//            //update the database
//            moviesCollection.document(movieObject.id!!)
//                .set(movieObject)
//                .addOnSuccessListener {
//                    Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
//                    //return to the list
//                    finish()
//                }
//        }

    }
}