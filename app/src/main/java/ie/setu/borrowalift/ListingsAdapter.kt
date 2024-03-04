package ie.setu.borrowalift

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ie.setu.borrowalift.models.Listing


class ListingsAdapter (val context: Context, val listings:List<Listing>) :
    RecyclerView.Adapter<ListingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listings[position])
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivPost: ImageView = itemView.findViewById(R.id.ivPost)
        val tvRelativeTime: TextView = itemView.findViewById(R.id.tvRelativeTime)

        fun bind(listing: Listing) {
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
        }
    }

}