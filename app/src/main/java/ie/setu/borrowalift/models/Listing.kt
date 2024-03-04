package ie.setu.borrowalift.models

import com.google.firebase.firestore.PropertyName

data class Listing(@get:PropertyName("description") @set:PropertyName("dscription") var description: String = "",
                   @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
                   @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimes: Long = 0,
                @get:PropertyName("vehicle_type") @set:PropertyName("vehicle_type") var vehicleType: String = "",
                   var user: User? = null)