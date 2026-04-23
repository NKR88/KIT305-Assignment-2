package au.edu.utas.kit305.tutorial05

import com.google.firebase.firestore.Exclude

class House (
    @get:Exclude var id : String = "",

    var h_owner : String = "",
    var h_address : String = "",
    )