package io.hotmic.media_player_sample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.adapter.StreamItemAdapter
import io.hotmic.media_player_sample.databinding.FragmentMainBinding
import io.hotmic.media_player_sample.viewmodel.StreamViewModel
import io.hotmic.media_player_sample.viewmodel.StreamViewModelFactory
import io.hotmic.player.models.HMStreamBasic

class MainFragment : Fragment(), StreamItemAdapter.EventListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var streamViewModel: StreamViewModel
    private lateinit var streamViewModelFactory: StreamViewModelFactory
    private var streamListAdapter: StreamItemAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val rootView = binding.root

        // View Model
        activity?.let {
            streamViewModelFactory = StreamViewModelFactory(it.application)
            streamViewModel =
                ViewModelProvider(this, streamViewModelFactory).get(StreamViewModel::class.java)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe stream list
        streamViewModel.getStreamListLiveData().observe(viewLifecycleOwner, { streamList ->
            streamListAdapter = StreamItemAdapter(streamList, this)
            binding.rvStreamList.adapter = streamListAdapter
            binding.pbDataLoading.visibility = View.GONE
        })
        streamViewModel.retrieveStreamList()
    }

    override fun onItemClicked(data: HMStreamBasic) {
        Toast.makeText(context, "Clicked Id: ${data.id}", Toast.LENGTH_SHORT).show()
    }
}