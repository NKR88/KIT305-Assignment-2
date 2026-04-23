package au.edu.utas.kit305.tutorial05

import com.google.firebase.firestore.Exclude

class House (
    @get:Exclude var id : String? = null,

    var h_owner : String? = null,
    var h_address : String? = null,
    )