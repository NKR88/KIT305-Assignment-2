package au.edu.utas.kit305.tutorial05

import com.google.firebase.firestore.Exclude

class Room (
    @get:Exclude var id : String = "",

    var r_name : String = "",
)