package ie.setu.borrowalift.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.PropertyName
@Parcelize
data class User(@get:PropertyName("username") @set:PropertyName("username") var username: String = "",
                @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "") : Parcelable