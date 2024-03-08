package ie.setu.borrowalift

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ie.setu.borrowalift.databinding.ItemPostBinding
import ie.setu.borrowalift.models.Listing

interface ListListener {
    fun onListingClick(listing: Listing)
    fun onListingLongClick(listing: Listing)
}

class ListingsAdapter(
    val context: Context,
    val listings: List<Listing>,
    val listener: ListListener? = null
) :
    RecyclerView.Adapter<ListingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = listings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = listings[holder.adapterPosition]
        holder.bind(listing, listener)
    }


    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvUsername: TextView = binding.tvUsername
        val tvDescription: TextView = binding.tvDescription
        val ivPost: ImageView = binding.ivPost
        val tvRelativeTime: TextView = binding.tvRelativeTime

        init {
            // Long-click listener setup
            binding.root.setOnLongClickListener {
                listener?.onListingLongClick(listings[adapterPosition])
                true // Return true to indicate that the long click is handled
            }
        }

        fun bind(listing: Listing, listener: ListListener?) {
            tvUsername.text = listing.user?.username
            tvDescription.text = listing.description
            Glide.with(context).load(listing.imageUrl).into(ivPost)
            tvRelativeTime.text = DateUtils.getRelativeDateTimeString(
                context,
                listing.creationTimeMs,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            )

            binding.root.setOnClickListener {
                listener?.onListingClick(listing)
            }
        }
    }
    }
