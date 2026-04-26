package au.edu.utas.kit305.tutorial05

import com.google.firebase.firestore.Exclude

class Space (
    @get:Exclude var id : String = "",

    var s_type : String = "",
    var s_name : String = "",
    var s_width : Int = 0,
    var s_height : Int = 0
)