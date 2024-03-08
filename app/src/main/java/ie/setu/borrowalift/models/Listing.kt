package ie.setu.borrowalift.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Listing(@get:PropertyName("description") @set:PropertyName("description") var description: String = "",
                   @get:PropertyName("vehicle_type") @set:PropertyName("vehicle_type") var vehicleType: String = "",
                   @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
                   @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimeMs: Long = 0,
                   var user: User? = null) : Parcelable