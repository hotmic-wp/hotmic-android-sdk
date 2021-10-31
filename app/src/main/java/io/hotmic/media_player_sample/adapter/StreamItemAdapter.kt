package io.hotmic.media_player_sample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.hotmic.media_player_sample.R
import io.hotmic.player.models.HMStreamBasic
import java.text.SimpleDateFormat

class StreamItemAdapter(
    private val dataSet: ArrayList<HMStreamBasic>,
    private val listener: EventListener
) :
    RecyclerView.Adapter<StreamItemAdapter.ViewHolder>() {

    private var context: Context? = null
    private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy")
    private val TIME_FORMAT = SimpleDateFormat("hh:mm a")

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clContainer: ConstraintLayout = view.findViewById(R.id.cl_container)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDateTime: TextView = view.findViewById(R.id.tv_date_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stream_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = dataSet[position].title
        dataSet[position].scheduledDate?.let { currDate ->
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