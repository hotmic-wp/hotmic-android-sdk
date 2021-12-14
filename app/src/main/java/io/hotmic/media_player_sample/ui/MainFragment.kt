package io.hotmic.media_player_sample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.hotmic.media_player_sample.MainActivity
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

            if (streamListAdapter == null) {
                streamListAdapter = StreamItemAdapter(ArrayList(streamList), this)
            } else {
                streamListAdapter?.let {
                    it.updateList(streamList)
                    it.notifyDataSetChanged()
                }
            }
            binding.rvStreamList.adapter = streamListAdapter
            binding.pbDataLoading.visibility = View.GONE
        })
    }

    override fun onResume() {
        super.onResume()
        streamViewModel.retrieveStreamList()
    }

    override fun onDestroy() {
        streamListAdapter = null
        super.onDestroy()
    }

    override fun onItemClicked(stream: HMStreamBasic) {
        activity?.let {
            val mActivity = activity as MainActivity
            mActivity.onStreamClicked(stream)
        }
    }
}