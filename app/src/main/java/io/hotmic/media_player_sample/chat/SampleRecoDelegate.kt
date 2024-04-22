package io.hotmic.media_player_sample.chat

import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import io.hotmic.media_player_sample.R
import polyadapter.PolyAdapter
import polyadapter.equalityItemCallback

class SampleRecoDelegate() : PolyAdapter.BindingDelegate<SampleReco, SampleRecoViewHolder>,
    PolyAdapter.OnViewRecycledDelegate<SampleRecoViewHolder> {

    override val layoutId = SampleRecoViewHolder.LAYOUT
    override val dataType = SampleReco::class.java

    override val itemCallback = equalityItemCallback<SampleReco> { id }

    override fun createViewHolder(itemView: View) = SampleRecoViewHolder(itemView)

    override fun bindView(holder: SampleRecoViewHolder, item: SampleReco) {
        holder.bindData(item)
    }

    override fun onRecycle(holder: SampleRecoViewHolder) {

    }
}

class SampleRecoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val recoTextView: TextView = view.findViewById(R.id.reco_text)

    fun bindData(data: SampleReco) {
        recoTextView.text = "Recommend\n${data.id}"

    }


    companion object {
        @LayoutRes
        val LAYOUT = R.layout.sample_reco_delegate
    }
}

data class SampleReco(
    val id: Int
)