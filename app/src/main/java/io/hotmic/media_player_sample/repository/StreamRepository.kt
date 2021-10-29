package io.hotmic.media_player_sample.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hotmic.player.HotMicPlayer
import io.hotmic.player.models.HMStreamBasic
import io.reactivex.schedulers.Schedulers

class StreamRepository(private val context: Context) {

    private val API_KEY = "d34c0ae5-185b-4c3b-a55c-2d7ef9ebf5b6"

    // Live Data
    private var streamListLiveData = MutableLiveData<List<HMStreamBasic>>()

    fun getStreamListLiveData(): LiveData<List<HMStreamBasic>> {
        return streamListLiveData
    }

    fun retrieveStreamList() {

        HotMicPlayer.getStreams(context, API_KEY)?.let { api ->
            api.subscribeOn(Schedulers.io()).subscribe({ sList ->
                Toast.makeText(context, "${sList.size} streams are fetched.", Toast.LENGTH_SHORT).show()
                streamListLiveData.postValue(sList)
            }, { e ->
                Toast.makeText(context, "Fetching stream error occurred: $e", Toast.LENGTH_SHORT).show()
            })
        }
    }

}