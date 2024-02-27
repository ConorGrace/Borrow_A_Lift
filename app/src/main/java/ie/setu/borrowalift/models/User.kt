package ie.setu.borrowalift.models

import com.google.firebase.firestore.PropertyName

data class User(@get:PropertyName("username") @set:PropertyName("username") var username: String = "",
                @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "")