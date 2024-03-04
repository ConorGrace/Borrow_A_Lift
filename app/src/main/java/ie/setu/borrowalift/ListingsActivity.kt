package ie.setu.borrowalift

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ie.setu.borrowalift.models.Listing
import ie.setu.borrowalift.models.User

private const val TAG = "PostsActivity"
private const val EXTRA_USERNAME = "EXTRA_USERNAME"

class ListingsActivity : AppCompatActivity() {

        private var signedInUser: User? = null
        private lateinit var firestoreDb: FirebaseFirestore
        private lateinit var listings:MutableList<Listing>
        private lateinit var adapter: ListingsAdapter
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_listings)
            val rvPosts: RecyclerView = findViewById(R.id.rvPosts)

            listings = mutableListOf()
            adapter = ListingsAdapter(this, listings)

            rvPosts.adapter = adapter
            rvPosts.layoutManager = LinearLayoutManager(this)
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

            var postsReference = firestoreDb.collection("listings").limit(20)
                .orderBy("creation_time_ms", Query.Direction.DESCENDING)

            val username = intent.getStringExtra(EXTRA_USERNAME)
            if (username != null) {
                supportActionBar?.title = username
                postsReference = postsReference.whereEqualTo("user.username", username)
            }
            postsReference.addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e(TAG, "Exception when querying trips", exception)
                    return@addSnapshotListener
                }
                val listingList = snapshot.toObjects(Listing::class.java)
                listings.clear()
                listings.addAll(listingList)
                adapter.notifyDataSetChanged()
                for (listing in listingList) {
                    Log.i(TAG, "Document ${listing}")
                }
            }
            val fabCreate: FloatingActionButton = findViewById(R.id.fabCreate)

            fabCreate.setOnClickListener {
                val intent = Intent(this, CreateActivity::class.java)
                startActivity(intent)
            }
        }
    }