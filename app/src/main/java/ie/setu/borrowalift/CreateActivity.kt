package ie.setu.borrowalift

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.integrity.i
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ie.setu.borrowalift.models.Listing
import ie.setu.borrowalift.models.User


private const val TAG = "CreateActivity"
private const val PICK_PHOTO_CODE = 1234

private lateinit var imageView: ImageView
private lateinit var btnPickImage: Button
private lateinit var btnSubmit: Button
private lateinit var etDescription: EditText
private lateinit var etVehicleType: EditText
private lateinit var btnLocation: Button
class CreateActivity : AppCompatActivity() {
    private var signedInUser: User? = null
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        val selectedListing = intent.getSerializableExtra("SELECTED_LISTING") as Listing
        storageReference = FirebaseStorage.getInstance().reference
        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }

        imageView = findViewById(R.id.imageView)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnLocation = findViewById(R.id.btnLocation)
        etVehicleType = findViewById(R.id.etVehicleType)
        etDescription = findViewById(R.id.etDescription)

        btnPickImage.setOnClickListener{
            Log.i(TAG, "Open up image picker on Device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }

        registerMapCallback()

        btnLocation.setOnClickListener{
            launchMapActivity()
        }

        btnSubmit.setOnClickListener{
            handleSubmitButtonClick()
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.i(TAG, "Map Loaded")
        }
    }


    private fun handleSubmitButtonClick() {
        if(photoUri == null) {
            Toast.makeText(this, "No Photo Selected", Toast.LENGTH_SHORT).show()
            return
        }
        if(etDescription.text == null) {
            Toast.makeText(this, "Description cannot be null", Toast.LENGTH_SHORT).show()
            return
        }
        if(etVehicleType.text == null) {
            Toast.makeText(this, "Vehicle Type cannot be null", Toast.LENGTH_SHORT).show()
            return
        }
        if(signedInUser == null) {
            Toast.makeText(this, "No signed In user, stop", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        photoReference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.i(TAG, "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                val listing = Listing(
                    etDescription.text.toString(),
                    etVehicleType.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                firestoreDb.collection("listings").add(listing)
            }.addOnCompleteListener { tripCreationTask ->
                btnSubmit.isEnabled = true
                if (!tripCreationTask.isSuccessful) {
                    Log.e(TAG,"Exception during Firebase operations", tripCreationTask.exception)
                    Toast.makeText(this, "Failed to save listing", Toast.LENGTH_SHORT).show()
                }
                etDescription.text.clear()
                etVehicleType.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this,"Successful Listing Posting", Toast.LENGTH_SHORT).show()
                val postIntent = Intent(this, ListingsActivity::class.java)
                startActivity(postIntent)
                finish()
            }
    }

    private fun launchMapActivity() {
        val mapIntent = Intent(this, MapsActivity::class.java)
        // Add any extras or information needed for the map activity
        // mapIntent.putExtra("KEY", "VALUE")
        mapIntentLauncher.launch(mapIntent)
        finish() // Finish the current activity if needed
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode, data)
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                Log.i(TAG, "photoUri $photoUri")
                imageView.setImageURI(photoUri)
            }
        }
        else {
            Toast.makeText(this, "Image picker action cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}