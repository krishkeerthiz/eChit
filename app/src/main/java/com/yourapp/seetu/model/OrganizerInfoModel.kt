package com.yourapp.seetu.model

import java.io.Serializable

class OrganizerInfoModel : Serializable {
    var name : String? = null
    var area : String? = null

    // Empty constructor needed for Firestore serialization
    constructor()

    constructor(name : String?, area : String?){
        this.name = name
        this.area = area
    }
}