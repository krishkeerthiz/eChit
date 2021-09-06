package com.yourapp.seetu.model

import java.io.Serializable

class OrganizerModel : Serializable {
    var accountNumber : String? = null
    var address : String? = null
    var area : String? = null
    var bankName : String? = null
    var branch : String? = null
    var ifsc : String? = null
    var name : String? = null
    var phone : String? = null

    // Empty constructor needed for Firestore serialization
    constructor()

    constructor(accountNumber : String?, address : String?, area : String?, bankName : String?, branch : String?, ifsc : String?,
    name : String?, phone : String?){
        this.accountNumber = accountNumber
        this.address = address
        this.area = area
        this.bankName = bankName
        this.branch = branch
        this.ifsc = ifsc
        this.name = name
        this.phone = phone
    }

}