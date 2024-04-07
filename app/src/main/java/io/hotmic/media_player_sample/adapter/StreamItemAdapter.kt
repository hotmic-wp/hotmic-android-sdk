package io.hotmic.media_player_sample.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.hotmic.media_player_sample.R
import io.hotmic.player.models.HMStreamBasic
import java.text.SimpleDateFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop

import com.bumptech.glide.request.RequestOptions

class StreamItemAdapter(
    private val dataSet: ArrayList<HMStreamBasic>,
    private val listener: EventListener
) :
    RecyclerView.Adapter<StreamItemAdapter.ViewHolder>() {

    private var context: Context? = null
    private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy")
    private val TIME_FORMAT = SimpleDateFormat("hh:mm a")

    private val mRoundingRadius = (6 * Resources.getSystem().displayMetrics.density).toInt()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clContainer: ConstraintLayout = view.findViewById(R.id.cl_container)
        val tvThumbnail: ImageView = view.findViewById(R.id.thumbnail)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDateTime: TextView = view.findViewById(R.id.tv_date_time)
        val tvState: TextView = view.findViewById(R.id.tv_state)
        val hostAvatar: ImageView = view.findViewById(R.id.host_profile)
        val hostName: TextView = view.findViewById(R.id.host_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stream_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataSet.getOrNull(position)?.let { stream ->
            Glide.with(holder.tvThumbnail.context)
                .load(stream.thumbnail)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(mRoundingRadius)))
                .into(holder.tvThumbnail)

            holder.tvTitle.text = stream.title
            stream.scheduledDate?.let { currDate ->
                val date = DATE_FORMAT.format(currDate)
                val time = TIME_FORMAT.format(currDate)
                context?.let {
                    holder.tvDateTime.text = "$date ${it.getString(R.string.at)} $time"
                }
            }

            // Events
            holder.clContainer.setOnClickListener {
                listener.onItemClicked(dataSet[position])
            }

            holder.tvState.text = stream.state.name
            holder.hostName.text = stream.user?.name ?: ""

            Glide.with(holder.hostAvatar.context)
                .load(stream.user?.profilePic ?: "")
                .circleCrop()
                .into(holder.hostAvatar)
        }

    }

    fun updateList(newDataSet: List<HMStreamBasic>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
    }

    override fun getItemCount() = dataSet.size

    interface EventListener {
        fun onItemClicked(data: HMStreamBasic)
    }
}